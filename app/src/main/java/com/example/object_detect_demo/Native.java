package com.example.object_detect_demo;

import android.graphics.Bitmap;

public class Native {
    static {
        System.loadLibrary("Native");
    }

    private long ctx = 0;

    public boolean init(String model_path,
                        String labelPath,
                        int cpuThreadNum,
                        String cpuPowerMode,
                        int inputWidth,
                        int inputHeight,
                        float conf_threshold,
                        float nms_threshold) {
        ctx = nativeInit(
                model_path,
                labelPath,
                cpuThreadNum,
                cpuPowerMode,
                inputWidth,
                inputHeight,
                conf_threshold,
                nms_threshold);
        return ctx == 0;
    }

    public boolean release() {
        if (ctx == 0) {
            return false;
        }
        return nativeRelease(ctx);
    }

    public boolean process(Bitmap ARGB8888ImageBitmap, String savedImagePath) {
        if (ctx == 0) {
            return false;
        }
        // ARGB8888 bitmap is only supported in native, other color formats can be added by yourself.
        return nativeProcess(ctx, ARGB8888ImageBitmap, savedImagePath);
    }

    public static native long nativeInit(String model_path,
                                         String labelPath,
                                         int cpuThreadNum,
                                         String cpuPowerMode,
                                         int inputWidth,
                                         int inputHeight,
                                         float scoreThreshold,
                                         float nms_threshold);

    public static native boolean nativeRelease(long ctx);

    public static native boolean nativeProcess(long ctx, Bitmap ARGB888ImageBitmap, String savedImagePath);
}
