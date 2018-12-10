package kaus.testit.tapp;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * Created by Ausiannikau on 11.04.2018.
 */

public class Recognizer {

    static QuizAsyncTask tsk;

    private Recognizer(){}

    public static Answer getAnswer(Context context, byte[] data){

            tsk = new QuizAsyncTask(context);
            Log.d("Quiz","start recognition");

            tsk.execute(data);

            Answer answer = null;
            try {
                answer = tsk.get();

                if (answer==null){

                    Log.d("Quiz", "Answer null");
                }
                else {
                    Log.d("Quiz", answer.getQuizNumber());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return answer;
    }
}
