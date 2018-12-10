package kaus.testit.tapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.List;

public class GroupsActivity extends Activity {

    ListView mLstGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        TappDataBaseHelper db = new TappDataBaseHelper(this);

        final List<Group> groups =  db.getGroups();

        mLstGroups = (ListView)findViewById(R.id.lstGroups);

        String[] mGroups = new String [groups.size()];
        for(int i=0;i<mGroups.length;i++){
            mGroups[i] = groups.get(i).getGroupName() + " "+ groups.get(i).getServerId()+" "+groups.get(i).getId();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mGroups);

        mLstGroups.setAdapter(adapter);

        mLstGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(GroupsActivity.this, RespondentsActivity.class);

                intent.putExtra("GroupId", groups.get(i).getServerId());

                startActivity(intent);
            }
        });
    }
}
