package com.example.object_detect_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button img_bt = (Button) findViewById(R.id.detect_images);
        Button photo_bt = (Button) findViewById(R.id.detect_photo);
        Button vedio_bt = (Button) findViewById(R.id.detect_video);
        img_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,Activity_images.class);
                startActivity(intent);
            }
        });
        photo_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,Activity_photo.class);
                startActivity(intent);
            }
        });
        vedio_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,Activity_video.class);
                startActivity(intent);
            }
        });



    }
}
