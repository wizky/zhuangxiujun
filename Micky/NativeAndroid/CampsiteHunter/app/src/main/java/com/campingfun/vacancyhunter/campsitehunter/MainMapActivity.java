package com.campingfun.vacancyhunter.campsitehunter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.campingfun.vacancyhunter.campsitehunter.Active.Async_FindCampgroundNearName;
import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;
import com.campingfun.vacancyhunter.campsitehunter.Utils.ToastHelper;

import de.greenrobot.event.EventBus;


public class MainMapActivity extends Activity {

    private static final String TAG = MainMapActivity.class.getSimpleName();
    private AutoCompleteTextView autoCompleteTextView;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main_map);
        progressDialog = new ProgressDialog(this);

        // Get a reference to the AutoCompleteTextView in the layout
        this.autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.main_map_autoCompleteSearchTextView);
        // Create the adapter and set it to the AutoCompleteTextView
        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(this, android.R.layout.simple_list_item_1);
        this.autoCompleteTextView.setAdapter(adapter);
        this.autoCompleteTextView.setThreshold(2);
        this.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                searchCampgrounds(adapter.getItem(position));
            }
        });

        this.autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String name = v.getText().toString();
                    GooglePlace found = adapter.findEntryByName(name);
                    Log.i(TAG, "Try to get place " + name + "from cached entry result is: " + found);
                    if (found == null) {
                        found = new GooglePlace(name, null);
                    }

                    searchCampgrounds(found);
                    return true;
                }
                return false;
            }
        });
    }

    private void searchCampgrounds(GooglePlace place) {
        progressDialog.show();
        new Async_FindCampgroundNearName(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, place);
    }

    public void onEventMainThread(ShowCampgroundsEvent event) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void onEventMainThread(Event_ShowToast event) {
        ToastHelper.showToast(this, event.getMessage());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
