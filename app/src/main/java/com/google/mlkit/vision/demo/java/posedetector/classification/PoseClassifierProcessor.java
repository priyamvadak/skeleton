/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.posedetector.classification;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Accepts a stream of {@link Pose} for classification and Rep counting.
 */
public class PoseClassifierProcessor {
  private static final String TAG = "PoseClassifierProcessor";
  private static final String POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv";

  // Specify classes for which we want rep counting.
  // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
  // for your pose samples.
  private static final String PUSHUPS_CLASS = "pushups_down";
  private static final String SQUATS_CLASS = "squats_down";
  private static final String[] POSE_CLASSES = {
          PUSHUPS_CLASS, SQUATS_CLASS
  };

  private final boolean isStreamMode;

  private EMASmoothing emaSmoothing;
  private List<RepetitionCounter> repCounters;
  private PoseClassifier poseClassifier;
  private String lastRepResult;
  private Context context;


  @WorkerThread
  public PoseClassifierProcessor(Context context, boolean isStreamMode) {
    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    this.isStreamMode = isStreamMode;
    this.context = context;
    if (isStreamMode) {
      emaSmoothing = new EMASmoothing();
      repCounters = new ArrayList<>();
      lastRepResult = "";
    }
    loadPoseSamples(context);
  }

  private void loadPoseSamples(Context context) {
    List<PoseSample> poseSamples = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(
              new InputStreamReader(context.getAssets().open(POSE_SAMPLES_FILE)));
      String csvLine = reader.readLine();
      while (csvLine != null) {
        // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if (poseSample != null) {
          poseSamples.add(poseSample);
        }
        csvLine = reader.readLine();
      }
    } catch (IOException e) {
      Log.e(TAG, "Error when loading pose samples.\n" + e);
    }
    poseClassifier = new PoseClassifier(poseSamples);
    if (isStreamMode) {
      for (String className : POSE_CLASSES) {
        repCounters.add(new RepetitionCounter(className));
      }
    }
  }

  /**
   * Given a new {@link Pose} input, returns a list of formatted {@link String}s with Pose
   * classification results.
   *
   * <p>Currently it returns up to 2 strings as following:
   * 0: PoseClass : X reps
   * 1: PoseClass : [0.0-1.0] confidence
   */
  @WorkerThread
  public List<String> getPoseResult(Pose pose) {
    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    List<String> result = new ArrayList<>();
    ClassificationResult classification = poseClassifier.classify(pose);

    // Update {@link RepetitionCounter}s if {@code isStreamMode}.
    if (isStreamMode) {
      // Feed pose to smoothing even if no pose found.
      classification = emaSmoothing.getSmoothedResult(classification);

      // Return early without updating repCounter if no pose found.
      if (pose.getAllPoseLandmarks().isEmpty()) {
        result.add(lastRepResult);
        return result;
      }

      for (RepetitionCounter repCounter : repCounters) {
        int repsBefore = repCounter.getNumRepeats();
        int repsAfter = repCounter.addClassificationResult(classification);
        if (repsAfter > repsBefore) {

          SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putInt("EXERCISE_COUNT", repsAfter);
          editor.commit();

          // Play a fun beep when rep counter updates.
          ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
          tg.startTone(ToneGenerator.TONE_PROP_BEEP);
          lastRepResult = String.format(
                  Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter);
          break;
        }
      }
      result.add(lastRepResult);
    }

    // Add maxConfidence class of current frame to result if pose is found.
    if (!pose.getAllPoseLandmarks().isEmpty()) {
      String maxConfidenceClass = classification.getMaxConfidenceClass();
      String maxConfidenceClassResult = String.format(
              Locale.US,
              "%s : %.2f confidence",
              maxConfidenceClass,
              classification.getClassConfidence(maxConfidenceClass)
                      / poseClassifier.confidenceRange());
      result.add(maxConfidenceClassResult);
    }

    return result;
  }

  public static boolean checkPushupPosture(Pose pose){
    PointF shoulderPos = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition();
    PointF hipPos = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition();
    PointF heelPos = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL).getPosition();
    PointF wristPos = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition();
    PointF kneePos = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition();
    PointF elbowPos = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition();

    return checkAligned(hipPos,heelPos,kneePos,elbowPos,wristPos,shoulderPos,pose);


  }
  static boolean isStraightLinePossible(
          ArrayList<PointF> arr, int n)
  {

    // First pair of point (x0, y0)
    float x0 = arr.get(0).x;
    float y0 = arr.get(0).y;

    // Second pair of point (x1, y1)
    float x1 = arr.get(1).x;
    float y1 = arr.get(1).y;

    float dx = x1 - x0, dy = y1 - y0;

    // Loop to iterate over the points
    for(int i = 0; i < n; i++)
    {
      float x = arr.get(i).x;
      float y = arr.get(i).y;
      if (dx * (y - y1) != dy * (x - x1))
      {
        System.out.println("NO");
        return false;
      }
    }
    System.out.println("YES");
    return true;
  }
  //TODO maybe this needs to be a bool functionhipPos

  public static boolean checkAligned(PointF hipPos, PointF heelPos, PointF kneePos, PointF elbowPos,PointF wristPos,PointF shoulderPos,Pose pose){

    Log.d("tag1","3");
    ArrayList<PointF> arr = new ArrayList<>();
    arr.add(hipPos);
    arr.add(heelPos);
    arr.add(kneePos);
    arr.add(shoulderPos);
    if(isStraightLinePossible(arr,arr.size())){
      System.out.println("THE BODY IS ALIGNED");
      return true;
    }
    else {
      //PoseGraphic.drawCorrectLinePushup();
      System.out.println("THE BODY IS NOT ALIGNED");
      return false;
    }
    /*
    ArrayList<PointF> arr2 = new ArrayList<>();
    arr2.add(wristPos);
    arr2.add(shoulderPos);
    if(isStraightLinePossible(arr2,arr2.size())){
      System.out.println("THE ARMS ARE ALIGNED");
    }
    else{
      System.out.println("THE ARMS ARE NOT ALIGNED");
    }
     */

  }

  public static void checkSquatPosture(Pose pose){

  }

}