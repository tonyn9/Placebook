package com.example.tonynguyen.placebook;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tony on 6/7/2015.
 */
public class SearchList extends ArrayAdapter<PlacebookEntry> {


private final Activity context;
private final ArrayList<PlacebookEntry> PlacebookEntry;



public SearchList(Activity context, ArrayList<PlacebookEntry> _PlaceName) {
        super(context, R.layout.search_layout, _PlaceName);
        this.context = context;
        this.PlacebookEntry = _PlaceName;

        }

@Override
public View getView(int position, View view, ViewGroup patent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.search_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.row_txtPlace01);
        txtTitle.setText(PlacebookEntry.get(position).getName());
        return rowView;

        }


}
