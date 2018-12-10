package kaus.testit.tapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;


public class ScanActivity extends AppCompatActivity {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error

        }
    }

    ImageButton btnBack;
    CameraPreview preview;
    Camera mCamera;
    FrameLayout mFrame;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mCamera = openCamera (); //1
        if (mCamera == null) { //2
            Toast.makeText(this, "Opening camera failed", Toast.LENGTH_LONG).show();
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        parameters.setPreviewSize(800,600);
        mCamera.setParameters(parameters);

        preview = new CameraPreview(this, mCamera, (TextView) findViewById(R.id.txtViewAnswer), (ImageView)findViewById(R.id.backgraundImage)); //3

        mFrame = (FrameLayout) findViewById(R.id.layout); //4
        mFrame.addView(preview, 0);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mCamera != null) {
                    Camera camera = mCamera;
                    camera.cancelAutoFocus();
                    Rect focusRect = new Rect((int)(event.getX()-100),(int)(event.getY()-100),(int)(event.getX()+100),(int)(event.getY()+100));

                    Camera.Parameters parameters = camera.getParameters();
                    if (parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }

                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                    }

                    try {
                        camera.cancelAutoFocus();
                        camera.setParameters(parameters);
                        camera.startPreview();
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    Camera.Parameters parameters = camera.getParameters();
                                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    if (parameters.getMaxNumFocusAreas() > 0) {
                                        parameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });


        btnBack = (ImageButton)findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mCamera != null) {

            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;

            mFrame.removeView(preview);
            preview = null;
        }
    }

    protected void onResume() {
        super.onResume();

        if(mCamera==null){
            mCamera = openCamera (); //1
            if (mCamera == null) { //2
                Toast.makeText(this, "Opening camera failed", Toast.LENGTH_LONG).show();
                return;
            }

            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            for(int i=0;i<sizeList.size();i++){
                Log.d("Quiz", String.valueOf(sizeList.get(i).height)+" "+String.valueOf(sizeList.get(i).width));
            }

            parameters.setPreviewSize(720,1280);
            mCamera.setParameters(parameters);

            preview = new CameraPreview(this, mCamera, (TextView) findViewById(R.id.txtViewAnswer), (ImageView)findViewById(R.id.backgraundImage)); //3
            mFrame = (FrameLayout) findViewById(R.id.layout); //4
            mFrame.addView(preview, 0);
        }
    }

    private Camera openCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return null;

        Camera cam = null;
        if (Camera.getNumberOfCameras() > 0) {
            try {
                cam = Camera.open(0);
            }
            catch (Exception exc) {
                Toast.makeText(getApplicationContext(),exc.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        return cam;
    }
}
