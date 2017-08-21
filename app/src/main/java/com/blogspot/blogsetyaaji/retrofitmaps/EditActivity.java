package com.blogspot.blogsetyaaji.retrofitmaps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    @BindView(R.id.txteditnama)
    EditText txteditnama;
    @BindView(R.id.txteditjenis)
    EditText txteditjenis;
    @BindView(R.id.btneditsimpan)
    Button btneditsimpan;

    BaseApi baseApi;
    String id_propeti;
    GoogleMap maptambah;
    Marker marker;
    private LatLng latlangDrag;
    double dragLat;
    double dragLong;
    String nama;
    String jenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        baseApi = BaseApi.BaseApiUtama.buat();

        Intent painem = getIntent();
         nama = painem.getStringExtra("nama");
         jenis = painem.getStringExtra("jenis");
        dragLat = painem.getDoubleExtra("lat", 0.0);
        dragLong = painem.getDoubleExtra("long", 0.0);
        txteditnama.setText(nama);
        txteditjenis.setText(jenis);
        id_propeti = painem.getStringExtra("id");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapedit);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.btneditsimpan)
    public void onViewClicked() {
        String nama = txteditnama.getText().toString();
        String jenis = txteditjenis.getText().toString();
        // tambah data
        Call<MProperties> edit_properti = baseApi.editProperti(id_propeti, nama, jenis, dragLat, dragLong);
        edit_properti.enqueue(new Callback<MProperties>() {
            @Override
            public void onResponse(Call<MProperties> call, Response<MProperties> response) {
                if (response.body().getSukses() == true) {
                    Toast.makeText(EditActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(EditActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MProperties> call, Throwable t) {
                Toast.makeText(EditActivity.this, "Gagagl menjangkau server"
                        , Toast.LENGTH_SHORT).show();
                Log.e("error :", t.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maptambah = googleMap;

        // konfirmasi permission
        if (
                Build.VERSION.SDK_INT >= 23
                        && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(dragLat, dragLong);
        maptambah.addMarker(new MarkerOptions().position(sydney).title(nama).draggable(true));
        maptambah.setOnMarkerDragListener(this);
        maptambah.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        maptambah.animateCamera(cameraUpdate);
        maptambah.getUiSettings().setZoomControlsEnabled(true);
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
        Toast.makeText(getApplicationContext(), "Loksi dipilih", Toast.LENGTH_LONG).show();

    }
}
