package com.example.object_detect_demo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyUtils {

    public static List<String> getFileAllName(String path){
        //传入指定文件夹的路径　　　　
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> filePaths = new ArrayList<>();
        if(files == null) {
            filePaths.add("空文件夹！");
            return filePaths;
        }
        for(int i = 0; i < files.length; i++){
            filePaths.add(files[i].getPath());
        }
        return filePaths;
    }

    public static List<String> getImgAllName(String path){
        //传入指定文件夹的路径　　　　
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> imagePaths = new ArrayList<>();
        for(int i = 0; i < files.length; i++){
            if(checkIsImageFile(files[i].getPath())){
                imagePaths.add(files[i].getPath());
            }

        }
        return imagePaths;
    }

    /**
     * 判断是否是照片
     */
    public static boolean checkIsImageFile(String fName){
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if(fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("gif")
                || fileEnd.equals("jpeg")|| fileEnd.equals("bmp")){
            isImageFile = true;
        }else{
            isImageFile = false;
        }
        return isImageFile;
    }

    public static String copyAssetGetFilePath(Context mContext, String assetsFilename) {

        try {
            File filesDir = mContext.getFilesDir();
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            File outFile = new File(filesDir, assetsFilename);
            String outFilename = outFile.getAbsolutePath();
            Log.i(TAG, "outFile is " + outFilename);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    Log.e(TAG, "outFile not exist!(" + outFilename + ")");
                    return null;
                }
            }
            InputStream is = mContext.getAssets().open(assetsFilename);

            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
