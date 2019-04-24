package kaus.testit.tapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PostDataSender extends AsyncTask<String, Void, String> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... data) {

        String result = null;

        try {
            result = GetUrl(data[0], data[1]);
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
        //String response = null;


        StringBuffer response = new StringBuffer();

        try {
            url = new URL(urlAdress);
            HttpURLConnection connection;
            connection = (HttpURLConnection)url.openConnection();

            Log.d("quiz", "open get request");

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + urlData);
            int responseCode = connection.getResponseCode();

            Log.d("quiz", String.valueOf(responseCode));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            // do something...
        } catch (Exception e) {
            e.printStackTrace();
        }



/*




        URL url;
        url = new URL(urlAdress);
        HttpURLConnection connection;
        connection = (HttpURLConnection)url.openConnection();

        Log.d("quiz", "open");

        connection.setRequestProperty("Content-Type",
                "application/json");

        connection.setRequestProperty("Authorization", "Bearer " + urlData);

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        Log.d("quiz", "get answer");

        DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream());
        wr.writeBytes("password=Lehfrb1!&user=2000@tut.by");
        wr.flush();
        wr.close();

        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            String response = sb.toString();

            Log.d("quiz", response);

            return  sb.toString();
        }

/*        Log.d("quiz", "convert answer");

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
        */

        Log.d("quiz", response.toString());

        return response.toString();
    }

}
