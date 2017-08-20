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

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.txteditnama)
    EditText txteditnama;
    @BindView(R.id.txteditjenis)
    EditText txteditjenis;
    @BindView(R.id.btneditsimpan)
    Button btneditsimpan;

    BaseApi baseApi;
    String id_propeti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        baseApi = BaseApi.BaseApiUtama.buat();

        Intent painem = getIntent();
        String nama = painem.getStringExtra("nama");
        String jenis = painem.getStringExtra("jenis");
        txteditnama.setText(nama);
        txteditjenis.setText(jenis);
        id_propeti = painem.getStringExtra("id");
    }

    @OnClick(R.id.btneditsimpan)
    public void onViewClicked() {
        String nama = txteditnama.getText().toString();
        String jenis = txteditjenis.getText().toString();
        // tambah data
        Call<MProperties> edit_properti = baseApi.editProperti(id_propeti, nama, jenis);
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
}
