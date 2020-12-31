package com.example.scriptapp;

import androidx.fragment.app.FragmentManager;
//import android.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // set the theme to green
        setTheme(R.style.Theme_Scriptapp_Light);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                // ex : setting remove animations
                // not very visible lol
                ft.setCustomAnimations(FragmentTransaction.TRANSIT_NONE,
                        FragmentTransaction.TRANSIT_NONE,
                        FragmentTransaction.TRANSIT_NONE,
                        FragmentTransaction.TRANSIT_NONE);

                ConfigureJobFragment f = ConfigureJobFragment.newInstance("my text");
                ft.add(R.id.linear_layout_actions_list, f);
                ft.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //Toolbar settings_toolbar_fragment = findViewById(R.id.settings_toolbar_fragment);
            //setSupportActionBar(settings_toolbar_fragment);
            //Toolbar toolbar_for_settings = new Toolbar(getApplicationContext());
            //setSupportActionBar(toolbar_for_settings);

            findViewById(R.id.fab).setVisibility(View.INVISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new SettingsFragment());
            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}