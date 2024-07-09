package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.lon.R;

public class Reset_password extends AppCompatActivity {
    EditText edt_forget_mail;
    Button btn_Send_Link;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        addControls();
        addEvents();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
    }
    private  void  addControls(){
       edt_forget_mail = findViewById(R.id.edt_forget_mail);
       btn_Send_Link = findViewById(R.id.btn_Send_link);
    }
    private void addEvents(){
        btn_Send_Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_forget_mail.getText().toString().trim();
                if (email.isEmpty()){
                    Toast.makeText(Reset_password.this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
                } else if(!isValidEmail(email)){
                    Toast.makeText(Reset_password.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(email);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void resetPassword(String email){
        DatabaseReference userRef = firebaseDatabase.getReference("users") ;
        userRef.child("Email").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Reset_password.this, "Link khôi phục mật khẩu đã được gửi về email", Toast.LENGTH_LONG).show();  ;
                                Intent intent = new Intent(Reset_password.this , MainActivity.class) ;
                                startActivity(intent);
                                finish();
                            }
                        }
                    }) ;
                }else {
                    Toast.makeText(Reset_password.this, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show();
                    btn_Send_Link.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Reset_password.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
}}