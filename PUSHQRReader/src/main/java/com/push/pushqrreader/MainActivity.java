package com.push.pushqrreader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.push.pushqrreader.Clients.ExerciseClient;
import com.push.pushqrreader.Fragments.BlankFragment;
import com.push.pushqrreader.Fragments.ListOfScansFragment;
import com.push.pushqrreader.Fragments.ScanFragment;
import com.push.pushqrreader.PUSHResponseObjects.ErrorResponse;
import com.push.pushqrreader.PUSHResponseObjects.Exercise;

import java.util.ArrayList;

;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ExerciseClient.ExerciseClientListener, ScanFragment.ScanFragmentListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private ExerciseClient mClient;
    private View mLoadingSpinner;
    private ArrayList<Exercise> mExercises = new ArrayList<Exercise>();
    private ArrayList<ErrorResponse> mErrors = new ArrayList<ErrorResponse>();

    private ScanFragment mScanFragment;
    private ListOfScansFragment mListOfScansFragment;
    private BlankFragment mBlankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mLoadingSpinner = findViewById(R.id.progress_spinner);
        mLoadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                if(mScanFragment == null) {
                    mScanFragment = new ScanFragment();
                }
                mScanFragment.setListener(this);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mScanFragment).commit();
                break;
            case 1:
                if(mListOfScansFragment == null) {
                    mListOfScansFragment = new ListOfScansFragment();
                }
                mListOfScansFragment.configureWithExercisesAndErrors(mExercises, mErrors);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mListOfScansFragment)
                        .commit();
                break;
            default:
                if(mBlankFragment == null) {
                    mBlankFragment = new BlankFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mBlankFragment)
                        .commit();
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
            case 3:
                mTitle = getString(R.string.additional_page2);
                break;
            case 4:
                mTitle = getString(R.string.additional_page3);
                break;
        }
        restoreActionBar();
    }

    public void makeRequestWithPath(String path) {
        if(mClient == null) {
            mClient = new ExerciseClient(path);
        }
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
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    //Scan Fragment Listener

    public void scannedWithURL(String URL) {
        if(URL != null &&  URLUtil.isValidUrl(URL)) {
            makeRequestWithPath(URL);
        } else {
            Toast.makeText(MainActivity.this, "Scanned invalid URL", Toast.LENGTH_LONG);
        }
        mLoadingSpinner.setVisibility(View.VISIBLE);
    }

    //Exercise Client Listener

    public void requestSucceededWithResponse(ArrayList<Exercise> response) {
        mExercises.addAll(response);
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "RESPONSE received: ", Toast.LENGTH_LONG).show();
                mLoadingSpinner.setVisibility(View.GONE);
            }
        });
    }

    public void requestFailedWithError(final ErrorResponse response) {
        mErrors.add(response);
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Scanned: " + response.error, Toast.LENGTH_LONG).show();
                mLoadingSpinner.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public void requestFailed() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Request failed unknown ", Toast.LENGTH_LONG).show();
                mLoadingSpinner.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void responseInvalid() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Received invalid response: ", Toast.LENGTH_LONG).show();
                mLoadingSpinner.setVisibility(View.GONE);
            }

        });
    }
}
