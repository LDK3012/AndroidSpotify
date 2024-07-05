package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import project.lon.R;

public class Reset_password extends AppCompatActivity {
EditText edt_forget_mail;
Button btn_Send_Link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        addControls();
        addEvents();
    }
    private  void  addControls(){
       edt_forget_mail = findViewById(R.id.edt_forget_mail);
       btn_Send_Link = findViewById(R.id.btn_Send_link);
    }
    private void addEvents(){
        btn_Send_Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reset_password.this, Reset_mail.class);
                startActivity(intent);
            }
        });
    }
}