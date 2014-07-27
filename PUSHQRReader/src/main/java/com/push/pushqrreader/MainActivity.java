package com.push.pushqrreader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.push.pushqrreader.Clients.ExerciseClient;
import com.push.pushqrreader.Fragments.ListOfScansFragment;
import com.push.pushqrreader.Fragments.ScanFragment;
import com.push.pushqrreader.PUSHResponseObjects.ErrorResponse;
import com.push.pushqrreader.PUSHResponseObjects.Exercise;

import java.util.ArrayList;

;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ExerciseClient.ExerciseClientListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private ExerciseClient mClient;
    private ArrayList<Exercise> mExercises;
    private ArrayList<ErrorResponse> mErrors;
    private View mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mLoadingSpinner = findViewById(R.id.loading_spinner);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ScanFragment())
                        .commit();
                break;
            case 1:
                ListOfScansFragment fragment = new ListOfScansFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
                fragment.configureWithExercisesAndErrors(mExercises, mErrors);
                break;
        }

        onSectionAttached(position);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.scan_page);
                break;
            case 1:
                mTitle = getString(R.string.list_of_scans);
                break;
            case 2:
                mTitle = getString(R.string.additional_page);
                break;
        }
        restoreActionBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                makeRequestWithPath(result.getContents());
                mLoadingSpinner.setVisibility(View.VISIBLE);
            }
        } else {
              super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void makeRequestWithPath(String path) {
        mClient = new ExerciseClient(path);
        mClient.syncWithServer(this);
    }
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //Exercise Client Listener

    public void requestSucceededWithResponse(ArrayList<Exercise> response) {
        mExercises.addAll(response);
        mLoadingSpinner.setVisibility(View.GONE);
    }

    public void requestFailedWithError(ErrorResponse response) {
        mErrors.add(response);
        mLoadingSpinner.setVisibility(View.GONE);
    }

}
