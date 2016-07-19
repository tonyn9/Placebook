package com.example.tonynguyen.placebook;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.SurfaceHolder;

import java.io.File;

/**
 * Created by Tony Nguyen on 5/27/2015. stuff
 */
public class PlacebookEntry implements Parcelable {

    public final long id ;
    private String name ;
    private String description ;
    private String photoPath ;


    private Context context;

    public PlacebookEntry(Context context, int _EntryID){
        this.id = _EntryID;
        this.name = null;
        this.description = null;
        this.photoPath = null;
        this.context = context;
    }


    public PlacebookEntry ( Parcel source ) {
        this . id = source . readLong () ;
        this . name = source . readString () ;
        this . description = source . readString () ;
        this . photoPath = source . readString () ;

    }


    public String getID(){
        return ((Long) this.id).toString();
    }

    public Long getIDNumeral(){
        return this.id;
    }

    public String getPhotoPath(){
        return this.photoPath;
    }

    public void setPhotoPath(String _PhotoPath){
        this.photoPath = _PhotoPath;

    }

    public String getName(){
        return this.name;
    }

    public void setName(String _Name){
        this.name = _Name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String _Description){
        this.description = _Description;
    }


    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel ( Parcel dest , int flags ) {
        dest . writeLong ( this . id );
        dest . writeString (this.name);
        dest . writeString ( this . description );
        dest . writeString ( this . photoPath );
    }


    public static final Parcelable . Creator < PlacebookEntry > CREATOR
            = new Parcelable . Creator < PlacebookEntry >() {
        @Override
        public PlacebookEntry createFromParcel ( Parcel source ) {
            return new PlacebookEntry ( source );
        }
        @Override
        public PlacebookEntry [] newArray ( int size ) {
            return new PlacebookEntry [ size ];
        }
    };


}
