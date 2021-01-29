package com.releasestandard.scriptmanager;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.releasestandard.scriptmanager.controller.OverflowMenu;
import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.tools.Logger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.DocumentsContract;
import android.util.TypedValue;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar = null;

    private static boolean FIRST_CREATION = true;
    private static int ACTIVITY_REQUEST_CODE_IMPORT = 1;
    public boolean isInSelectMode = false;

     // Objects that encapsulate the data //
     public JobsView jobs_view = null;
     public SettingsView sf = null;
     public OverflowMenu ow_menu = null;
     private Hashtable <Integer, Fragment> views = null;

     StorageManager sm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Shell locations path
        this.sm = new StorageManager(getApplicationContext().getFilesDir().getAbsolutePath(),
                getApplicationContext().getExternalFilesDir(null).getAbsolutePath());

        FloatingActionButton fab = findViewById(R.id.main_activity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                handlerFabClick();
            }
        });

        this.views = new Hashtable<>();
        jobs_view = new JobsView(this.sm);

        sf = new SettingsView();
        views.put(R.id.nav_host_fragment, jobs_view);
        views.put(R.id.action_settings, sf);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.RECEIVE_SMS}, 1);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, jobs_view);
        ft.commit();

        Logger.debug("MainActivity:onCreate");
    }
    @Override
    protected  void onStart() {
        super.onStart();
        if ( FIRST_CREATION ) {
            jobs_view.readState(); // get the state from the storage
            FIRST_CREATION = false;
        }
        Logger.debug("MainActivity:onStart");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ow_menu = new OverflowMenu(this, menu);
        return true;
    }

    public void showFileWithEditor(String path) {
        Context context = getApplicationContext();
        String pvd = context.getApplicationContext().getPackageName() + ".provider";
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        Uri uri = FileProvider.getUriForFile(context, pvd, f);

        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(uri);
        myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(myIntent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        MainActivity main = this;
        int id = item.getItemId();

        if (id == R.id.action_stopselected) {
            jobs_view.stopAllFragments(true);
            jobs_view.unselectAllFragments();
        }
        if (id == R.id.action_stopall) {
            jobs_view.stopAllFragments();
            jobs_view.unselectAllFragments();
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

            findViewById(R.id.main_activity_fab).setVisibility(View.INVISIBLE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, sf);
            ft.commit();

            ow_menu.setMenuVisibility(false);
            return true;
        }

        if (id == android.R.id.home) {
            if (isInSelectMode) {
               mainLeaveSelectMode();
            } else {
                settings2main();
            }
        }

        if (id == R.id.action_oneonly_edit) {
            JobView jf = jobs_view.getSelected();
            showFileWithEditor(jf.shell.sm.getScriptAbsolutePath());
            jobs_view.unselectAllFragments();
        }

        if ( R.id.action_anyselection_delete == id ) {
            for( JobView jf : jobs_view.getSelecteds() ) {
                jf.removeViewJob();
                jobs_view.fragments.remove(jf);
            }
            jobs_view.unselectAllFragments();
        }
        if (R.id.action_oneonly_clear_log == id) {
            JobView jf = jobs_view.getSelected();
            try {
                jf.shell.clearLog(jf.shell.sm.getLogAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            jobs_view.unselectAllFragments();
        }
        if ( R.id.action_oneonly_show_log == id) {
            JobView jf = jobs_view.getSelected();
            showFileWithEditor(jf.shell.sm.getLogAbsolutePath());
            jobs_view.unselectAllFragments();
        }
        if (R.id.action_oneonly_rename == id) {
            JobView jf = jobs_view.getSelected();
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppTheme_RenameDialog);
            View customLayout = getLayoutInflater().inflate(R.layout.rename_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(R.string.action_oneonly_rename_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText et = (EditText) customLayout.findViewById(R.id.rename_dialog_input);
                    jf.setName(et.getText().toString());
                    jf.writeState();
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
            jobs_view.unselectAllFragments();
        }

        // not ready yet due to limitations to access the storage //
        if (R.id.action_browse_scripts == id) {
            JobView jf = jobs_view.getSelected();
            Uri selectedUri = Uri.parse(sm.externalStorage );

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("text/*");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, selectedUri);
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_IMPORT);
            jobs_view.unselectAllFragments();
        }
        if (R.id.action_import_script == id) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("text/*");
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_IMPORT);
            jobs_view.unselectAllFragments();
        }
        if ( R.id.action_test_button == id) {
            jobs_view.writeState();
        }
        if ( R.id.action_test_button2 == id) {
            jobs_view.readState();
        }

        // Start service at boot / enregistrement sur disk
        return true;
    }
    /*
     * Handle result of the import action.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if ( data == null) {
            // user has cancelled the request
            return;
        }
        if ( requestCode == ACTIVITY_REQUEST_CODE_IMPORT) {
            Uri uri = data.getData();
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            handlerFabClick();
            // create a new fragment
            JobView jf = jobs_view.fragments.get(jobs_view.fragments.size() - 1);
            File f2 = new File(jf.shell.sm.getScriptAbsolutePath());
            if (!f2.exists()) {
                try {
                    f2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                int c;
                while ((c = is.read()) != -1) {
                    fos.write(c);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    // Handlers for click on interface
    public void handlerFabClick() {
        jobs_view.addNewJob();
    }

    public void settings2main() {
        ActionBar ab = super.getSupportActionBar();
        ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayUseLogoEnabled(true);

        findViewById(R.id.main_activity_fab).setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, jobs_view);
        ft.commit();

        ow_menu.setMenuVisibility(true);
    }
    public void mainLeaveSelectMode() {
        ActionBar ab = super.getSupportActionBar();
        ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayUseLogoEnabled(true);
        jobs_view.unselectAllFragments();
        ow_menu.leaveSelectMode();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        for ( Fragment f : fm.getFragments()) {
            if ( f == sf) {
                settings2main();
                return;
            }
        }
        if ( isInSelectMode ) {
            mainLeaveSelectMode();
        }
        else {
            AlertDialog ad = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                    .setTitle(R.string.dialog_back_title)
                    .setMessage(R.string.dialog_back_content)

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}