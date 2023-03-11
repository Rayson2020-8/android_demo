package com.example.object_detect_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

public class Activity_images_run extends AppCompatActivity {
    private Button pre_img_bt, next_img_bt, return_bt;
    private ImageView img_iv;
    private int img_idx = 0;
    private Bundle paras_bd;
    private List<String> img_names;
    private String img_path;
    private Bitmap bitmap;

    private Native model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_run);
        initView();
        iniEvent();
    }

    private void initView(){
        pre_img_bt = (Button) findViewById(R.id.pre_img_bt);
        next_img_bt = (Button) findViewById(R.id.next_img_bt);
        return_bt = (Button) findViewById(R.id.return_setting);
        img_iv = (ImageView) findViewById(R.id.show_img_iv);
    }

    private void iniEvent(){
        paras_bd = this.getIntent().getExtras();
        img_names = MyUtils.getImgAllName(paras_bd.getString("img_dir"));
        pre_img_bt.setOnClickListener(new ButtonListener());
        next_img_bt.setOnClickListener(new ButtonListener());
        String model_path = paras_bd.getString("model_path");
        String label_path = MyUtils.copyAssetGetFilePath(getApplicationContext(),"lcoco_label_list.txt");
        int cpu_thread_num = paras_bd.getInt("thread_num");
        String cpu_mode = paras_bd.getString("core_mode");
        int input_size = paras_bd.getInt("input_size");
        float conf_threshold = paras_bd.getFloat("obj_conf");
        float nms_threshold = paras_bd.getFloat("nms_conf");
        model.init(model_path, label_path, cpu_thread_num, cpu_mode, input_size,
                input_size, conf_threshold, nms_threshold);
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pre_img_bt:
                    img_idx -= 1;
                    img_idx = img_idx < 0 ? 0 : img_idx;
                    img_path = img_names.get(img_idx);
                    bitmap = BitmapFactory.decodeFile(img_path);
                    model.process(bitmap, null);
                    img_iv.setImageBitmap(bitmap);
                    break;
                case R.id.next_img_bt:
                    img_idx += 1;
                    img_idx = img_idx > img_names.size() ? img_names.size() : img_idx;
                    img_path = img_names.get(img_idx);
                    bitmap = BitmapFactory.decodeFile(img_path);
                    model.process(bitmap, null);
                    img_iv.setImageBitmap(bitmap);
                    break;
                case R.id.return_setting:
                    model.release();
                    Intent return_intent = new Intent();
                    return_intent.setClass(getApplicationContext(), Activity_images.class);
                    startActivity(return_intent);
                    break;
                default:
                    break;
            }
        }
    }
}
