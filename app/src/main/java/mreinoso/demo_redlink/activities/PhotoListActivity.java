package mreinoso.demo_redlink.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import mreinoso.demo_redlink.R;
import mreinoso.demo_redlink.adapters.PhotoAdapter;
import mreinoso.demo_redlink.model.Photo;
import mreinoso.demo_redlink.utils.DemoDB;

public class PhotoListActivity extends AppCompatActivity {

    private RecyclerView rv_photos;
    private DemoDB demoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_photos = (RecyclerView) findViewById(R.id.rv_album);

        demoDB = new DemoDB(getApplicationContext());
        demoDB = DemoDB.getInstance(getApplicationContext());


        String id = getIntent().getStringExtra(getString(R.string.idAlbum));

        loadRecyclePhotos(id);

    }

    private void loadRecyclePhotos(String id) {

        ArrayList<Photo> photos = demoDB.getPhoto(id);

        rv_photos.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv_photos.setLayoutManager(llm);

        PhotoAdapter photoAdapter = new PhotoAdapter(PhotoListActivity.this, photos);

        rv_photos.setAdapter(photoAdapter);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
