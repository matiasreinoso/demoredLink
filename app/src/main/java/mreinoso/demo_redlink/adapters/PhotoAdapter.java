package mreinoso.demo_redlink.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mreinoso.demo_redlink.R;
import mreinoso.demo_redlink.activities.PhotoListActivity;
import mreinoso.demo_redlink.model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.HomeViewHolder> {

    ArrayList<Photo> photoList;
    public static Context mContext;

    public PhotoAdapter(Context context, ArrayList<Photo> contactModel) {
        mContext = context;
        photoList = contactModel;
    }

    @Override
    public PhotoAdapter.HomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_photo, viewGroup, false);

        return new PhotoAdapter.HomeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.HomeViewHolder holder, int position) {
        Photo photoModel = photoList.get(position);
        holder.bindDatos(photoModel);

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final int REQUEST_CODE = 1;

//        @BindView(R.id.tv_id)
        public TextView tv_id;

//        @BindView(R.id.tv_title)
        public TextView tv_text;

//        @BindView(R.id.iv_photo)
        public ImageView iv_photo;

        private int id;

        @Override
        public void onClick(View v) {

        }

        HomeViewHolder(View itemView) {
            super(itemView);

//            ButterKnife.bind(PhotoListActivity.this);

            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
//            itemView.setOnClickListener(this);
        }


        public void bindDatos(Photo photo) {

            id = photo.getId();

            tv_text.setText(photo.getTitle());
            tv_id.setText(photo.getId());

            new DownLoadImageTask(iv_photo).execute(photo.getUrl());
        }


        private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView imageView;

            public DownLoadImageTask(ImageView imageView) {
                this.imageView = imageView;
            }

            protected Bitmap doInBackground(String... urls) {
                String urlOfImage = urls[0];
                Bitmap logo = null;
                try {
                    InputStream is = new URL(urlOfImage).openStream();
                    logo = BitmapFactory.decodeStream(is);
                } catch (Exception e) { // Catch the download exception
                }
                return logo;
            }

            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        }
    }

}
