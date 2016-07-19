package com.example.tonynguyen.placebook;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Placebook extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private Context context;

    public static final String VIEW_ALL_KEY = " com . example . tonynguyen . placebook . EXTRA_VIEW_ALL " ;
    public static final String SEARCH_ALL_KEY = " com . example . tonynguyen . placebook . EXTRA_SEARCH_ALL ";

    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_SPEECH_INPUT = 1002;
    private static final int REQUEST_PLACE_PICKER = 1003;
    public static final int REQUEST_CHANGE = 1004;
    private static final int REQUEST_VIEW_ALL = 1005;
    private static final int REQUEST_SEARCH = 1006;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String TAG = Placebook.class.getSimpleName();

    private int change = -1;
    private long old_id;


    private TextView mTextView = null;

    private boolean mResolvingError, mBtnLocationPressed;

    EditText mPlaceDescription, mTxtPlaceContent;
    ImageButton mBtnSpeak, mBtnLocation, mBtnPlacePicker, mBtnSnapshot;
    TextView mTxtGPSLongitude, mTxtGPSLatitude, mTxtGPSAltitude;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;


    private int entryID;

    private PlacebookEntry mPlacebookEntry;
    private ArrayList<PlacebookEntry> mPlacebookEntries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextView = new TextView(this);
        context = this;

        setContentView(R.layout.activity_main);



        mPlaceDescription        =  (EditText)      findViewById(R.id.edit_place_desc   );
        mBtnSpeak                =  (ImageButton)   findViewById(R.id.button_speak      );
        mBtnLocation             =  (ImageButton)   findViewById(R.id.button_location   );
        mBtnSnapshot             =  (ImageButton)   findViewById(R.id.button_snapshot   );
        mTxtGPSLongitude         =  (TextView)      findViewById(R.id.txtGpsLongitudeContent);
        mTxtGPSLatitude          =  (TextView)      findViewById(R.id.txtGpsLatitudeContent);
        mTxtGPSAltitude          =  (TextView)      findViewById(R.id.txtGpsAltitudeContent);
        mTxtPlaceContent         =  (EditText)      findViewById(R.id.txtPlaceContent);
        mBtnPlacePicker          =  (ImageButton)   findViewById(R.id.button_place_picker);


        mTxtGPSLongitude.setText("Starting...");
        mTxtGPSLatitude .setText("Starting...");
        mTxtGPSAltitude .setText("Starting...");


        initGoogleApi();

        entryID = 0;
        mBtnLocationPressed = false;
        mPlacebookEntry = new PlacebookEntry( this, entryID++);
        mPlacebookEntries = new ArrayList<>();

        mBtnSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        });

        mBtnSpeak . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to be executed when the button is clicked .
                Toast.makeText(context, "Speak Button is clicked", Toast.LENGTH_SHORT).show();
                dispatchSpeechInputIntent();
            }
        }) ;

        mBtnPlacePicker . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to be executed when the button is clicked .
                Toast.makeText(context, "Place Picker Button is clicked", Toast.LENGTH_SHORT).show();
                launchPlacePicker();
            }
        }) ;


        mBtnLocation . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Location Button Pressed", Toast.LENGTH_SHORT).show();

                mBtnLocationPressed = !mBtnLocationPressed;
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) context);
                }
                else {
                    handleNewLocation(location);
                }

                mBtnLocationPressed = !mBtnLocationPressed;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch( id ){
            case R.id.action_search             :   openSearch();   return true;
            case R.id.action_new_place          :   new_place();    return true;
            case R.id.action_delete_place       :   delete_place(); return true;
            //case R.id.action_edit_place         :   /*edit_place();*/   return true;
            case R.id.action_view_all           :   dispatchViewAllPlaces();    return true;

            default:
                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }



    private void delete_place() {

        Toast.makeText(context, "Clears Everything", Toast.LENGTH_SHORT).show();

        mTxtPlaceContent.getText().clear();
        mPlaceDescription.getText().clear();
        mPlacebookEntry.setName(null);

        mPlacebookEntries.clear();
        entryID = 0;
    }

    private void new_place() {


        if(mPlacebookEntry.getPhotoPath() == null){
            Toast.makeText(context, "try taking picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if(change != -1){
            for(PlacebookEntry temp: mPlacebookEntries){
                if ( (mTxtPlaceContent.getText().toString()).equals(temp.getName())  && temp != mPlacebookEntries.get(change)  ){
                    Toast.makeText(context, "Make unique name before adding", Toast.LENGTH_SHORT).show();
                    return;
                }

            }


            mPlacebookEntries.get(change).setName(mTxtPlaceContent.getText().toString());
            mPlacebookEntries.get(change).setDescription(mPlaceDescription.getText().toString());
            mPlacebookEntries.get(change).setPhotoPath(mPlacebookEntry.getPhotoPath());

            mPlacebookEntry = null;
            mPlacebookEntry = new PlacebookEntry(context, entryID);

            mPlaceDescription.getText().clear();
            mTxtPlaceContent.getText().clear();

            change = -1;
            return;

        }


        for(PlacebookEntry temp: mPlacebookEntries){
            if ( (mTxtPlaceContent.getText().toString()).equals(temp.getName())){
                Toast.makeText(context, "Make unique name before adding", Toast.LENGTH_SHORT).show();
                return;
            }

        }


        mPlacebookEntry.setName(mTxtPlaceContent.getText().toString());
        mPlacebookEntry.setDescription(mPlaceDescription.getText().toString());
        mPlacebookEntries.add(mPlacebookEntry);
        mPlacebookEntry = null;
        mPlacebookEntry = new PlacebookEntry(context, entryID++);

        mPlaceDescription.getText().clear();
        mTxtPlaceContent.getText().clear();


        Toast.makeText(context, "Place added into list", Toast.LENGTH_SHORT).show();
    }



    // Call init GoogleApi () from MainActivity . onCreate ()
   private void initGoogleApi () {
        mGoogleApiClient = new GoogleApiClient
                . Builder ( this )
                . addApi ( Places . GEO_DATA_API )
                . addApi ( Places. PLACE_DETECTION_API )
                . addApi(LocationServices.API)
                . addConnectionCallbacks(this)
                . addOnConnectionFailedListener(this)
                . build() ;


       // Create the LocationRequest object
       mLocationRequest = LocationRequest.create()
               .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
               .setInterval(10 * 1000)        // 10 seconds, in milliseconds
               .setFastestInterval(1000); // 1 second, in milliseconds



    }


    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        double currentAltitude = location.getAltitude();





        if(mBtnLocationPressed) {
            mTxtGPSLongitude.setText(Double.toString(currentLongitude));
            mTxtGPSLatitude.setText(Double.toString(currentLatitude));
            mTxtGPSAltitude.setText(Double.toString(currentAltitude));

            String Place = Double.toString(currentLatitude) + Double.toString(currentLongitude);
            mTxtPlaceContent.setText(Place);

        }


    }


    private File createImageFile() throws IOException {
        String fname            = "Placebook_";
        String timestamp        = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName    = "JPEG_" + timestamp + "_" + fname;

        File storageDir         =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        //mPlacebookEntries.setPhotoPath (image.getAbsolutePath());

        mPlacebookEntry.setPhotoPath(image.getAbsolutePath()); // or is it this

        return image;
    }

    /********************************** DISPATCH AREA *******************************************/
    /********************************************************************************************/


    // Call d i s p a t c h S p e e c h I n p u t I n t e n t () when the speech - to - text button is clicked .
    void dispatchSpeechInputIntent () {
        Intent intent = new Intent ( RecognizerIntent . ACTION_RECOGNIZE_SPEECH );
        intent . putExtra (
                RecognizerIntent . EXTRA_LANGUAGE_MODEL ,
                RecognizerIntent. LANGUAGE_MODEL_FREE_FORM );
        intent . putExtra (
                RecognizerIntent . EXTRA_LANGUAGE , Locale. getDefault() );
        intent . putExtra (
                RecognizerIntent . EXTRA_PROMPT ,
                getString ( R. string . speech_prompt ) );
        try {
            startActivityForResult ( intent , REQUEST_SPEECH_INPUT );
        } catch ( ActivityNotFoundException a ) {
// Handle Exception
        }
    }


    // Call d i s p a t c h T a k e P i c t u r e I n t e n t () when the camera button is clicked .
    private void dispatchTakePictureIntent () throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there ’s a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }


            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }

    // Call d i s p a t c h V i e w A l l P l a c e s () when its menu command is selected .
    private void dispatchViewAllPlaces () {

        if (mPlacebookEntries.size() == 0){
            Toast.makeText(context, "Add a place before viewing all", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent ( this , HistoryActivity . class );
        intent . putParcelableArrayListExtra ( VIEW_ALL_KEY , mPlacebookEntries );
        try {
            startActivityForResult ( intent , REQUEST_VIEW_ALL );
        } catch ( ActivityNotFoundException a ) {}
    }


    // Open Search Activity
    private void openSearch() {
        if (mPlacebookEntries.size() == 0){
            Toast.makeText(context, "Add Place before searching", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent ( this , SearchActivity . class );
        intent . putParcelableArrayListExtra ( SEARCH_ALL_KEY , mPlacebookEntries );
        try {
            startActivityForResult ( intent , REQUEST_SEARCH );
        } catch ( ActivityNotFoundException a ) {

            Toast.makeText(context, "not there", Toast.LENGTH_SHORT).show();
        }
    }


    // Call l a u n c h P l a c e P i c k e r () when the Pick -A - Place button is clicked .
    private void launchPlacePicker () {
        PlacePicker . IntentBuilder builder = new PlacePicker. IntentBuilder () ;
        Context context = getApplicationContext () ;
        try {
            startActivityForResult ( builder . build ( context ) , REQUEST_PLACE_PICKER );
        } catch ( GooglePlayServicesRepairableException e) {
// Handle exception - Display a Toast message
            Toast.makeText(context, "Google Play Services probably broken", Toast.LENGTH_SHORT).show();
        } catch ( GooglePlayServicesNotAvailableException e ) {
// Handle exception - Display a Toast message
            Toast.makeText(context, "Google Play Services not available", Toast.LENGTH_SHORT).show();
        }
    }

    /****************************** END DISPATCH AREA *******************************************/
    /********************************************************************************************/

    @Override
    protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {

        /*if(requestCode == REQUEST_VIEW_ALL) {
            //Log.v("trying to get back stuff",)
            if(data == null){
                Log.v("REsult stuff", "Data is null");
            }
        }*/

        if ( resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE && data != null ) {
            // Save previously generated unique file path in current Placebook entry
            Toast.makeText(this, "Image saved to:\n" +
                    data.getData(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();
            Log.v("Image Capture", data.getData().toString());
        } else if ( resultCode == RESULT_OK && requestCode == REQUEST_SPEECH_INPUT && data != null ) {
            ArrayList < String > result =
                    data . getStringArrayListExtra ( RecognizerIntent . EXTRA_RESULTS );
            String spokenText = result.get(0);

            //if not hasFocus() try hasOnClickListeners()
            if(mPlaceDescription.hasFocus()){  // cant test in the library hahahaha...
                mPlaceDescription.setText(spokenText);
            }
            if(mTxtPlaceContent.hasFocus()){    // cant test in the library hahahaha...
                mTxtPlaceContent.setText(spokenText);
            }

            //mPlaceDescription.setText(spokenText);

        } else if ( resultCode == RESULT_OK && requestCode == REQUEST_PLACE_PICKER && data != null ) {
            Place place = PlacePicker . getPlace ( data , this ) ;
// Set place name text view to place . getName () .
            mTxtPlaceContent.setText(place.getName());
        } else if ( resultCode == RESULT_OK && requestCode == REQUEST_VIEW_ALL && data != null ) {

            //Toast.makeText(context, "I am Back", Toast.LENGTH_SHORT).show();

            ArrayList < PlacebookEntry > placebookEntrys =
                    data . getParcelableArrayListExtra ( VIEW_ALL_KEY );
                    // Check if any entry was deleted .
            Log.v("Return Array List", "Size of returned Array List is " + placebookEntrys.size());


            mPlacebookEntries = placebookEntrys;


        }else if ( resultCode == REQUEST_CHANGE && requestCode == REQUEST_VIEW_ALL && data != null ){
            change = data.getIntExtra("edit place", -1);
            mPlacebookEntries = data.getParcelableArrayListExtra(VIEW_ALL_KEY);
            mTxtPlaceContent.setText(mPlacebookEntries.get(change).getName());
            mPlaceDescription.setText(mPlacebookEntries.get(change).getDescription());
            mPlacebookEntry.setPhotoPath(mPlacebookEntries.get(change).getPhotoPath());
            old_id = mPlacebookEntries.get(change).getIDNumeral();

        }else if ( resultCode == RESULT_OK && requestCode == REQUEST_SEARCH && data != null ) {

            ArrayList < PlacebookEntry > placebookEntrys =
                    data . getParcelableArrayListExtra ( SEARCH_ALL_KEY );
            // Check if any entry was deleted .
            Log.v("Return Array List", "Size of returned Array List is " + placebookEntrys.size());
            mPlacebookEntries = placebookEntrys;
        }else if ( resultCode == REQUEST_CHANGE && requestCode == REQUEST_SEARCH && data != null ){
            change = data.getIntExtra("edit place", -1);
            mPlacebookEntries = data.getParcelableArrayListExtra(SEARCH_ALL_KEY);
            mTxtPlaceContent.setText(mPlacebookEntries.get(change).getName());
            mPlaceDescription.setText(mPlacebookEntries.get(change).getDescription());
            mPlacebookEntry.setPhotoPath(mPlacebookEntries.get(change).getPhotoPath());
            old_id = mPlacebookEntries.get(change).getIDNumeral();

        }



    }




    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
