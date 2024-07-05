package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import project.lon.R;

public class EditProfile extends AppCompatActivity {
    Button btnCancel, btnSave;
    ImageButton imgEditAvatar;
    EditText edtUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        addControls();
        addEvents();
        Intent intent = getIntent();
    }
    public void addControls(){
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgEditAvatar = (ImageButton) findViewById(R.id.imgAvatar);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
    }

    public void addEvents(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc EditProfile activity và quay lại FragProfile
                finish();
            }
        });
    }
}