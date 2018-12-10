package kaus.testit.tapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private String GetUrl(String url, String urlData) throws IOException {

//        String urlParameters  = "param1=a&param2=b&param3=c";
        byte[] postData = urlData.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL targetUrl = new URL(url);
        HttpURLConnection conn= (HttpURLConnection) targetUrl.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }


/*
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(url);

        http.setEntity(new UrlEncodedFormEntity(pairs));

        return (String) httpclient.execute(http, new BasicResponseHandler());

        */
        return "1";
    }

}
