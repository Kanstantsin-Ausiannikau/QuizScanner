package kaus.testit.tapp;

import android.content.Context;
import android.os.AsyncTask;

public class QuizAsyncTask  extends AsyncTask<byte [], Answer, Answer> {
    private Answer answer;

    private Context context;

    QuizAsyncTask(Context mContext){
        context = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Toast.makeText(mContext, "Go", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Answer doInBackground(byte []... data) {
        try {
            answer = new QuizForm(data[0]).getAnswer();
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    protected void onPostExecute(Answer result) {
        super.onPostExecute(result);

    }

}
