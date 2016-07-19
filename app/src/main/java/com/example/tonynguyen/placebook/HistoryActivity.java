package com.example.tonynguyen.placebook;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;


public class HistoryActivity extends Activity implements ActionMode.Callback {

    private ArrayList<PlacebookEntry> mPlacebookEntries;


    private ListView mListview;
    protected Object mActionMode;
    public int selectedItem = -1;

    private CustomList adapter;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_history);


        intent = getIntent();
        mPlacebookEntries = intent.getParcelableArrayListExtra(Placebook.VIEW_ALL_KEY);

        Log.v("HistoryActtt", "size is " + mPlacebookEntries.size());
        Log.v("HistoryActtt", mPlacebookEntries.get(0).getName());
        Log.v("HistoryActtt", "First ID is " + mPlacebookEntries.get(0).getID());



        mListview = (ListView) findViewById(R.id.listview);

        adapter = new CustomList(HistoryActivity.this, mPlacebookEntries);

        mListview.setAdapter(adapter);

        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }
                selectedItem = position;
                Log.v("Selected Item", "Selected item is number" + selectedItem);
                mActionMode = HistoryActivity.this.startActionMode(HistoryActivity.this);
                view.setSelected(true);
                return true;
            }


        });

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.rowselection, menu);
        return true;
    }


    @Override
    public void onBackPressed(){
        intent.putParcelableArrayListExtra(Placebook.VIEW_ALL_KEY, mPlacebookEntries);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent . putParcelableArrayListExtra ( Placebook . VIEW_ALL_KEY , mPlacebookEntries ) ;
        setResult(Activity.RESULT_OK, intent) ;
        finish();

    }


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_place:
                // Delete Item
                mPlacebookEntries.remove(selectedItem);
                Log.v("Delete Item", "Size of ArrayList is now " + mPlacebookEntries.size());
                adapter.notifyDataSetChanged();
                mode.finish();
                return true;
            case R.id.action_edit_place:
                intent.putExtra("edit place", selectedItem);
                intent.putParcelableArrayListExtra(Placebook.VIEW_ALL_KEY, mPlacebookEntries);
                setResult(Placebook.REQUEST_CHANGE, intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        selectedItem = -1;

    }

}
