package mreinoso.demo_redlink.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filterable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mreinoso.demo_redlink.R;
import mreinoso.demo_redlink.adapters.AlbumAdapter;
import mreinoso.demo_redlink.model.Album;
import mreinoso.demo_redlink.model.Photo;
import mreinoso.demo_redlink.utils.DemoDB;
import mreinoso.demo_redlink.utils.HttpHandler;
import mreinoso.demo_redlink.utils.ItemDecorator;

public class MainActivity extends AppCompatActivity {

    private DemoDB demoDB;
    private RecyclerView rv_album;
    private SearchView searchView;
    private AlbumAdapter mAdapter;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        rv_album = (RecyclerView) findViewById(R.id.rv_album);

        whiteNotificationBar(rv_album);

        demoDB = new DemoDB(getApplicationContext());
        demoDB = DemoDB.getInstance(getApplicationContext());

        new GetData().execute();

        loadRecycleView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            ArrayList<Album> albums = demoDB.getAlbum();

            if (albums.isEmpty()) {
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response

                parseData(sh, getResources().getString(R.string.album_access));
                parseData(sh, getResources().getString(R.string.photos_access));
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            loadRecycleView();
        }
    }

    private void loadRecycleView() {
        ArrayList<Album> albums = demoDB.getAlbum();

        if (!albums.isEmpty()) {

            if (mDialog != null) {
                if (mDialog.isShowing()) mDialog.dismiss();
            }


            mAdapter = new AlbumAdapter(MainActivity.this, albums);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rv_album.setLayoutManager(mLayoutManager);
            rv_album.setItemAnimator(new DefaultItemAnimator());
            rv_album.addItemDecoration(new ItemDecorator(this, DividerItemDecoration.VERTICAL, 36));

            rv_album.setAdapter(mAdapter);


        } else {
            mDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading_title),
                    getResources().getString(R.string.loading_msj), true);
        }
    }

    private void parseData(HttpHandler sh, String access) {

        String jsonStr = sh.makeServiceCall(getResources().getString(R.string.url_main) + access);

        if (jsonStr != null) {
            try {
                JSONArray json = new JSONArray(jsonStr);

                for (int i = 0; i < json.length(); i++) {
                    JSONObject c = json.getJSONObject(i);


                    switch (access) {
                        case "photos":
                            Photo ph = new Photo();
                            ph.setAlbumId(c.optInt(getResources().getString(R.string.tag_albumID)));
                            ph.setId(c.optInt(getResources().getString(R.string.tag_id)));
                            ph.setTitle(c.optString(getResources().getString(R.string.tag_title)));
                            ph.setUrl(c.optString(getResources().getString(R.string.tag_url)));
                            ph.setThumbnailUrl(c.optString(getResources().getString(R.string.tag_Turl)));
                            demoDB.updatePhoto(ph);
                            break;

                        case "albums":
                            Album alb = new Album();
                            alb.setUserId(c.optInt(getString(R.string.tag_userID)));
                            alb.setId(c.optInt(getResources().getString(R.string.tag_id)));
                            alb.setTitle(c.optString(getResources().getString(R.string.tag_title)));
                            demoDB.updateAlbum(alb);
                            break;
                    }
                }
            } catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}