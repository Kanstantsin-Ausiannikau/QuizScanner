package kaus.testit.tapp;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onResume() {
        super.onResume();

        if (getSupportFragmentManager().getFragments()!=null){
            for(int i=0;i<getSupportFragmentManager().getFragments().size();i++){
                Fragment f = getSupportFragmentManager().getFragments().get(i);

                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(f).attach(f).commit();
                //ft.attach(f);
                //ft.commit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("Quizzy",MODE_PRIVATE);

        if (sp.getString("user",null)==null){
 //           Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
        }



        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (sp.getString("FirstLogin","false").equals("true")){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("FirstLogin","false");
            editor.commit();

            Server server = new Server(getApplicationContext());
            //server.Connect(sp.getString("user",null),sp.getString("password",null));

            server.Connect("2000@tut.by","Lehfrb1!");


            if (server.isConnected()) {
                //server.SyncServer(sp.getString("user", null), sp.getString("password", null));

                server.SyncServer("2000@tut.by","Lehfrb1!");


                Snackbar.make(navigationView, "Синхронизация завершена",
                        Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(navigationView, "Нет соединения с сервером",
                        Snackbar.LENGTH_LONG).show();
            }
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        //mDrawerLayout.openDrawer(GravityCompat.START);

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);
                        // TODO: handle navigation
                        // Closing drawer on item click

                        if (menuItem.getItemId()==R.id.nav_groups){
                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                            TabLayout.Tab tab = tabLayout.getTabAt(0);
                            tab.select();
                        }

                        if (menuItem.getItemId()==R.id.nav_quizs){
                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                            TabLayout.Tab tab = tabLayout.getTabAt(1);
                            tab.select();
                        }

                        if (menuItem.getItemId()==R.id.nav_scan){
                            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                            startActivity(intent);
                        }

                        if (menuItem.getItemId()==R.id.nav_sync){
                            Server server = new Server(getApplicationContext());
                            server.Connect(sp.getString("user",null),sp.getString("password",null));
                            if (server.isConnected()) {
                                //server.SyncServer(sp.getString("user", null), sp.getString("password", null));

                                server.SyncServer("2000@tut.by", "Lehfrb1!");

                                Snackbar.make(navigationView, "Синхронизация завершена",
                                        Snackbar.LENGTH_LONG).show();
                            }
                            else {
                                Snackbar.make(navigationView, "Нет соединения с сервером",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }

                        if (menuItem.getItemId()==R.id.nav_logout){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("user",null);
                            editor.putString("password",null);
                            editor.commit();

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        adapter.addFragment(new GroupsFragment(), "Группы");
        adapter.addFragment(new QuizsFragment(), "Тесты");
        adapter.addFragment(new ResultsFragment(),"Результаты");

        viewPager.setAdapter(adapter);
    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}


