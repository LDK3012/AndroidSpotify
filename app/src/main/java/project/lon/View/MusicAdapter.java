package project.lon.View;

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

import Models.Music;
import project.lon.R;

public class MusicAdapter extends ArrayAdapter<Music> {
    Context context;
    int resource;
    List<Music> lsData;

    public MusicAdapter(@NonNull Context context, int resource, @NonNull List<Music> lsData) {
        super(context, resource, lsData);
        this.context = context;
        this.resource = resource;
        this.lsData = lsData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Music music = lsData.get(position);
        // Gọi ảnh, tên bài hát, tên albums
        ImageView imageView = convertView.findViewById(R.id.imgAlbum);
        Picasso.get().load(music.getImages()).resize(100, 100).into(imageView);

        TextView tvTrackName = convertView.findViewById(R.id.tvTrackName);
        tvTrackName.setText(music.getTrack_name());

        TextView tvAlbumName = convertView.findViewById(R.id.tvAlbumName);
        tvAlbumName.setText(music.getAlbum_name());

        return convertView;
    }
}
