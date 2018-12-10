package kaus.testit.tapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.OutputStream;

/**
 * Created by Ausiannikau on 22.03.2018.
 */

public class ImageSaver {

    public static void Save(Mat bmp){
 /*       try {

                //File path = new File (Environment.getExternalStorageDirectory(), "CameraTest");

                //String timeStamp = String.valueOf(System.currentTimeMillis());
                //File newFile = new File(path.getPath() + File.separator + timeStamp + ".jpg");

                MatOfByte buf = new MatOfByte();

                Mat mGray = bmp;

                Highgui.imencode(".jpg", mGray, buf);

                byte[] buffer = new byte[(int) buf.total()]; // read from stream ..

                buf.get(0, 0, buffer);

                byte[] modData;

                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                ImageView i = (ImageView) findViewById(R.id.backgraundImage);

                i.setImageBitmap(bitmap);

                modData = new byte[(int) ((int) mGray.total() * mGray.channels())];
                mGray.get(0,0,modData);

                OutputStream os1 = getContentResolver().openOutputStream(Uri.fromFile(newFile));
                os1.write(buffer);
                os1.close();

            } catch (Exception e) {
                Toast.makeText(mContext, "Error: modData" + e.getMessage() + e.toString(), Toast.LENGTH_SHORT).show();
            }
            */
    }

    public static Bitmap getBmp(Mat mat){
        MatOfByte buf = new MatOfByte();

        Mat mGray = mat;

        Highgui.imencode(".jpg", mGray, buf);

        byte[] buffer = new byte[(int) buf.total()];

        buf.get(0, 0, buffer);

        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

        return bitmap;
    }



}
