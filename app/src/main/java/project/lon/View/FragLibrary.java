package project.lon.View;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        return view;
    }
}