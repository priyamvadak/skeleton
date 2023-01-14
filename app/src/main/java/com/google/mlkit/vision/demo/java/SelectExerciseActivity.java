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

package com.google.mlkit.vision.demo.java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.demo.BuildConfig;
import com.google.mlkit.vision.demo.R;

public final class SelectExerciseActivity extends AppCompatActivity {

  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
          new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
      StrictMode.setVmPolicy(
          new StrictMode.VmPolicy.Builder()
              .detectLeakedSqlLiteObjects()
              .detectLeakedClosableObjects()
              .penaltyLog()
              .build());
    }
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_select_exercise);

    //initialise shared preference
    Context context = getApplicationContext();
    sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
    editor = sharedPreferences.edit();

    Button textview_pushups = findViewById(R.id.textview_pushups);
    textview_pushups.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        saveSelectedExercise(0);
      }
    });
    Button textview_squats = findViewById(R.id.textview_squats);
    textview_squats.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        saveSelectedExercise(1);
      }
    });
  }

  private void saveSelectedExercise(int exercise) {
    //0 - pushups, 1 - squats (save to shared preference)
    editor.putInt("SELECTED_EXERCISE", exercise);
    editor.commit();
    startActivity(new Intent(this, LivePreviewActivity.class));
  }

}
