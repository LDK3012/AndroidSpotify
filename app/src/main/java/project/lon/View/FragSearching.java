package project.lon.View;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Toast;

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
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Search");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        addEvents();
        setAvatar();
        //Picasso.get().load(urlDefault.toString()).into(imgAvatar);
        return view;
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
}