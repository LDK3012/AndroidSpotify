package project.lon.View;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Models.Music;
import project.lon.R;


public class FragSearching extends Fragment {

    ActionBar actionBar;
    BottomNavigationView btnNav;
    ListView lstView;
    EditText edtSearch;
    ImageButton imgAvatar;
    ImageView search_btn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    String urlDefault = "https://www.iconfinder.com/icons/2147887/avatar_photo_profile_user_icon";
    ArrayList<Music> lvDataMusic = new ArrayList<>();
    MusicAdapter musicAdapter;
    //Main_DesignMusic mainDesignMusic = (Main_DesignMusic) getActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addEvents();
        }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_searching, container, false);
        btnNav = view.findViewById(R.id.btnNav);
        lstView = view.findViewById(R.id.lstMusic);
        edtSearch = view.findViewById(R.id.edtSearch);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        search_btn = view.findViewById(R.id.search_btn);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Search");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        Picasso.get().load(urlDefault).into(imgAvatar);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(edtSearch.getText().toString());
            }
        });

        return view;
    }
    public void addEvents(){

    }

    public void getData(String data){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/search?q="+data+"&type=track";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    {
                        try {
                            loadData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Lỗi data", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    public void loadData(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject tracks = jsonResponse.getJSONObject("tracks");
        JSONArray items = tracks.getJSONArray("items");
        for(int i=0;i<items.length();i++)
        {
            JSONObject itemObject = items.getJSONObject(i);
            JSONObject albumObject = itemObject.getJSONObject("album");
            JSONArray artistArray = itemObject.getJSONArray("artists");
            JSONArray imagesArray = albumObject.getJSONArray("images");

            String idMusic = itemObject.getString("id");
            String imageUrl = imagesArray.getJSONObject(0).getString("url");
            String trackName = itemObject.getString("name");
            String albumName = albumObject.getString("name");
            String preview_url = itemObject.getString("preview_url");
            String artist_name = artistArray.getJSONObject(0).getString("name");
            String id_artist = artistArray.getJSONObject(0).getString("id");

            Music music = new Music(idMusic,imageUrl, trackName, albumName,preview_url,artist_name,id_artist);
            lvDataMusic.add(music);
        }
        musicAdapter = new MusicAdapter(getContext(), R.layout.layout_item_albums, lvDataMusic);
        lstView.setAdapter(musicAdapter);
        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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