package project.lon.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import project.lon.R;
import project.lon.View.EditProfile;


public class FragProfile extends Fragment {

    ActionBar actionBar;
    BottomNavigationView btnNav;
    ListView lstView;
    Button btnEditProfile, btnLogout;

    FirebaseAuth firebaseAuth;

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
        firebaseAuth = FirebaseAuth.getInstance();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Profile");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        addEvents();
        return view;
    }
    public void addEvents(){
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
                        firebaseAuth.signOut();
                        // Start LoginActivity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
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
}