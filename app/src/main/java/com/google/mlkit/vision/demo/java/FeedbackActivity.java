package com.google.mlkit.vision.demo.java;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.demo.R;

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
    private TextView textview_count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_activity);

        //initialise shared preference
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textview_count = findViewById(R.id.textview_count);
        setCount();

        button_screenshot = findViewById(R.id.button_screenshot);
        button_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot(); //todo fix
                System.out.println("Took screenshot!");
            }
        });
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
           // String mPath = Environment.getExternalStorageDirectory().toString()+"/img.jpg" ;
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File imageFile= new File(directory, "screenshot_pushup4" + ".jpg");
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);


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
    }
}
