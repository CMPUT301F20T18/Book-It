package com.example.cmput301f20t18;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * This activity implements an ISBN scanner and returns the ISBN calling this activity
 * This activity must be called with startActivityForResult to function properly
 * https://medium.com/analytics-vidhya/creating-a-barcode-scanner-using-android-studio-71cff11800a2
 * @see Book
 * @author deinum
 */
public class Scanner extends AppCompatActivity {

    private final boolean ENABLE_MANUAL = true;
    //private final boolean ENABLE_MANUAL = false;

    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final int OPEN_POST = 0;
    private final int RETURN_ISBN = 1;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public PreviewView previewView;
    public Button cap;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int mode;
    private int bookID;
    private Long expected_isbn;
    String TAG = "SCANNER_DEBUG";

    // TODO: don't forget to remove temporary button
    private Button buttonManual; // delete this
    private String inputISBN; // delete this too


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        previewView = findViewById(R.id.previewView);
        cap = (Button) findViewById(R.id.capture);
        buttonManual = (Button) findViewById(R.id.button_manual);

        mode = getIntent().getIntExtra("type", OPEN_POST);
        bookID = getIntent().getIntExtra("bookID", 0);
        expected_isbn = getIntent().getLongExtra("eISBN", 0);

        Log.d(TAG, "type of scan:" + Integer.toString(mode));
        Log.d(TAG, "Expected ISBN " + expected_isbn);


        // determine if the user has given the proper privileges to use the camera
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }


    }

    /**
     * Checks if the user has granted camera permissions
     * @return true if all permissions have been granted by the user, else false
     */
    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     *
     * @param requestCode An integer representing the request permissions
     * @param permissions A string containing the required permissions
     * @param grantResults An array of integers representing the permissions granted by the user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }


    /**
     * Starts the camera and output the camera preview to screen
     */
    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    Scanner.this.bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }


    /**
     * Binds the preview and other functions to the screen
     * @param cameraProvider represents the source of the input stream
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();
        final ImageCapture imageCapture = builder
                .setTargetRotation(Objects.requireNonNull(getDisplay()).getRotation())
                .build();

        // For future expansion , if user doesn't want a button to scan
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();


        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);


        // set our on capture listener
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        scanCode(image);
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });
            }
        });

        if (ENABLE_MANUAL) {
            buttonManual.setVisibility(View.VISIBLE);
            buttonManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText input = new EditText(v.getContext());
                    input.setTextColor(getResources().getColor(R.color.colorBlue));
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setId(R.id.manual_isbn_input);

                    android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                            .setTitle("Enter ISBN")
                            .setView(input)
                            .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    manualISBN(input.getText().toString());
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });
        }
    }


    /**
     * Analyze an image from our camera and scan it to obtain the books ISBN
     * @param image proxy image of the camera at the time the button was hit
     * @return integer representation of the scanned ISBN
     */
    public int scanCode(ImageProxy image) {

        // set-up our barcode format
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
                        .build();

        // convert proxy image to image ml kit can analyze
        InputImage picture = null;
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();
        if (mediaImage != null) {
            picture = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
        }
        assert picture != null;

        // scan the code and obtain the results
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(picture)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    // found a barcode! Take the first barcode scanned, return the ISBN
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            int valueType = barcode.getValueType();
                            if (valueType == Barcode.TYPE_ISBN && rawValue != null) {
                                Intent intent;
                                if (mode == OPEN_POST) {
                                    intent = new Intent(Scanner.this, PostScanActivity.class);
                                    intent.putExtra("ISBN", rawValue);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    startActivity(intent);


                                    }
                                else {
                                    intent = new Intent();
                                    intent.putExtra("ISBN", rawValue);
                                    intent.putExtra("bookID", bookID);
                                    intent.putExtra("eISBN", expected_isbn);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    return;
                                }

                            }
                        }
                    }
                })

                // no barcode found, keep going
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return 0;
    }

    // TEMPORARY
    public void manualISBN(String rawValue) {
        if (rawValue != null) {
            Intent intent;
            if (mode == OPEN_POST) {
                intent = new Intent(Scanner.this, PostScanActivity.class);
                intent.putExtra("ISBN", rawValue);
                setResult(RESULT_OK, intent);
                finish();
                startActivity(intent);
            }
            else {
                intent = new Intent();
                intent.putExtra("ISBN", rawValue);
                intent.putExtra("bookID", bookID);
                intent.putExtra("eISBN", expected_isbn);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}












