package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.lon.R;

public class Reset_mail extends AppCompatActivity {
Button btn_Back_Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_mail);
        addControls();
        addEvents();
    }
    private  void  addControls(){
        btn_Back_Login = findViewById(R.id.btn_Back_login);
    }
    private void addEvents(){
        btn_Back_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reset_mail.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}