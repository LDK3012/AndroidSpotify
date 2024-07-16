package project.lon.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Controls.MusicAdapter;
import Models.Music;
import project.lon.R;


public class FragLibrary extends Fragment {

    ActionBar actionBar;
    BottomNavigationView btnNav;
    ListView lst_Music;
    EditText edtLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_frag_library, container, false);
        btnNav = view.findViewById(R.id.btnNav);
        lst_Music = view.findViewById(R.id.lv_music);
       // edtLibrary = view.findViewById(R.id.edt_Library);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Library");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        getData(getActivity());
        return view;
    }

    private void getData(Activity activity)
    {
        SharedPreferences sharedPreferencesLibrary = activity.getSharedPreferences("idUser", activity.MODE_PRIVATE);
        String id = sharedPreferencesLibrary.getString("id","default ID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference user = db.collection(id);
        user.get().addOnSuccessListener(querySnapshot -> {
            ArrayList<Music> lvDataMusic = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Music music = new Music(
                        document.getString("idMusic"),
                        document.getString("images"),
                        document.getString("track_name"),
                        document.getString("album_name"),
                        document.getString("preview_url"),
                        document.getString("artist_name"),
                        document.getString("id_artist"));
                lvDataMusic.add(music);
            }
            loadListView(lvDataMusic);
        }).addOnFailureListener(e -> {
            Toast.makeText(activity, "Tải thất bại", Toast.LENGTH_SHORT).show();
            });

    }

    private void loadListView(ArrayList<Music> lvDataMusic)
    {
        if(getContext()!=null)
        {
            MusicAdapter musicAdapter = new MusicAdapter(getContext(), R.layout.layout_item_albums, lvDataMusic);
            lst_Music.setAdapter(musicAdapter);
            lst_Music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Music music = (Music) adapterView.getItemAtPosition(position);
                    if(music.getPreview_url().toString().equals("null"))
                    {
                        Toast.makeText(getContext(), "Nhạc tạm thời bị xóa", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Main_DesignMusic mainDesignMusic = (Main_DesignMusic) getActivity();
                        mainDesignMusic.musicController.addViewPlayMusic(getActivity(),(Music) adapterView.getItemAtPosition(position));
                    }
                }
            });
        }
    }
}