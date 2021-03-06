package com.releasestandard.scriptmanager.controller;

import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.releasestandard.scriptmanager.MainActivity;
import com.releasestandard.scriptmanager.R;

import java.util.ArrayList;

/**
 *  Implement logic on the settings overflow menu.
 */
public class OverflowMenu {

    private Menu optionsMenu = null;

    public static int MODE_STANDARD = 0;
    public static int MODE_NO_EXT = 1;
    private static int MODE = MODE_STANDARD;

    private ArrayList<MenuItem> optionsMenuItemBackup = new ArrayList<MenuItem>();

    // Collection off ids for the menu

    public static int any_selection_buttons_standard[] = {R.id.action_stopselected,
            R.id.action_unselectall,R.id.action_anyselection_delete};
    public  static  int any_selection_buttons[] = any_selection_buttons_standard;
    public static int any_selection_buttons_compat_no_external[] = any_selection_buttons;

    public static int one_only_selection_buttons_standard[] = {R.id.action_oneonly_edit,
            R.id.action_oneonly_rename, R.id.action_oneonly_show_log ,
            R.id.action_oneonly_clear_log
    };
    public static int one_only_selection_buttons[] =  one_only_selection_buttons_standard;
    public static int one_only_selection_buttons_no_external[] = {
            R.id.action_oneonly_rename
    };

    int debug_mode[] = {/*R.id.settings_fragment_debug_mode*/};

    int running_mode[] = {R.id.action_stopall};

    private MainActivity main = null;

    public static void gotoMode() { gotoMode(MODE_STANDARD);}
    public static void gotoMode(int mod) {
        if ( MODE == mod ) { return; }
        if ( mod == MODE_NO_EXT) {
            any_selection_buttons = any_selection_buttons_compat_no_external;
            one_only_selection_buttons = one_only_selection_buttons_no_external;
        }
        if ( mod == MODE_STANDARD) {
            any_selection_buttons = any_selection_buttons_standard;
            one_only_selection_buttons = one_only_selection_buttons_standard;
        }
        MODE = mod;
    }
    public OverflowMenu(MainActivity main, Menu menu) {
        this.main = main;
        this.optionsMenu = menu;
    }

    // helpers
    /**
     * Hide or show the overflow menu.
     */
    public void setMenuVisibility(boolean b) {
        Toolbar toolbar = (Toolbar) main.findViewById(R.id.toolbar);
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
     * At least one job is running
     */
    public void enterRunningMode() {
        for (int id : running_mode) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(true);
        }
    }
    public void leaveRunningMode() {
        for (int id : running_mode) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(false);
        }
    }
    public void enterDebugMode() {
        for (int id : debug_mode) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(true);
        }
    }
    public void leaveDebugMode() {
        for (int id : debug_mode) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(false);
        }
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

    /**
     * compat 11
     */
    public void enterSelectMode() {
        ActionBar ab = main.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        for (int id : any_selection_buttons) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(true);
        }
        main.isInSelectMode = true;
        enterOneOnlySelectMode();
    }

    /**
     * compat 11
     */
    public void leaveSelectMode() {
        ActionBar ab = main.getSupportActionBar();
        int color = main.getColorFromId(main, R.attr.colorPrimaryVariant);
        ab.setBackgroundDrawable(new ColorDrawable(color));
        ab.setDisplayHomeAsUpEnabled(false);

        for (int id : any_selection_buttons) {
            MenuItem mi = optionsMenu.findItem(id);
            mi.setVisible(false);
        }
        main.isInSelectMode = false;
        leaveOneOnlySelectMode();
    }

    public void callbackSelectAndRunning(MainActivity main) {
        if ( main.jobs_view.getNumberStartedAndSelected() > 0) {
            optionsMenu.findItem(R.id.action_stopselected)
                    .setVisible(true);
        } else {
            optionsMenu.findItem(R.id.action_stopselected)
                    .setVisible(false);
        }
    }
}
