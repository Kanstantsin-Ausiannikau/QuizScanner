package kaus.testit.tapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class PostDataSender extends AsyncTask<String, Void, String> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... data) {

        String result = null;
        List pairs = new ArrayList();

        String urlData = null;
        String separator = "";
        for(int i=1;i<data.length;i=i+2) {

            urlData = separator+data[i]+data[i+1];
            separator = "&";

            //pairs.add(new Pair(data[i], data[i+1]));
        }

        try {
            result = GetUrl(data[0], urlData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    protected void onPostExecute(String result) {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String GetUrl(String urlAdress, String urlData) throws IOException {

        Log.d("quiz", urlAdress);
        Log.d("quiz", urlData);

        URL url;
        url = new URL(urlAdress);
        URLConnection connection;
        connection = url.openConnection();

        Log.d("quiz", "open");

        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Length", "" +
                Integer.toString("password=Lehfrb1!&user=2000@tut.by".getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        Log.d("quiz", "get answer");

        DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream());
        wr.writeBytes("password=Lehfrb1!&user=2000@tut.by");
        wr.flush();
        wr.close();

        Log.d("quiz", "convert answer");

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            Log.d("quiz", line);
            response.append(line);
            response.append('\r');
        }
        rd.close();

        Log.d("quiz", response.toString());


        return response.toString();
    }

}
