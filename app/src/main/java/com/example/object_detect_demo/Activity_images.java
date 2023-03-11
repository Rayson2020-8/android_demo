package com.example.object_detect_demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class Activity_images extends AppCompatActivity {

    private String img_dir, model_path=null, core_mode="LITE_POWER_HIGH";
    private List<String> model_names=null;
    private boolean save_result=true, iter_by_hand=true;
    private int intput_size, thread_num=1;
    private float obj_conf, nms_conf;
    private TextView img_dir_tv;
    private Button img_select_bt, run_detect_bt, return_main_bt;
    private RadioGroup save_result_rg, auto_iter_rg;
    private Spinner select_model_sp, core_mode_sp, thread_num_sp;
    private EditText input_size_et, obj_conf_et, nms_conf_et;
    private static final int REQUEST_CODE_IMG_DIR=1;
    private static final String MODEL_DIR = Environment.getExternalStorageDirectory().toString() +  "/" +"model";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        initView();
        initEvent();
        sendParas();

    }

    private void sendParas(){
        img_dir = img_dir_tv.getText().toString();
        intput_size = Integer.parseInt(input_size_et.getText().toString());
        obj_conf = Float.parseFloat(obj_conf_et.getText().toString());
        nms_conf = Float.parseFloat(nms_conf_et.getText().toString());
        Intent detect_intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("img_idr", img_dir);
        bundle.putBoolean("save_result", save_result);
        bundle.putBoolean("iter_by_hand", iter_by_hand);
        bundle.putString("model_path", model_path);
        bundle.putInt("input_size", intput_size);
        bundle.putFloat("obj_conf", obj_conf);
        bundle.putFloat("nms_conf", nms_conf);
        bundle.putString("core_mode", core_mode);
        bundle.putInt("thread_num", thread_num);
        detect_intent.putExtras(bundle);

    }

    private void initView() {
        img_dir_tv = (TextView) findViewById(R.id.img_dir_tv);
        img_select_bt = (Button) findViewById(R.id.select_dir_bt);
        save_result_rg = (RadioGroup) findViewById(R.id.save_result_rg);
        auto_iter_rg = (RadioGroup) findViewById(R.id.iter_rg);
        select_model_sp = (Spinner) findViewById(R.id.select_model_sp);
        input_size_et = (EditText) findViewById(R.id.model_input_ed);
        obj_conf_et = (EditText) findViewById(R.id.conf_input_ed);
        nms_conf_et = (EditText) findViewById(R.id.nms_input_ed);
        core_mode_sp = (Spinner) findViewById(R.id.select_core_sp);
        thread_num_sp = (Spinner) findViewById(R.id.select_thread_sp);
        return_main_bt = (Button) findViewById(R.id.return_main);
        run_detect_bt = (Button) findViewById(R.id.run_detect);
    }

    private void initEvent(){
        img_select_bt.setOnClickListener(new ButtonListener());
        save_result_rg.setOnCheckedChangeListener(new MyChangedOnClickListenr());
        auto_iter_rg.setOnCheckedChangeListener(new MyChangedOnClickListenr());
        select_model_sp.setOnItemSelectedListener(new SpinnerListener());
        model_names = MyUtils.getFileAllName(MODEL_DIR);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, model_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_model_sp.setAdapter(adapter);
        core_mode_sp.setOnItemSelectedListener(new SpinnerListener());
        thread_num_sp.setOnItemSelectedListener(new SpinnerListener());
        return_main_bt.setOnClickListener(new ButtonListener());
        run_detect_bt.setOnClickListener(new ButtonListener());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_IMG_DIR:
                if (resultCode==Activity.RESULT_OK){
                    Uri uri = data.getData();
                    img_dir = uri.toString();
                    img_dir_tv.setText(img_dir);
                } else {
                    Toast.makeText(getApplicationContext(),"fail!!!",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class ButtonListener implements View.OnClickListener {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.select_dir_bt:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CODE_IMG_DIR);
                    break;
                case R.id.return_main:
                    Intent return_intent = new Intent();
                    return_intent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(return_intent);
                    break;
                case R.id.run_detect:
                    
                    break;
                default:
                    break;
            }
        }
    }
    class MyChangedOnClickListenr implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup,int i) {
            switch (radioGroup.getId()) {
                case R.id.save_result_rg:
                    if (i == R.id.to_save_rb) {
                        save_result = true;
                    } else {
                        save_result = false;
                    }
                    break;
                case R.id.iter_rg:
                    if (i == R.id.by_hand_rb) {
                        iter_by_hand = true;
                    } else {
                        iter_by_hand = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.select_model_sp:
                    model_path = MODEL_DIR + File.separator +  model_names.get(position);
                    break;
                case R.id.select_core_sp:
                    String[] core_content = getResources().getStringArray(R.array.core_mode);
                    core_mode = core_content[position];
                    break;
                case R.id.select_thread_sp:
                    String[] thread_content = getResources().getStringArray(R.array.thread_num);
                    thread_num = Integer.parseInt(thread_content[position]);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //未选中时候的操作
        }
    }

}
