package com.example.object_detect_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

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

public class Activity_photo_run extends AppCompatActivity {
    private Button take_photo_bt, return_bt;
    private PreviewView pre_view;
    private Bundle paras_bd;

    private Native model;

    private MyCamera my_camera;

    private File save_dir;
    private Boolean save_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_run);
        initView();
        iniEvent();
    }

    private void initView(){
        take_photo_bt = (Button) findViewById(R.id.take_photo_bt);
        return_bt = (Button) findViewById(R.id.return_setting);
        pre_view = (PreviewView) findViewById(R.id.pre_view);
    }

    private void iniEvent(){
        paras_bd = this.getIntent().getExtras();
        int camera_id = paras_bd.getInt("camera_id");
        int aspect_id = paras_bd.getInt("aspect_id");
        int rotation_id = paras_bd.getInt("rotation_id");
        save_result = paras_bd.getBoolean("save_result");
        String model_path = paras_bd.getString("model_path");
        String label_path = MyUtils.copyAssetGetFilePath(getApplicationContext(),"lcoco_label_list.txt");
        int cpu_thread_num = paras_bd.getInt("thread_num");
        String cpu_mode = paras_bd.getString("core_mode");
        int input_size = paras_bd.getInt("input_size");
        float conf_threshold = paras_bd.getFloat("obj_conf");
        float nms_threshold = paras_bd.getFloat("nms_conf");
        String dir_path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "detect_result";
        save_dir = new File(dir_path);
        if (!save_dir.exists()) save_dir.mkdir();
        model.init(model_path, label_path, cpu_thread_num, cpu_mode, input_size,
                input_size, conf_threshold, nms_threshold);
        my_camera.initForPhoto(this, pre_view, camera_id, aspect_id);
        take_photo_bt.setOnClickListener(new ButtonListener());
        return_bt.setOnClickListener(new ButtonListener());
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_photo_bt:
                    if (save_result){
                        String save_path = save_dir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                        my_camera.takePhoto(Activity_photo_run.this, model, getApplicationContext(), save_path);
                        Toast.makeText(Activity_photo_run.this, "已保存："+save_path, Toast.LENGTH_SHORT).show();
                    } else {
                        my_camera.takePhoto(Activity_photo_run.this, model, getApplicationContext(), null);
                    }

                case R.id.return_setting:
                    model.release();
                    Intent return_intent = new Intent();
                    return_intent.setClass(getApplicationContext(), Activity_photo.class);
                    startActivity(return_intent);
                    break;
                default:
                    break;
            }
        }
    }
}
