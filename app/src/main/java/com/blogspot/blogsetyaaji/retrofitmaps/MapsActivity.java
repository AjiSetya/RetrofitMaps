package com.blogspot.blogsetyaaji.retrofitmaps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;
import com.blogspot.blogsetyaaji.retrofitmaps.Model.Propertus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener, GoogleMap.OnMarkerDragListener {

    @BindView(R.id.btnmultimarker)
    Button btnmultimarker;
    @BindView(R.id.btncarilokasi)
    Button btncarilokasi;
    BaseApi baseApi;

    GoogleApiClient googleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Location currentLocation;
    private LatLngBounds.Builder latlangBuilder;
    private LatLng latlangDrag;
    private Marker marker;
    private GoogleMap mapku;
    double dragLat;
    double dragLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        baseApi = BaseApi.BaseApiUtama.buat();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //creating locationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(60 * 1000)       //10 seconds
                .setFastestInterval(1 * 1000);//1 second
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapku = googleMap;

        // konfirmasi permission
        if (
                Build.VERSION.SDK_INT >= 23
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            mapku.setMyLocationEnabled(true);
            mapku.getUiSettings().setMyLocationButtonEnabled(true);
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            currentLocation = location;
            mapku.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    handleNewLocation(currentLocation);
                    return true;
                }
            });
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mapku.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
        mapku.setOnMarkerDragListener(this);
        mapku.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mapku.getUiSettings().setZoomControlsEnabled(true);

    }

    @OnClick({R.id.btnmultimarker, R.id.btncarilokasi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnmultimarker:
                final Call<MProperties> permintaan_lokasi = baseApi.getProperties();
                permintaan_lokasi.enqueue(new Callback<MProperties>() {
                    @Override
                    public void onResponse(Call<MProperties> call, Response<MProperties> response) {
                        List<Propertus> lokasi_properti = response.body().getProperti();
                        Log.d("data : ", "" + new GsonBuilder().setPrettyPrinting().create()
                                .toJson(lokasi_properti));
                        // variabel batas marker
                        latlangBuilder = new LatLngBounds.Builder();
                        for (int a = 0; a < lokasi_properti.size(); a++) {
                            double lat = lokasi_properti.get(a).getLatitude();
                            double lon = lokasi_properti.get(a).getLongitude();
                            // membuat marker
                            marker = mapku.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title(lokasi_properti.get(a).getNama())
                                    .snippet(lokasi_properti.get(a).getJenis())
                                    .icon(getBitmapDescriptor(R.drawable.ic_mama)));
                            // batas marker diambil dari positionmarker
                            latlangBuilder.include(marker.getPosition());
                            // get posisi marker
                            marker.setTag(a);
                        }

                        LatLngBounds bounds = latlangBuilder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        // camera pindah dengan naimasi ke posiisi cu
                        mapku.animateCamera(cu);
                    }

                    @Override
                    public void onFailure(Call<MProperties> call, Throwable t) {

                    }
                });
                break;
            case R.id.btncarilokasi:

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onViewClicked: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onViewClicked: " + e.getMessage());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                latlangDrag = place.getLatLng();
                marker = mapku.addMarker(new MarkerOptions()
                        .position(latlangDrag)
                        .title("Your location")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .snippet("" + place.getName()));
                mapku.moveCamera(CameraUpdateFactory.newLatLng(latlangDrag));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlangDrag, 15);
                mapku.animateCamera(cameraUpdate);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private BitmapDescriptor getBitmapDescriptor(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        } else {
            currentLocation = location;
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        currentLocation = location;

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String address = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
                marker = mapku.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Your location")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .snippet(address));
                mapku.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                mapku.animateCamera(cameraUpdate);
                Log.w("Current loction address", "handleNewLocation: " + strReturnedAddress.toString());
            } else {
                Log.w("Current loction address", "No Address returned!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapku.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "Your location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkGps();

        googleApiClient.connect();
    }

    private void checkGps() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            //notify user
            AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
            gpsAlert.setMessage("Please enable location settings on your device");
            gpsAlert.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settings);
                }
            });
            gpsAlert.setCancelable(false);

            gpsAlert.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //handleNewLocation(location);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        dragLat = dragPosition.latitude;
        dragLong = dragPosition.longitude;
        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);
        Toast.makeText(getApplicationContext(), "Marker Dragged..!", Toast.LENGTH_LONG).show();

    }
}
