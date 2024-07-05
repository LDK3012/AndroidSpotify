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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import project.lon.R;


public class FragSearching extends Fragment {

    ActionBar actionBar;
    BottomNavigationView btnNav;
    ListView lstView;
    EditText edtSearch;
    ImageButton imgAvatar;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    String urlDefault = "https://www.iconfinder.com/icons/2147887/avatar_photo_profile_user_icon";

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

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Search");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        Picasso.get().load(urlDefault).into(imgAvatar);

        return view;
    }
    public void addEvents(){

    }
}