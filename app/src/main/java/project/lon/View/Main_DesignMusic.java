package project.lon.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import project.lon.R;
import project.lon.View.FragMain;
import project.lon.View.FragProfile;
import project.lon.View.FragSearching;

public class Main_DesignMusic extends AppCompatActivity {
    ActionBar actionBar;
    FrameLayout frame;
    BottomNavigationView btnNav;
    ListView lstView;
    MusicController musicController = new MusicController();
    public String music;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_design_music);
        music = "music";
        addControls();
        addEvents();
        loadFragment();
    }
    public void addControls(){
        actionBar = getSupportActionBar();
        frame = (FrameLayout) findViewById(R.id.mainFrame);
        btnNav = (BottomNavigationView) findViewById(R.id.btnNav);
        lstView = (ListView) findViewById(R.id.lstMusic);
    }

    public void addEvents(){
        btnNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.item_btt_nav_main){
                    selectedFragment = new FragMain();
                } else if (item.getItemId() == R.id.item_btt_nav_search) {
                    selectedFragment = new FragSearching();
                } else if (item.getItemId() == R.id.item_btt_nav_library) {
                    selectedFragment = new FragLibrary();
                } else if (item.getItemId() == R.id.item_btt_nav_profile) {
                    selectedFragment = new FragProfile();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment1 = new FragMain();
        fragmentTransaction.add(R.id.mainFrame,fragment1);
        fragmentTransaction.commit();
    }

    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
    }
}