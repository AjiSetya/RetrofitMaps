package com.blogspot.blogsetyaaji.retrofitmaps;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;
import com.blogspot.blogsetyaaji.retrofitmaps.Model.Propertus;
import com.google.gson.GsonBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView lvproperti;
    BaseApi baseApi;
    List<Propertus> list_properti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, TambahActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        baseApi = BaseApi.BaseApiUtama.buat();
        lvproperti = (RecyclerView) findViewById(R.id.lvproperti);
        lvproperti.setLayoutManager(new LinearLayoutManager(this));
        tampilData();
        // aksi ketika list dipilih
        lvproperti.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    final int position = rv.getChildAdapterPosition(child);
                    final String id_properti = list_properti.get(position).getId();
                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Choose an action");
                    // add a list
                    String[] animals = {"Lihat Detail", "Edit", "Hapus"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    // pindah ke detail data
                                    break;
                                case 1:
                                    // edit data
                                    edit_data(id_properti, position);
                                    tampilData();
                                    break;
                                case 2:
                                    // hapus data
                                    hapus_data(id_properti);
                                    tampilData();
                                    break;
                            }
                        }
                    });
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void hapus_data(String id) {
        Call<MProperties> permintaan_hapus = baseApi.hapusProperti(id);
        permintaan_hapus.enqueue(new Callback<MProperties>() {
            @Override
            public void onResponse(Call<MProperties> call, Response<MProperties> response) {
                if (response.body().getSukses() == true) {
                    Toast.makeText(MainActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                    tampilData();
                } else {
                    Toast.makeText(MainActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                }
                Log.d("respon : ", new GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body()));
            }

            @Override
            public void onFailure(Call<MProperties> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal menjaungkau server"
                        , Toast.LENGTH_SHORT).show();
                Log.e("error :", t.getMessage());
            }
        });
    }

    private void edit_data(String id, int posisi) {
        Intent inten = new Intent(MainActivity.this, EditActivity.class);
        inten.putExtra("nama", list_properti.get(posisi).getNama());
        inten.putExtra("jenis", list_properti.get(posisi).getJenis());
        inten.putExtra("lat", list_properti.get(posisi).getLatitude());
        inten.putExtra("long", list_properti.get(posisi).getLongitude());
        inten.putExtra("id", id);
        startActivity(inten);
    }

    private void tampilData() {
        Call<MProperties> permintaan = baseApi.getProperties();
        permintaan.enqueue(new Callback<MProperties>() {
            @Override
            public void onResponse(Call<MProperties> call, Response<MProperties> response) {
                Log.d("data : ", "" + new GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body()));
                // msaukkan data peroperti ke dalam model Propertus
                list_properti = response.body().getProperti();
                PropertiAdapter adapter = new PropertiAdapter(MainActivity.this, list_properti);
                lvproperti.setAdapter(adapter);
                Toast.makeText(MainActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MProperties> call, Throwable t) {
                Log.e("Gagal : ", t.getMessage());
                Toast.makeText(MainActivity.this, "Gagal menjangkau server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
