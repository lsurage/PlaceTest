package bangali.baba.placetest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bangali.baba.placetest.autocomple.component.GoogleAutoCompleteTextView;
import bangali.baba.placetest.autocomple.manual.CustomAutoCompleteTextView;
import bangali.baba.placetest.autocomple.manual.CustomListAdapter;
import bangali.baba.placetest.autocomple.manual.PlaceItem;

public class MainActivity extends AppCompatActivity  {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected LatLngBounds mBounds;
    protected List<PlaceItem> mPlaces;
    protected CustomListAdapter mAdapter;
    GoogleAutoCompleteTextView.AutoCompleteLocationListener autoCompleteLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaces = new ArrayList<PlaceItem>();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitute = location.getLongitude();
            double latitude = location.getLatitude();
            LatLng southwest = new LatLng(longitute, latitude);
            LatLng northeast = new LatLng(longitute, latitude);
            mBounds = new LatLngBounds(southwest, northeast);

        }


        GoogleAutoCompleteTextView autoCompleteLocation = findViewById(R.id.google_autocomplete);

        autoCompleteLocationListener = new GoogleAutoCompleteTextView.AutoCompleteLocationListener() {

            @Override
            public void onItemSelected(Place selectedPlace) {
                Log.d("this is ", (String) selectedPlace.getAddress());
            }
        };
        autoCompleteLocation.setAutoCompleteTextListener(autoCompleteLocationListener);


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        mAdapter = new CustomListAdapter(this, R.layout.place_item , mPlaces);
        final CustomAutoCompleteTextView autoCompleteTextView = findViewById(R.id.autocomplete);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 2) {
                    Task<AutocompletePredictionBufferResponse> results = mGeoDataClient.getAutocompletePredictions(s.toString(), null, null);
                    results.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onSuccess(AutocompletePredictionBufferResponse response) {
                            mPlaces.clear();
                            @SuppressLint("RestrictedApi") Iterator<AutocompletePrediction> iterator = response.iterator();
                            if (iterator.hasNext()) {
                                while (iterator.hasNext()) {
                                    AutocompletePrediction prediction = iterator.next();
                                    mPlaces.add(new PlaceItem(prediction.getPrimaryText(null).toString(), prediction.getSecondaryText(null).toString(), prediction.getPlaceId()));
                                    Log.i("prediction", prediction.getFullText(null).toString());
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            response.release();

                        }
                    });
                    results.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error while fetching suggestions : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteTextView.setText(mPlaces.get(position).getPrimary());
            }
        });

        autoCompleteTextView.setAdapter(mAdapter);

    }


}