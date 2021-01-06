package com.example.scriptmanager;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
//import android.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.scriptmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Settings settings = new Settings();
    private Toolbar toolbar = null;
    public List<JobFragment> fragments = new ArrayList<JobFragment>();

    private Menu optionsMenu = null;
    private ArrayList <MenuItem> optionsMenuItemBackup = new ArrayList<MenuItem>();
    private boolean isInSelectMode = false;

    // Collection off ids for the menu
    int any_selection_buttons[] = {R.id.action_stopselected,
                        R.id.action_unselectall};
     int one_only_selection_buttons[] = {R.id.action_oneonly_edit};

    @Override
    protected  void onStart() {
        super.onStart();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Shell locations path
        Shell s = new Shell(
                getApplicationContext().getFilesDir().getAbsolutePath(),
                getApplicationContext().getExternalFilesDir(null).getAbsolutePath());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {

                /* Start intent to get a file ex: allow user to export/import scripts from the application
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");
                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when your app creates the document.
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("file:///sdcard/a"));
                startActivityForResult(intent, 1);
                */

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
                fragments.add(f);
                ft.commit();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_stopselected) {
            stopAllFragments(true);
            unselectAllFragments();
        }
        if (id == R.id.action_stopall) {
            unselectAllFragments();
            stopAllFragments();
        }
        if (id == R.id.action_unselectall) {
            unselectAllFragments();
        }
        if (id == R.id.action_settings) {

            unselectAllFragments();
            leaveSelectMode();

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

            if( isInSelectMode ) {
                unselectAllFragments();
                leaveSelectMode();
            }
            else {
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, new ViewJobsFragment());
                ft.commit();
                setMenuVisibility(true);
            }
            return true;
        }

        if ( id == R.id.action_oneonly_edit) {
            unselectAllFragments();

            Context context = getApplicationContext();
            String pvd = context.getApplicationContext().getPackageName() + ".provider";
            Log.v("scriptmanager",pvd);
            File f = new File(Shell.externalStorage+"/test.sh");
            Uri uri = FileProvider.getUriForFile(context, pvd, f);

            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(uri);
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(myIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // helpers
    /*
     * Hide or show the overflow menu
     */
    private void setMenuVisibility(boolean b) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu m = toolbar.getMenu();

        if( b ) {
            for (MenuItem mi : optionsMenuItemBackup) {
                mi.setVisible(true);
            }
            optionsMenuItemBackup.clear();
        }
        else {
            for (int a = 0; a < m.size(); a = a + 1) {
                MenuItem mi = m.getItem(a);
                if( mi.isVisible() ) {
                    optionsMenuItemBackup.add(mi);
                    mi.setVisible(false);
                }
            }
        }
    }

    /*
     * get color associated with the current theme.
     */
    public static int getColorFromId(MainActivity main, int colorID) {
        TypedValue typedValue = new TypedValue();
        main.getTheme().resolveAttribute(colorID, typedValue, true);
        return typedValue.data;
    }
    public void unselectAllFragments() {
        for (JobFragment jf : fragments) {
            jf.unselectView();
        }
    }
    public void stopAllFragments() {
        stopAllFragments(false);
    }
    public void stopAllFragments(boolean onlySelected) {
        for (JobFragment jf : fragments) {
            if( onlySelected)  {
                if(jf.isSelected) {
                    jf.stopJob();
                }
            }
            else
            {
                jf.stopJob();
            }
        }
    }
    public int getNumberSelected() {
        int count = 0;
        for (JobFragment jf : fragments) {
            if ( jf.isSelected ) {
                count += 1;
            }
        }
        return count;
    }
    public void enterOneOnlySelectMode() {
        for (int id : one_only_selection_buttons) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(true);
        }
    }
    public void leaveOneOnlySelectMode() {
        for (int id : one_only_selection_buttons) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(false);
        }
    }
    public void enterSelectMode() {
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);

            for (int id : any_selection_buttons) {
                MenuItem mi = optionsMenu.findItem(id);
                mi.setVisible(true);
            }
        isInSelectMode = true;
        enterOneOnlySelectMode();
    }

    public void leaveSelectMode() {
        ActionBar ab = getSupportActionBar();
        int color = getColorFromId(this, R.attr.colorPrimaryVariant);
        ab.setBackgroundDrawable(new ColorDrawable(color));
        ab.setDisplayHomeAsUpEnabled(false);

        for (int id : any_selection_buttons) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(false);
        }
        isInSelectMode = false;
        leaveOneOnlySelectMode();
    }
    //
}