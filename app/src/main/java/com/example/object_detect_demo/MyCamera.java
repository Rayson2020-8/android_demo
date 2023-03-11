package com.example.object_detect_demo;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;


public class MyCamera {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider mCameraProvider;
    private ImageCapture imageCapture = null;
    private Preview preview = null;
    private CameraSelector cameraSelector = null;
    private ImageAnalysis imageAnalysis = null;

    public void initForPhoto(AppCompatActivity act, PreviewView pre, int camera_select, int aspect_ratio) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(act.getApplicationContext());
        cameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider.unbindAll();
                mCameraProvider = cameraProviderFuture.get();
                preview = new Preview.Builder()
                        .build();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(camera_select == 0 ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_BACK)
                        .build();
                preview.setSurfaceProvider(pre.getSurfaceProvider());
                imageCapture =
                        new ImageCapture.Builder()
                                .setTargetAspectRatio(aspect_ratio == 1 ? AspectRatio.RATIO_16_9 : AspectRatio.RATIO_4_3)
                                .setTargetRotation(pre.getDisplay().getRotation())
                                .build();
                mCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(act.getApplicationContext()));
    }

    public void initForVideo(AppCompatActivity act, Native model,ImageView img_view, int camera_select, int aspect_ratio){
        cameraProviderFuture = ProcessCameraProvider.getInstance(act.getApplicationContext());
        cameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider.unbindAll();
                mCameraProvider = cameraProviderFuture.get();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(camera_select == 0 ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_BACK)
                        .build();
                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(aspect_ratio == 1 ? AspectRatio.RATIO_16_9 : AspectRatio.RATIO_4_3)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        Bitmap bitmapBuffer = Bitmap.createBitmap(
                                imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                        bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());
                        model.process(bitmapBuffer, null);
                        img_view.setImageBitmap(bitmapBuffer);
                        imageProxy.close();
                    }
                });
                mCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(act.getApplicationContext()));
    }


    public void takePhoto(Activity act, Native model,Context cont, String img_dir) {
        String save_path = img_dir + File.separator + System.currentTimeMillis() + ".jpg";
        File file = new File(save_path);
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        if (imageCapture == null) {
            return;
        }
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(cont), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                Bitmap bitmap;
                try {
                     bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), savedUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                model.process(bitmap, save_path);
                Toast.makeText(act, "保存成功: " + save_path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(act, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
