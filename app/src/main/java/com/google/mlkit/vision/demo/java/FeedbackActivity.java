package com.google.mlkit.vision.demo.java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class FeedbackActivity  extends AppCompatActivity {

    private Button button_screenshot;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView textview_count, textview_exercise, textview_fb_back, textview_fb_legs, textview_1;
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
        textview_1 = findViewById(R.id.textview_1);
        setCount();

        button_screenshot = findViewById(R.id.button_screenshot);
        button_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot(); //todo fix
            }
        });
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/"  + "screenshot.jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void setCount() {
        // read from shared preference
        int count = sharedPreferences.getInt("EXERCISE_COUNT",0);
        textview_count.setText("Exercise count- " + count);

        int exercise = sharedPreferences.getInt("SELECTED_EXERCISE",0);
        //0 - pushups, 1 - squats
        String e = exercise==0?"PUSHUPS":"SQUATS";
        textview_exercise.setText("Exercise selected- " + e);

        if(exercise==0){
            //show image, give text feedback
            image_exercise_back.setImageResource(R.drawable.image_pushup);
            image_exercise_legs.setImageResource(R.drawable.image_pushup);
            textview_fb_back.setText(R.string.fb_pushup_back);
            textview_fb_legs.setText(R.string.fb_pushup_legs);
            textview_1.setText("HANDS");
        }
        else{
            image_exercise_back.setImageResource(R.drawable.image_squat);
            image_exercise_legs.setImageResource(R.drawable.image_squat);
            textview_fb_back.setText(R.string.fb_squat_back);
            textview_fb_legs.setText(R.string.fb_squat_legs);
            textview_1.setText("LEGS");
        }
    }
}
