package com.example.scriptapp;

import androidx.appcompat.app.ActionBar;
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

    public Settings settings = new Settings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

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

                JobFragment f = JobFragment.newInstance("my text");
                ft.add(R.id.linear_layout_actions_list, f);
                ft.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
     * Hide or show the overflow menu
     */
    private void setMenuVisibility(boolean b) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu m = toolbar.getMenu();
        for (int a = 0; a < m.size(); a = a + 1) {
            MenuItem mi = m.getItem(a);
            mi.setVisible(b);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            ActionBar ab = super.getSupportActionBar();
            ab.setTitle(R.string.settings_page_title);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayUseLogoEnabled(false);

            findViewById(R.id.fab).setVisibility(View.INVISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new SettingsFragment());
            ft.commit();

            setMenuVisibility(false);

            return true;
        }

        if ( id == android.R.id.home) {

            ActionBar ab = super.getSupportActionBar();
            ab.setTitle(R.string.app_name);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayUseLogoEnabled(true);

            findViewById(R.id.fab).setVisibility(View.VISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new ViewJobsFragment());
            ft.commit();

            setMenuVisibility(true);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}