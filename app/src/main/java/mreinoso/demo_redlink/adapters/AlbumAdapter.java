package mreinoso.demo_redlink.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import mreinoso.demo_redlink.R;
import mreinoso.demo_redlink.activities.MainActivity;
import mreinoso.demo_redlink.activities.PhotoListActivity;
import mreinoso.demo_redlink.model.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.HomeViewHolder> implements Filterable {

    ArrayList<Album> albumList;
    public static Context mContext;
    private ArrayList<Album> albumListFiltered;

    public AlbumAdapter(Context context, ArrayList<Album> contactModel) {
        mContext = context;
        albumList = contactModel;
    }

    @Override
    public AlbumAdapter.HomeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_album, viewGroup, false);

        return new HomeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        Album albumModel = albumList.get(position);
        holder.bindDatos(albumModel);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    albumListFiltered = albumList;
                } else {
                    ArrayList<Album> filteredList = new ArrayList<>();
                    for (Album value : albumList) {
                        if (value.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(value);
                        }
                    }

                    albumListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = albumListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                albumListFiltered = (ArrayList<Album>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final int REQUEST_CODE = 1;
        //        @BindView(R.id.tv_name)
        public TextView tv_id;
        //        @BindView(R.id.tv_address)
        public TextView tv_text;
        private int id;

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, PhotoListActivity.class);
            intent.putExtra(mContext.getString(R.string.idAlbum), String.valueOf(id));

            ((MainActivity) mContext).startActivityForResult(intent, REQUEST_CODE);
        }

        HomeViewHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);

            itemView.setOnClickListener(this);
        }


        public void bindDatos(Album album) {

            id = album.getId();

            tv_text.setText(album.getTitle());
            tv_id.setText(String.valueOf(album.getId()));
        }


    }

}