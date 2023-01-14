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

package com.google.mlkit.vision.demo.java.posedetector;

import static java.lang.Math.abs;
import static java.lang.Math.atan;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;

/** Draw the detected pose in preview. */
public class PoseGraphic extends Graphic {
  static Canvas mycanvas;
  //static Paint myleftPaint;
  private static final float DOT_RADIUS = 8.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 10.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;

  private final Pose pose;
  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  private final List<String> poseClassification;
  private final Paint classificationTextPaint;
  /*private final Paint leftPaint;
  private final Paint rightPaint;*/
  private final Paint whitePaint;
  private final Paint greenPaint;

  private PoseLandmark rightShoulder, leftShoulder, rightHip, leftHip,
          rightKnee, leftKnee, rightAnkle, leftAnkle, rightWrist, leftWrist;
  private boolean isFacingRightSide, isPushupSelected;

  private static int counterPushup, counterSquat;
  private static boolean pushupBottomReached;

  PoseGraphic(
          GraphicOverlay overlay,
          Pose pose,
          boolean showInFrameLikelihood,
          boolean visualizeZ,
          boolean rescaleZForVisualization,
          List<String> poseClassification) {
    super(overlay);
    this.pose = pose;
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;

    this.poseClassification = poseClassification;
    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

    greenPaint = new Paint();
    greenPaint.setStrokeWidth(STROKE_WIDTH);
    greenPaint.setColor(Color.GREEN);
    greenPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

  }

  @Override
  public void draw(Canvas canvas) {
    mycanvas = canvas;
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    if (landmarks.isEmpty()) {
      return;
    }
    getPoseLandmarks();
    // Draw pose classification text.
    float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;
    for (int i = 0; i < poseClassification.size(); i++) {
      float classificationY =
              (canvas.getHeight()
                      - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size() - i));
      canvas.drawText(
              poseClassification.get(i), classificationX, classificationY, classificationTextPaint);
    }


    //check body direction, negative z values are near the camera
    isFacingRightSide = leftShoulder.getPosition3D().getZ() < 0;
    //get selected exercise from shared preference
    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
    int exercise = sharedPreferences.getInt("SELECTED_EXERCISE",0);
    isPushupSelected = exercise==0?true:false;  //0 - pushups, 1 - squats
    displayCorrectPosture(isFacingRightSide);


    

  }

  private void displayCorrectPosture(boolean isFacingRightSide) {
    if(isPushupSelected){ //pushup
      displayPushupPoints(isFacingRightSide);
      if (isFacingRightSide){ //facing right side pushup
        //correct posture (left side)
        drawLine(mycanvas, leftShoulder, leftAnkle, greenPaint);

        int angle = (int)Math.toDegrees(abs(atan((leftShoulder.getPosition3D().getY() - leftWrist.getPosition3D().getY())/(leftWrist.getPosition3D().getX() - leftAnkle.getPosition3D().getX()))));
        if(angle>20){
          //connect shoulder to wrist only when body is atleast at an angle of 20deg
          drawLine(mycanvas, leftShoulder, leftWrist, whitePaint);
          if(pushupBottomReached){
            counterPushup++;
            saveCounter(counterPushup);
          }
          pushupBottomReached = false;
        }
        if(angle<12){
          pushupBottomReached= true;
        }
        //current posture
        drawLine(mycanvas, leftShoulder, leftHip, whitePaint);
        drawLine(mycanvas, leftHip, leftKnee, whitePaint);
        drawLine(mycanvas, leftKnee, leftAnkle, whitePaint);
      }
      else{ //facing left side pushup todo
        //drawLine(mycanvas, rightShoulder, rightAnkle, greenPaint);
      }
    }
    else{ //squat todo
      displaySquatPoints(isFacingRightSide);
      if(isFacingRightSide){
        //facing right side squat

      }
      else{
        //facing left side squat

      }
    }
  }

  private void saveCounter(int count) {
    /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt("EXERCISE_COUNT", count);
    editor.commit();*/
  }

  private void displaySquatPoints(boolean isFacingRightSide) {
    if(isFacingRightSide){
      //left ear, shoulder, hip, knee, toe
      drawPoint(mycanvas,pose.getPoseLandmark(PoseLandmark.LEFT_EAR), whitePaint);
      drawPoint(mycanvas, leftShoulder, whitePaint);
      drawPoint(mycanvas, leftHip, whitePaint);
      drawPoint(mycanvas, leftKnee, whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX), whitePaint);
    }
    else{
      //right
      drawPoint(mycanvas,pose.getPoseLandmark(PoseLandmark.RIGHT_EAR), whitePaint);
      drawPoint(mycanvas, rightShoulder, whitePaint);
      drawPoint(mycanvas, rightHip, whitePaint);
      drawPoint(mycanvas, rightKnee, whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX), whitePaint);
    }
  }

  private void displayPushupPoints(boolean isFacingRightSide) {
    if(isFacingRightSide){
      //left shoulder, hip, knee, ankle, elbow, wrist
      drawPoint(mycanvas, leftShoulder, whitePaint);
      drawPoint(mycanvas, leftHip, whitePaint);
      drawPoint(mycanvas, leftKnee, whitePaint);
      drawPoint(mycanvas, leftAnkle, whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW), whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.LEFT_WRIST), whitePaint);
    }
    else{
      //right
      drawPoint(mycanvas, rightShoulder, whitePaint);
      drawPoint(mycanvas, rightHip, whitePaint);
      drawPoint(mycanvas, rightKnee, whitePaint);
      drawPoint(mycanvas, rightAnkle, whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW), whitePaint);
      drawPoint(mycanvas, pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST), whitePaint);
    }
  }

  private void getPoseLandmarks() {
    rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
    leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
    rightHip= pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
    leftHip= pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
    rightKnee= pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
    leftKnee= pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
    rightAnkle= pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
    leftAnkle= pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
    leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
    rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
  }

  void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    //updatePaintColorByZValue(paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;
    //updatePaintColorByZValue(paint, canvas, visualizeZ, rescaleZForVisualization, avgZInImagePixel, zMin, zMax);

    canvas.drawLine(
            translateX(start.getX()),
            translateY(start.getY()),
            translateX(end.getX()),
            translateY(end.getY()),
            paint);
  }

}