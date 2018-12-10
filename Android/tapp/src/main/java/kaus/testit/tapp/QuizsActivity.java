package kaus.testit.tapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class QuizsActivity extends AppCompatActivity {

    ListView mLstQuizs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizs);

        TappDataBaseHelper db = new TappDataBaseHelper(this);

        final List<Quiz> quizs =  db.getQuizs();

        mLstQuizs = (ListView)findViewById(R.id.lstQuizs);

        String[] mQuizs = new String [quizs.size()];
        for(int i=0;i<mQuizs.length;i++){
            mQuizs[i] = quizs.get(i).getTitle() + " "+ quizs.get(i).getQuizNumber()+" "+quizs.get(i).getQuizId();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mQuizs);

        mLstQuizs.setAdapter(adapter);

        mLstQuizs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });


    }
}
