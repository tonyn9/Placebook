package com.example.tonynguyen.placebook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class to create a CustomList
 */
public class CustomList extends ArrayAdapter<PlacebookEntry> {


    private final Activity context;
    private final ArrayList<PlacebookEntry> mPlacebookEntries;



    public CustomList(Activity context, ArrayList<PlacebookEntry> _mPlacebookEntries) {
        super(context, R.layout.row_layout, _mPlacebookEntries);
        this.context = context;
        this.mPlacebookEntries = _mPlacebookEntries;



    }

    @Override
    public View getView(int position, View view, ViewGroup patent){

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.row_layout, null, true);


        TextView txtTitle = (TextView) rowView.findViewById(R.id.row_txtPlace);
        TextView txtDescription = (TextView) rowView.findViewById(R.id.row_txtPlaceDesc);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.row_image_view);

        txtTitle.setText(mPlacebookEntries.get(position).getName());

        txtDescription.setText(mPlacebookEntries.get(position).getDescription());

        //imageView.setImageResource(ID.get(position));

        File imgFile = new File(mPlacebookEntries.get(position).getPhotoPath());
        if(imgFile.exists()){
            imageView.setImageBitmap(decodeSampledBitmapFromFile(imgFile, 150, 150));

        }


        return rowView;


    }


    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromFile(File imgFile,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
    }

}
