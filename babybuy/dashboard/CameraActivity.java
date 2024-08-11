package np.com.yourname.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import np.com.yourname.babybuy.R;

public class CameraActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private MaterialButton imageButtonClick;
    private MaterialButton imageButtonCancel;
    private PreviewView previewViewCamera;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static String FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final int CAMERA_ACTIVITY_SUCCESS_RESULT_CODE = 3012;
    public static final int CAMERA_ACTIVITY_FAILURE_RESULT_CODE = 3013;
    public static final String CAMERA_ACTIVITY_OUTPUT_FILE_PATH = "output_file_path";
    public static final String CAMERA_ACTIVITY_OUTPUT_EXCEPTION_MESSAGE = "output_exception_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageButtonClick = findViewById(R.id.mb_camera_click);
        imageButtonCancel = findViewById(R.id.mb_cancel);
        previewViewCamera = findViewById(R.id.preview_view_camera);

        //Asking for permissions and if given, initialize camera
        if (allPermissionGranted()) {
            initializeCustomCamera();
        } else {
            requestPermissions(
                    getPermissionsRequiredForCamera().toArray(new String[0]),
                    10
            );
        }

        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFailureResultBackToCallingComponent("Camera cancelled...");
            }
        });

        imageButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (allPermissionGranted()) {
                initializeCustomCamera();
            } else {
                String message = "Please grant the required permissions";
                Toast.makeText(this,
                        message,
                        Toast.LENGTH_SHORT).show();
                setFailureResultBackToCallingComponent(message);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(CAMERA_ACTIVITY_FAILURE_RESULT_CODE);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private boolean allPermissionGranted() {
        boolean granted = false;
        for (String permission :
                getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                granted = true;
            }
        }
        return granted;
    }

    private List<String> getPermissionsRequiredForCamera() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissions;
    }

    private void initializeCustomCamera() {
        cameraProviderFuture = ProcessCameraProvider
                .getInstance(CameraActivity.this);
        cameraProviderFuture.addListener(
                () -> {
                    try {
                        ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                        Preview preview = new Preview.Builder().build();
                        preview.setSurfaceProvider(previewViewCamera.getSurfaceProvider());

                        imageCapture = new ImageCapture.Builder().build();

                        CameraSelector defaultCameraSelector = new CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build();
                        processCameraProvider.unbindAll();
                        processCameraProvider.bindToLifecycle(
                                this,
                                defaultCameraSelector,
                                preview,
                                imageCapture
                        );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                ContextCompat.getMainExecutor(this)
        );
    }

    private void captureImage() {
        //If this instance is null, we don't have to proceed further
        if (imageCapture == null) {
            setFailureResultBackToCallingComponent("Cannot start camera...");
            return;
        }
        String fileName = new SimpleDateFormat(FILE_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/");
        }

        //Lets create an output options object which will contains the file and metadata
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(
                        getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        ).build();

        //Setting up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults outputFileResults) {
                        setSuccessResultBackToCallingComponent(
                                outputFileResults.getSavedUri().toString()
                        );
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        setFailureResultBackToCallingComponent(exception.getMessage());
                    }
                }
        );
    }

    private void setSuccessResultBackToCallingComponent(String outputFileUriPath) {
        Intent intent = new Intent();
        intent.putExtra(CAMERA_ACTIVITY_OUTPUT_FILE_PATH, outputFileUriPath);
        setResult(CAMERA_ACTIVITY_SUCCESS_RESULT_CODE, intent);
        finish();
    }

    private void setFailureResultBackToCallingComponent(String exceptionMessage) {
        Intent intent = new Intent();
        intent.putExtra(CAMERA_ACTIVITY_OUTPUT_FILE_PATH, exceptionMessage);
        setResult(CAMERA_ACTIVITY_FAILURE_RESULT_CODE, intent);
        finish();
    }
}