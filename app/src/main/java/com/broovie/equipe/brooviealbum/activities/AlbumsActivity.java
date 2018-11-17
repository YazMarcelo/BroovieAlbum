package com.broovie.equipe.brooviealbum.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.broovie.equipe.brooviealbum.R;
import com.broovie.equipe.brooviealbum.bootstrap.APIClient;
import com.broovie.equipe.brooviealbum.model.Album;
import com.broovie.equipe.brooviealbum.resources.AlbumResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumsActivity extends AppCompatActivity {

    AlbumResource apiAlbumResouce;
    ListView lstAlbums;
    EditText txtDescAlbum;
    EditText txtIdUsuer;
    EditText txtIdAlbum;
    List<Album> listAlbum;
    List<HashMap<String,String>> colecao =
            new ArrayList<HashMap<String,String>>();
    SimpleAdapter simpleAdapter;
    int indexUpd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        lstAlbums = findViewById(R.id.listViewAlbum);
        txtDescAlbum = findViewById(R.id.txt_desc_album);
        txtIdUsuer = findViewById(R.id.txt_id_usuer);
        txtIdAlbum = findViewById(R.id.txt_id_album);


        apiAlbumResouce = APIClient.getClient().create(AlbumResource.class);

        registerForContextMenu(lstAlbums);

        getAlbums();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_item, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        indexUpd = info.position;
        switch (item.getItemId()){
            case R.id.btn_update:
                Toast.makeText(this, "Alterar Selecionado", Toast.LENGTH_SHORT).show();
                updAlbum(listAlbum.get(indexUpd));
                return true;
            case R.id.btn_delete:
                Toast.makeText(this, "Delete Selecionado", Toast.LENGTH_SHORT).show();
                deleteAlbum(listAlbum.get(indexUpd));
                listAlbum.remove(item);
                colecao.remove(indexUpd);
                simpleAdapter.notifyDataSetChanged();
                return true;
                default:
                    return super.onContextItemSelected(item);
        }
    }

    public void addAlbum(View view){
        String titleAlbum;
        int idUser,idAlbum;
        idUser = Integer.parseInt(txtIdUsuer.getText().toString());
        idAlbum = Integer.parseInt(txtIdAlbum.getText() != null ? txtIdAlbum.getText().toString() : "");
        titleAlbum = txtDescAlbum.getText().toString();


        final Album abm = Album.builder()
                .id(idAlbum)
                .userId(idUser)
                .title(titleAlbum)
                .build();
        if(idAlbum > 0){
            Call<Album> upd = apiAlbumResouce.put(String.valueOf(abm.getId()),abm);
            upd.enqueue(new Callback<Album>() {
                @Override
                public void onResponse(Call<Album> call, Response<Album> response) {
                    Album a = response.body();
                    HashMap<String,String> mapUser = new HashMap<String,String>();
                    mapUser.put("id",String.valueOf(a.getId()));
                    mapUser.put("title",a.getTitle());
                    colecao.set(indexUpd,mapUser);
                    simpleAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Album \"" + a.getTitle() + "\" alterado", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Album> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }else
        {
            Call<Album> post = apiAlbumResouce.post(abm);
            post.enqueue(new Callback<Album>() {
                @Override
                public void onResponse(Call<Album> call, Response<Album> response) {
                    Album a = response.body();
                    HashMap<String,String> mapUser = new HashMap<String,String>();
                    mapUser.put("id",String.valueOf(a.getId()));
                    mapUser.put("title",a.getTitle());
                    colecao.add(mapUser);
                    simpleAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Album \"" + a.getTitle() + "\" inserido", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Album> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getAlbums(){
        Call<List<Album>> get = apiAlbumResouce.get();

        get.enqueue(new Callback<List<Album>>() {

            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                listAlbum = response.body();

                for (Album a : listAlbum){
                    HashMap<String,String> mapUser = new HashMap<String,String>();
                    mapUser.put("id",String.valueOf(a.getId()));
                    mapUser.put("title",a.getTitle());

                    colecao.add(mapUser);
                }


                String[] from = {"id","title"};
                int[] to = {R.id.id_album,R.id.nome_album};

                simpleAdapter =
                        new SimpleAdapter(
                                getApplicationContext(),
                                colecao,
                                R.layout.item_album,
                                from,
                                to);

                lstAlbums.setAdapter(simpleAdapter);
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteAlbum(final Album abm){
        Call<Void> delete = apiAlbumResouce.delete(String.valueOf(abm.getId()));
        delete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "Album \"" + abm.getTitle() + "\" deletado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void updAlbum(final Album abm){
        txtIdAlbum.setText(String.valueOf(abm.getId()));
        txtIdUsuer.setText(String.valueOf(abm.getUserId()));
        txtDescAlbum.setText(abm.getTitle());
    }
}
