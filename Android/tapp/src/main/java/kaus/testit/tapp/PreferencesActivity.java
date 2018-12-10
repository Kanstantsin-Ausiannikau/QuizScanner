package kaus.testit.tapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PreferencesActivity extends Activity {

    private SharedPreferences mTapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        mTapp = getSharedPreferences("tapp", MODE_PRIVATE);

        if (mTapp.contains("BigPaperArea")){
            //Preferences.
        }

    }
}
