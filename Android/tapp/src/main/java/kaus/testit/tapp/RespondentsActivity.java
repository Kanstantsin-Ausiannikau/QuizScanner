package kaus.testit.tapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class RespondentsActivity extends Activity {

    ListView mLstRespondents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respondents);

        Intent intent = getIntent();

        int groupId = intent.getIntExtra("GroupId", 0);

        TappDataBaseHelper db = new TappDataBaseHelper(this);

        List<Respondent> respondents = db.getRespondents(groupId);

        mLstRespondents = (ListView)findViewById(R.id.lstRespondents);

        String[] mRespondents = new String [respondents.size()];
        for(int i=0;i<mRespondents.length;i++){
            mRespondents[i] = respondents.get(i).getFirstName()+" "+respondents.get(i).getLastName()+" "+respondents.get(i).getRespondentNumber()+" "+respondents.get(i).getServerGroupId();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mRespondents);

        mLstRespondents.setAdapter(adapter);

        mLstRespondents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Intent intent = new Intent(RespondentsActivity.this, RespondentsActivity.class);

                //intent.putExtra("GroupId",i);

                //startActivity(intent);
            }
        });
    }
}
