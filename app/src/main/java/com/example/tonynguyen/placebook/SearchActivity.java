package com.example.tonynguyen.placebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends Activity implements ActionMode.Callback {


    private ArrayList<PlacebookEntry> mPlacebookEntries;
    private ArrayList<String> places;


    private ListView mListview;
    protected Object mActionMode;
    public int selectedItem = -1;




    private SearchList adapter;
    private ArrayAdapter<String> adapter2;

    private String name;
    private long id;


    // Search EditText
    private EditText inputSearch = null;

    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_search);




        intent = getIntent();
        mPlacebookEntries = intent.getParcelableArrayListExtra(Placebook.SEARCH_ALL_KEY);

        //Listview data
        places = new ArrayList<>();

        for(PlacebookEntry place: mPlacebookEntries){
            places.add(place.getName());
        }


        adapter2 = new ArrayAdapter<>(this, R.layout.search_layout, R.id.row_txtPlace01, places);



        mListview = (ListView) findViewById(R.id.searchview);

        mListview.setAdapter(adapter2);

        //adapter = new SearchList(SearchActivity.this, places);

        //mListview.setAdapter(adapter);

        inputSearch = (EditText) findViewById(R.id.SearchText);
        inputSearch.addTextChangedListener(inputTextWatcher);




        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }


                name  = adapter2.getItem(position);
                Log.v("What is parent", name);

                selectedItem = position;
                Log.v("Selected Item", "Selected item is number" + selectedItem);
                mActionMode = SearchActivity.this.startActionMode(SearchActivity.this);
                view.setSelected(true);
                return true;
            }
        });

    }


    private TextWatcher inputTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter2.getFilter().filter(s);
            adapter2.notifyDataSetChanged();
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent.putParcelableArrayListExtra(Placebook.SEARCH_ALL_KEY, mPlacebookEntries) ;
        setResult(Activity.RESULT_OK, intent);

        inputSearch.removeTextChangedListener(inputTextWatcher);
    }


    @Override
    public void onBackPressed(){
        intent.putParcelableArrayListExtra(Placebook.SEARCH_ALL_KEY, mPlacebookEntries);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.rowselection, menu);
        return true;
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
                for(PlacebookEntry temp: mPlacebookEntries){
                    if (name.equals(temp.getName())){
                        selectedItem = mPlacebookEntries.indexOf(temp);
                        mPlacebookEntries.remove(temp);
                        places.remove(selectedItem);
                        adapter2.notifyDataSetChanged();
                        break;
                    }
                }




                mode.finish();
                return true;
            case R.id.action_edit_place:
                for(PlacebookEntry temp: mPlacebookEntries){
                    if (name.equals(temp.getName())){
                        selectedItem = mPlacebookEntries.indexOf(temp);
                        intent.putExtra("edit place", selectedItem);
                        intent.putParcelableArrayListExtra(Placebook.SEARCH_ALL_KEY, mPlacebookEntries);
                        setResult(Placebook.REQUEST_CHANGE, intent);

                        break;
                    }
                }


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
