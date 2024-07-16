package project.lon.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import Controls.MusicAdapter;
import Controls.MusicController;
import Models.Music;
import project.lon.R;


public class FragMain extends Fragment {


    ActionBar actionBar;
    BottomNavigationView btnNav;
    ImageView imgAvatar;
    ListView lstView;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    String urlDefault = "https://cdn0.iconfinder.com/data/icons/seo-web-4-1/128/Vigor_User-Avatar-Profile-Photo-01-512.png";

    MusicAdapter musicAdapter;
    ArrayList<Music> lvDataMusic ;
    MusicController musicController = new MusicController();


//    String urlVpop = "https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/playlists?id=6rpiU3z2AlQKR9IV1itdnz";
//    String urlRelax = "https://v1.nocodeapi.com/dangkhoa/spotify/lvTawhkqIEhjcdsm/playlists?id=522ffyAc2JqipH5PObiAin";
//    String urlWorkout = "https://v1.nocodeapi.com/dangkhoa/spotify/lvTawhkqIEhjcdsm/playlists?id=0CjPp1DbD2SE9a990lywVH";
//    String urlTopsongs = "https://v1.nocodeapi.com/dangkhoa/spotify/lvTawhkqIEhjcdsm/playlists?id=1MLZbMpiIhlGe9qK52L4YN";
    Button btnVpop, btnRelax, btnWorkout , btnTopsongs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_main, container, false);
        btnNav = view.findViewById(R.id.btnNav);
        lstView = view.findViewById(R.id.lstMusic);
        imgAvatar = view.findViewById(R.id.imgAvatar);


        btnVpop = view.findViewById(R.id.buttonVpop);
        btnRelax = view.findViewById(R.id.btn_Relax);
        btnWorkout = view.findViewById(R.id.btn_Wordout);
        btnTopsongs = view.findViewById(R.id.btn_Topsong);


        AppCompatActivity activity = (AppCompatActivity) getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Main");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        addEvents();
        setupListView();
        //Xu li button api
        btnVpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllDataVpop("https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/playlists?id=1veSSu7spp9IJuh9ai5NkZ");
            }
        });

        btnRelax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllDataVpop("https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/playlists?id=100CTKxd8KGMgc0lbn7RNJ");
            }
        });
        btnWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllDataVpop("https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/playlists?id=0RB09ewTnvjNgBUHeRcJRx");
            }
        });
        btnTopsongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllDataVpop("https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/playlists?id=0RB09ewTnvjNgBUHeRcJRx");
            }
        });

        setAvatar();
        //Picasso.get().load(urlDefault.toString()).into(imgAvatar);
        return view;
    }

    private void setAvatar() {
        firebaseUser = firebaseAuth.getCurrentUser();
        // Check provider data to determine if it's Google or Facebook login
        boolean isGoogle = false;
        if (firebaseUser != null && firebaseUser.getProviderData() != null) {
            for (com.google.firebase.auth.UserInfo userInfo : firebaseUser.getProviderData()) {
                String providerId = userInfo.getProviderId();
                if (providerId.equals("google.com")){
                    isGoogle = true;
                    break;
                }
            }
        }

        if (isGoogle) {
            // Set avatar for Google or Facebook account
            Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(imgAvatar);
        } else {
            Picasso.get().load(urlDefault.toString()).into(imgAvatar);
        }
    }
    private  void setupListView(){
        lvDataMusic = new ArrayList<>();
        musicAdapter = new MusicAdapter(getActivity(), R.layout.layout_item_albums, lvDataMusic);
        lstView.setAdapter(musicAdapter);
    }
    public void addEvents(){
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });
    }
    // truyen du lieu API Vpop
    public void getAllDataVpop(String urlVpop){
        lvDataMusic.clear();
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlVpop,
                response -> {
            {
                try {
                    parentJsonDatatoArryListVpop(response);
                    musicAdapter.notifyDataSetChanged();
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

    public void parentJsonDatatoArryListVpop(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject tracks = jsonResponse.getJSONObject("tracks");
        JSONArray itemsArray = tracks.getJSONArray("items");

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObject = itemsArray.getJSONObject(i);
            JSONObject trackObject = itemObject.getJSONObject("track");
            JSONObject albumObject = trackObject.getJSONObject("album");
            JSONArray artistArray = trackObject.getJSONArray("artists");
            JSONArray imagesArray = albumObject.getJSONArray("images");

            String idMusic = trackObject.getString("id");
            String imageUrl = imagesArray.getJSONObject(0).getString("url");
            String trackName = trackObject.getString("name");
            String albumName = albumObject.getString("name");
            String preview_url = trackObject.getString("preview_url");
            String artist_name = artistArray.getJSONObject(0).getString("name");
            String id_artist = artistArray.getJSONObject(0).getString("id");

            Music music = new Music(idMusic, imageUrl, trackName, albumName,preview_url, artist_name, id_artist);
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
