package Controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Artists;
import project.lon.R;

public class ArtistsAdapter extends ArrayAdapter<Artists> {
    Context context;
    int resource;
    List<Artists> lsDataArtists;

    public ArtistsAdapter(@NonNull Context context, int resource, @NonNull List<Artists> lsDataArtists) {
        super(context, resource, lsDataArtists);
        this.context = context;
        this.resource = resource;
        this.lsDataArtists = lsDataArtists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        // Gọi ảnh, tên và bài hát nghệ sĩ
        Artists artists = lsDataArtists.get(position);
        ImageView imageView = convertView.findViewById(R.id.imgArtist);
        Picasso.get().load(artists.getImages_albums()).resize(100, 100).into(imageView);

        TextView tvIdArtist = convertView.findViewById(R.id.tvArtistName);
        tvIdArtist.setText(artists.getId_artists());

        TextView tvArtistsName = convertView.findViewById(R.id.tvTrackName);
        tvArtistsName.setText(artists.getName_artists());

        return convertView;
    }
}
