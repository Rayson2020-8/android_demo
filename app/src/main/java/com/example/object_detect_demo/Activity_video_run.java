package com.example.object_detect_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class Activity_video_run extends AppCompatActivity {
    private Bundle paras_bd;

    private ImageView img_view;
    private Native model;

    private MyCamera my_camera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_run);
        initView();
        iniEvent();
    }

    private void initView(){
        img_view = (ImageView) findViewById(R.id.show_img_iv);
    }

    private void iniEvent(){
        paras_bd = this.getIntent().getExtras();
        int camera_id = paras_bd.getInt("camera_id");
        int aspect_id = paras_bd.getInt("aspect_id");
        int rotation_id = paras_bd.getInt("rotation_id");
        String model_path = paras_bd.getString("model_path");
        String label_path = MyUtils.copyAssetGetFilePath(getApplicationContext(),"lcoco_label_list.txt");
        int cpu_thread_num = paras_bd.getInt("thread_num");
        String cpu_mode = paras_bd.getString("core_mode");
        int input_size = paras_bd.getInt("input_size");
        float conf_threshold = paras_bd.getFloat("obj_conf");
        float nms_threshold = paras_bd.getFloat("nms_conf");
        model.init(model_path, label_path, cpu_thread_num, cpu_mode, input_size,
                input_size, conf_threshold, nms_threshold);
        my_camera.initForVideo(this, model, img_view, camera_id, aspect_id);
    }
}
