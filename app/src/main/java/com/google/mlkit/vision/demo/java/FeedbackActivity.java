package com.google.mlkit.vision.demo.java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.posedetector.PoseGraphic;
import com.google.mlkit.vision.pose.Pose;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;

public class FeedbackActivity  extends AppCompatActivity {

    private Button button_screenshot;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView workoutScore,textview_count, textview_exercise, textview_fb_back, textview_fb_legs, textview_1,textview_fb_backangle,textview_fb_legsangle;

    private ImageView image_exercise_back, image_exercise_legs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_activity);

        //initialise shared preference
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textview_count = findViewById(R.id.textview_count);
        textview_exercise = findViewById(R.id.textview_exercise);
        image_exercise_back = findViewById(R.id.image_exercise_back);
        image_exercise_legs = findViewById(R.id.image_exercise_legs);
        textview_fb_back = findViewById(R.id.textview_fb_back);
        textview_fb_legs = findViewById(R.id.textview_fb_legs);
        textview_fb_backangle = findViewById(R.id.textview_fb_backangle);
        textview_fb_legsangle = findViewById(R.id.textview_fb_legsangle);
        textview_1 = findViewById(R.id.textview_1);
        workoutScore = findViewById(R.id.workoutScore);
        setCount();

        button_screenshot = findViewById(R.id.button_screenshot);
        button_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Took screenshot!");
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void setCount() {
        // read from shared preference
        int count = sharedPreferences.getInt("EXERCISE_COUNT",0);
        textview_count.setText("Exercise count: " + count);

        int exercise = sharedPreferences.getInt("SELECTED_EXERCISE",0);
        //0 - pushups, 1 - squats
        String e = exercise==0?"PUSHUPS":"SQUATS";
        textview_exercise.setText("Workout Selected: " + e);
        System.out.println("Mistakes " + PoseGraphic.mistakes);
        System.out.println("Checks " + PoseGraphic.checks);
        workoutScore.setText("Total score: " + String.valueOf(PoseGraphic.mistakes)+ "/ " + PoseGraphic.checks);

        if(exercise==0){
            //show image, give text feedback
            //TODO load screenshot from storage
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "screenshot_spine" + ".jpg");
            File file2 = new File(directory, "screenshot_arms" + ".jpg");
            image_exercise_back.setImageDrawable(Drawable.createFromPath(file.toString()));
            image_exercise_legs.setImageDrawable(Drawable.createFromPath(file2.toString()));
            // image_exercise_back.setImageResource(R.drawable.image_pushup);
            // image_exercise_legs.setImageResource(R.drawable.image_pushup);
            textview_fb_backangle.setText("Spine angle: " + (int)PoseGraphic.wrongPushupSpineAngle + "deg.");
            textview_fb_legsangle.setText("Shoulder angle: " + (int)PoseGraphic.wrongPushupArmsAngle + "deg.");
            textview_fb_back.setText(R.string.fb_pushup_back);
            textview_fb_legs.setText(R.string.fb_pushup_legs);
            textview_1.setText("Shoulder");
        }
        else{
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "screenshot_squat" + ".jpg");
            File file2 = new File(directory, "screenshot_squat2" + ".jpg");
            image_exercise_back.setImageDrawable(Drawable.createFromPath(file.toString()));
            image_exercise_legs.setImageDrawable(Drawable.createFromPath(file2.toString()));
            textview_fb_backangle.setText("Spine angle: " + (int)PoseGraphic.wrongSquatShoulderAngle + "deg.");
            textview_fb_legsangle.setText("Hip/knee/ankle angle: " + (int)PoseGraphic.wrongSquatLegsAngle + "deg.");
            textview_fb_back.setText(R.string.fb_squat_back);
            textview_fb_legs.setText(R.string.fb_squat_legs);
            textview_1.setText("Legs");
        }
    }
}
