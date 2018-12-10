package kaus.testit.tapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Vibrator;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.SurfaceHolder.Callback;

public class CameraPreview extends SurfaceView implements Callback {

    SurfaceHolder mHolder;
    Camera mCamera;
    Context mContext;
    TextView mtxtAnswer;
    ImageView mView;


    public CameraPreview(Context context, Camera camera, TextView txtAnswer, ImageView view) { //1
        super(context);
        mContext = context;
        mCamera = camera;
        mHolder = getHolder(); //2
        mHolder.addCallback(this); //3
        mView = view;

        mtxtAnswer = txtAnswer;

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //4
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //5
        try {
            mCamera.setPreviewDisplay(holder);

            mCamera.startPreview();
        } catch (Exception e) {
            Toast.makeText(mContext, "Camera preview failed" + e.getMessage() + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // 6
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mHolder.getSurface() == null)
            return;

        if (mCamera != null) {

            mCamera.stopPreview();
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {

                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                    Log.d("Quiz", "review enter");
                    int width = 0;
                    int height = 0;
                    Camera.Parameters parameters = camera.getParameters();

                    height = parameters.getPreviewSize().height;
                    width = parameters.getPreviewSize().width;
                    YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);

                    Rect rectangle = new Rect(0, 0, width, height);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(rectangle, 90, out);
                    byte[] imageBytes = out.toByteArray();

                    recognizePicture(imageBytes);
                }

            });

            setCameraDisplayOrientation();

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Toast.makeText(mContext, "Camera preview failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setCameraDisplayOrientation() {
        if (mCamera == null)
            return;

        Camera.CameraInfo info = new Camera.CameraInfo(); // 1
        Camera.getCameraInfo(0, info);

        WindowManager winManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation(); //2

        int degrees = 0;

        switch (rotation) { //3
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 0;
                break;
            case Surface.ROTATION_180:
                degrees = 0;
                break;
            case Surface.ROTATION_270:
                degrees = 0;
                break;
        }

        int result; //4
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result); //5

        Camera.Parameters parameters = mCamera.getParameters(); //6
        int rotate = 90;

        parameters.setRotation(rotate);
        mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void recognizePicture(byte[] data) {

        Answer answer = Recognizer.getAnswer(mContext, data);

        if (answer == null) {
            mtxtAnswer.setText("");
            return;
        }

         mView.setImageBitmap(ImageSaver.getBmp(answer.getImage()));

        if (!answer.getQuizNumber().isEmpty()) {

            double answerResult = Answer.calculateAnswerPercentage(mContext, answer);
            if (answerResult >= 0) {
                TappDataBaseHelper db = new TappDataBaseHelper(mContext);
                String respondentName = db.GetRespondentNameByRespondentNumber(answer.getRespondentNumber());
                String newFormattedAnswer = String.format("Тест - %s\nОценка - %s\n%s",
                        answer.getQuizNumber(),
                        String.format("%.2f", answerResult * 100) + "%",
                        respondentName);

                if (!mtxtAnswer.getText().equals(newFormattedAnswer)){
                    long quizDbId = db.insertAnswer(answer);
                    answer.setId(quizDbId);

                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                    mtxtAnswer.setTextColor(Color.GREEN);
                    mtxtAnswer.setText(newFormattedAnswer);
                }
            }
        }
    }
}
