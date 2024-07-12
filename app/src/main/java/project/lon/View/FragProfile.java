package project.lon.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import project.lon.R;
import project.lon.View.EditProfile;


public class FragProfile extends Fragment {
    TextView txtUsername;
    ActionBar actionBar;
    BottomNavigationView btnNav;
    ListView lstView;
    Button btnEditProfile, btnLogout;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    ImageView imgAvatar;
    String urlDefault = "https://cdn0.iconfinder.com/data/icons/seo-web-4-1/128/Vigor_User-Avatar-Profile-Photo-01-512.png";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_profile, container, false);
        btnNav = view.findViewById(R.id.btnNav);
        lstView = view.findViewById(R.id.lstActivity);
        btnEditProfile = view.findViewById(R.id.btnEdtProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        txtUsername = view.findViewById(R.id.txtUsername);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Lấy tên người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("name", "Chưa có tên");
        txtUsername.setText(username);
        //
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Profile");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        //
        setAvatar();
        addEvents();
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
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để bắt đầu EditProfile activity
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove session
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm");
                builder.setMessage("Bạn có muốn đăng xuất ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove session
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("status_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", "false");
                        editor.apply();
                        //
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Start LoginActivity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                //
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
                //
                AlertDialog dialog = builder.create();
                dialog.show();
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