package kaus.testit.tapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error

        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ImageButton mCaptureBtn;
    CameraPreview mPreview;
    Camera mCamera;
    FrameLayout mFrame;
    Context mContext;
    MediaPlayer mShootMP;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
 //           mParam2 = getArguments().getString(ARG_PARAM2);
//        }


    }

    public void shootSound()
    {
        AudioManager meng = (AudioManager)getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);



        if (volume != 0)
        {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShootMP != null)
                mShootMP.start();
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            mCamera.startPreview(); //3

            // Uri pictureFile = generateFile(); //1
            try {
                recognizePicture (data); //2
                //      Toast.makeText(mContext, "Save file: " + pictureFile, Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                Toast.makeText(mContext, "Error: can't save file" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    };

    private void recognizePicture(byte[] data)  {

        QuizAsyncTask tsk = new QuizAsyncTask(getActivity());

        tsk.execute(data);

        Answer answer = null;
        try {
            answer = tsk.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        if (answer!=null) {

            if (!answer.getQuizNumber().isEmpty()) {
                // Toast.makeText(getApplicationContext(), answer.getQuizNumber(), Toast.LENGTH_SHORT).show();

                double ans = Answer.calculateAnswerPercentage(getActivity(),answer);
                if (ans>=0) {
                    Toast.makeText(getActivity().getApplicationContext(), String.format("Номер теста - %s\nПроцент правильных ответов - %s",answer.getQuizNumber() ,ans*100)+"%", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Ошибка распознования бланка ответа", Toast.LENGTH_SHORT).show();
                }

            }

 /*
        if (answer.getId()==-1){
            //Toast.makeText(getApplicationContext(),"Ответ не записан в базу", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),answer.getQuizNumber()+" "+answer.getRespondentNumber(), Toast.LENGTH_SHORT).show();
                    }
*/
 /*           try {

                //File path = new File (Environment.getExternalStorageDirectory(), "CameraTest");

                //String timeStamp = String.valueOf(System.currentTimeMillis());
                //File newFile = new File(path.getPath() + File.separator + timeStamp + ".jpg");

                MatOfByte buf = new MatOfByte();

                Mat mGray = answer.getBmp();

                Highgui.imencode(".jpg", mGray, buf);

                byte[] buffer = new byte[(int) buf.total()]; // read from stream ..

                buf.get(0, 0, buffer);

                byte[] modData;

                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                ImageView i = (ImageView) findViewById(R.id.backgraundImage);

                i.setImageBitmap(bitmap);

                //modData = new byte[(int) ((int) mGray.total() * mGray.channels())];
                // mGray.get(0,0,modData);

                //OutputStream os1 = getContentResolver().openOutputStream(Uri.fromFile(newFile));
                //os1.write(buffer);
                //os1.close();

            } catch (Exception e) {
                Toast.makeText(mContext, "Error: modData" + e.getMessage() + e.toString(), Toast.LENGTH_SHORT).show();
            }

        */
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Не распознано. Повторите сканирование.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "Не распознано. Повторите сканирование.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView =  inflater.inflate(R.layout.fragment_scan, container, false);
/*
        mContext = getActivity();

        mCamera = openCamera (); //1
        if (mCamera == null) { //2
            Toast.makeText(getActivity(), "Opening camera failed", Toast.LENGTH_LONG).show();
            return rootView;
        }

        mPreview = new CameraPreview(getActivity(), mCamera); //3

        mFrame = (FrameLayout)rootView.findViewById(R.id.layout); //4
        mFrame.addView(mPreview, 0);

        // mFrame.draw();



        mPreview.setOnTouchListener(new View.OnTouchListener() {
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

        mCaptureBtn = (ImageButton)rootView.findViewById(R.id.capture); //5

        ImageView i = (ImageView)rootView.findViewById(R.id.backgraundImage);

        mCaptureBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // делаем снимок

                        //mCamera.cancelAutoFocus();

                        Camera.Parameters params =  mCamera.getParameters();

                        List<android.hardware.Camera.Size> sizes = params.getSupportedPictureSizes();


                        for (android.hardware.Camera.Size size : sizes) {
                            if (size.width==1280&&size.height==960){

                                params.setPictureSize(size.width, size.height);

                            }
                            Log.d("Quiz", "Available resolution: "+size.width+" "+size.height);
                        }

                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

                        shootSound();

                        mCamera.setParameters(params);
                        mCamera.takePicture(null, null, null, mPictureCallback);


                    }
                }
        );
*/
        return  rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private Camera openCamera() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return null;

        Camera cam = null;
        if (Camera.getNumberOfCameras() > 0) {
            try {
                cam = Camera.open(0);
            }
            catch (Exception exc) {
                Toast.makeText(getActivity().getApplicationContext(),exc.getMessage(),Toast.LENGTH_LONG);
                //
            }
        }
        return cam;
    }
}
