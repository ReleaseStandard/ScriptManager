package com.example.scriptmanager;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
//import android.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar = null;

    public boolean isInSelectMode = false;

     // Objects that encapsulate the data //
     public ViewJobsFragment jobs_view = null;
     public SettingsFragment sf = null;
     public OverflowMenu ow_menu = null;
     private Hashtable <Integer, Fragment> views = null;

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
                handlerFabClick();
            }
        });

        this.views = new Hashtable<>();
        jobs_view = new ViewJobsFragment();
        sf = new SettingsFragment();
        views.put(R.id.nav_host_fragment, jobs_view);
        views.put(R.id.action_settings, sf);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, jobs_view);
        ft.commit();
        Log.v("scriptmanager","MainActivity:onCreate");
    }
    @Override
    protected  void onStart() {
        super.onStart();
        Log.v("scriptmanager","MainActivity:onStart");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ow_menu = new OverflowMenu(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivity main = this;
        int id = item.getItemId();

        if (id == R.id.action_stopselected) {
            jobs_view.stopAllFragments(true);
            jobs_view.unselectAllFragments();
        }
        if (id == R.id.action_stopall) {
            jobs_view.unselectAllFragments();
            jobs_view.stopAllFragments();
        }
        if (id == R.id.action_unselectall) {
            jobs_view.unselectAllFragments();
        }
        if (id == R.id.action_settings) {

            jobs_view.unselectAllFragments();
            ow_menu.leaveSelectMode();

            ActionBar ab = super.getSupportActionBar();
            ab.setTitle(R.string.settings_page_title);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayUseLogoEnabled(false);

            findViewById(R.id.fab).setVisibility(View.INVISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment,sf);
            ft.commit();


            ow_menu.setMenuVisibility(false);

            return true;
        }

        if ( id == android.R.id.home) {
            ActionBar ab = super.getSupportActionBar();
            ab.setTitle(R.string.app_name);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayUseLogoEnabled(true);

            if( isInSelectMode ) {
                jobs_view.unselectAllFragments();
                ow_menu.leaveSelectMode();
            }
            else {
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment,jobs_view);
                ft.commit();

                ow_menu.setMenuVisibility(true);
            }
            return true;
        }

        if ( id == R.id.action_oneonly_edit) {
            JobFragment jf = jobs_view.getSelected();
            jobs_view.unselectAllFragments();

            Context context = getApplicationContext();
            String pvd = context.getApplicationContext().getPackageName() + ".provider";
            Log.v("scriptmanager",pvd);
            File f = new File(Shell.externalStorage+"/" + jf.path );
            if ( ! f.exists() ) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    // We a need a proper way to handle this
                    return true;
                }
            }
            Uri uri = FileProvider.getUriForFile(context, pvd, f);

            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(uri);
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(myIntent);

            return true;
        }

        if ( R.id.action_oneonly_rename == id ) {
            JobFragment jf = jobs_view.getSelected();
            jobs_view.unselectAllFragments();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View customLayout = getLayoutInflater().inflate(R.layout.rename_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(R.string.action_oneonly_rename_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditText et = (EditText)customLayout.findViewById(R.id.editText);
                            jf.setName(et.getText().toString());
                        }
                    });
            builder.setNegativeButton(R.string.action_oneonly_rename_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.setTitle(R.string.action_oneonly_rename);
            alert.show();
        }

        // not ready yet due to limitations to access the storage //
        if ( R.id.action_browse_scripts == id ) {
            JobFragment jf = jobs_view.getSelected();
            jobs_view.unselectAllFragments();

            /*Log.v("scriptmanager",Shell.externalStorage + "/");
            Uri selectedUri = Uri.parse(Shell.externalStorage + "/");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE); Intent.ACT
            intent.setData(selectedUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //startActivity(intent);
            startActivityForResult(intent, 1);
*/
/*
            Intent intent = new Intent((Build.VERSION.SDK_INT >= 19 ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT));
            intent.setType("text/*");
            startActivityForResult(intent, 1);

 */
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * get color associated with the current theme.
     */
    public static int getColorFromId(MainActivity main, int colorID) {
        TypedValue typedValue = new TypedValue();
        main.getTheme().resolveAttribute(colorID, typedValue, true);
        return typedValue.data;
    }

    // Handlers for click on interface
    public void handlerFabClick() {
        FragmentManager fm = jobs_view.getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        JobFragment f = new JobFragment();
        ft.add(R.id.linear_layout_actions_list, f);
        ft.commit();

        jobs_view.fragments.add(f);
    }
}