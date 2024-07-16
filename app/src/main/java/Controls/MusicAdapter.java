package Controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Models.Music;
import project.lon.R;

public class MusicAdapter extends ArrayAdapter {
    Context context;
    int layout;
    ArrayList<Music> lsData;

    public MusicAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Music> data) {
        super(context, resource, data);
        this.context = context;
        this.lsData = data;
        this.layout = resource;
    }


//    @Override
//    public int getCount() {
//        return 0;
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Music music = lsData.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout,null);
        }

        //Gọi ảnh, tên bài hát, tên albums
        ImageView imageView = convertView.findViewById(R.id.imgAlbum);
        Picasso.get().load(music.getImages()).into(imageView);

        TextView tvTrackName = convertView.findViewById(R.id.tvTrackName);
        tvTrackName.setText(music.getTrack_name());
        TextView tvAlbumName = convertView.findViewById(R.id.tvAlbumName);
        tvAlbumName.setText(music.getAlbum_name());

        return convertView;
    }

//    private void handleItemClick(int position) {
//        // Xử lý sự kiện click cho phần tử tại vị trí position
//        String selectedItem = items.get(position);
//        // Thực hiện các hành động cần thiết
//    }
}
