package com.blogspot.blogsetyaaji.retrofitmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahActivity extends AppCompatActivity {

    @BindView(R.id.txttambahnama)
    EditText txttambahnama;
    @BindView(R.id.txttambahjenis)
    EditText txttambahjenis;
    @BindView(R.id.btntambahsimpan)
    Button btntambahsimpan;

    BaseApi baseApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);
        ButterKnife.bind(this);
        baseApi = BaseApi.BaseApiUtama.buat();
    }

    @OnClick(R.id.btntambahsimpan)
    public void onViewClicked() {
        String nama = txttambahnama.getText().toString();
        String jenis = txttambahjenis.getText().toString();
        // tambah data
        Call<MProperties> tambah_properti = baseApi.insertProperti(nama, jenis);
        tambah_properti.enqueue(new Callback<MProperties>() {
            @Override
            public void onResponse(Call<MProperties> call, Response<MProperties> response) {
                if (response.body().getSukses() == true){
                    Toast.makeText(TambahActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TambahActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(TambahActivity.this, response.body().getPesan()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MProperties> call, Throwable t) {
                Toast.makeText(TambahActivity.this, "Gagagl menjangkau server"
                        , Toast.LENGTH_SHORT).show();
                Log.e("error :", t.getMessage());
            }
        });
    }
}
