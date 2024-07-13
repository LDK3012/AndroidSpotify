package project.lon.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import project.lon.R;

public class MainActivity extends AppCompatActivity {
    EditText edt_Login_username, edt_Login_pass;
    Button btn_Login_Google, btn_Login;
    TextView tv_Signup, txtForgotPassword;
    private Boolean passwordVisibility = false;
    FirebaseAuth mAuth, pAuth;
    FirebaseDatabase database, mDatabase;
    GoogleSignInClient googleSignInClient;
    String email,password;
    int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        pAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        //
        checkLoginStatus();
        //
        addControls();
        addEvents();
        btn_Login_Google = findViewById(R.id.btn_LoginGoogle);
        btn_Login_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        Intent intent = getIntent();
        boolean signupSuccess = intent.getBooleanExtra("SIGNUP_SUCCESS", false);
        // Hiển thị Toast nếu đăng ký thành công
        if (signupSuccess) {
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLoginStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences("status_login" , MODE_PRIVATE) ;
        String check = sharedPreferences.getString("name" , "") ;
        if (check.equals("true")){
            Intent intent = new Intent(MainActivity.this , Main_DesignMusic.class ) ;
            startActivity(intent);
            finish();
        }
    }

    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        pAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = pAuth.getCurrentUser();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child("Email");
                            reference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Email đã tồn tại
                                        Toast.makeText(MainActivity.this, "Email đã được sử dụng ở phương thức khác", Toast.LENGTH_LONG).show();
                                        pAuth.signOut();
                                        googleSignInClient.signOut();
                                    } else {
                                        // Email chưa tồn tại, tiếp tục quá trình đăng nhập
                                        DatabaseReference userRef = database.getReference().child("users").child("Google").child(user.getUid());
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    // Người dùng chưa tồn tại, tạo mục nhập mới
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("email", user.getEmail());
                                                    map.put("id", user.getUid());
                                                    map.put("name", user.getDisplayName());
                                                    map.put("profile", user.getPhotoUrl().toString());
                                                    userRef.setValue(map);
                                                }

                                                // Lưu thông tin người dùng vào SharedPreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("name", user.getDisplayName());
                                                editor.apply();

                                                // Lưu id của người dùng vào SharedPreferences để làm key cho danh sách nhạc yêu thích
                                                SharedPreferences sharedPreferences1 = getSharedPreferences("idUser", MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                                editor1.putString("id", user.getUid());
                                                editor1.apply();

                                                // Hiển thị thông báo và chuyển đến Main_DesignMusic
                                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, Main_DesignMusic.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(MainActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(MainActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Có lỗi xảy ra khi đăng nhập", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addControls() {
        edt_Login_username = findViewById(R.id.edt_Login_mail);
        edt_Login_pass = findViewById(R.id.edt_Login_pass);
        tv_Signup = findViewById(R.id.tv_Signup);
        btn_Login = findViewById(R.id.btn_Login);
        txtForgotPassword = findViewById(R.id.txtForgotpassword);
    }

    private void addEvents() {
        // phần này hiện và che mật khẩu khi nhập
        edt_Login_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edt_Login_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);

        edt_Login_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edt_Login_pass.getRight() - edt_Login_pass.getCompoundDrawables()[Right].getBounds().width())) {
                        Log.d("MainActivity", "Drawable touched");
                        int selection = edt_Login_pass.getSelectionEnd();
                        if (passwordVisibility) {
                            edt_Login_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            edt_Login_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisibility = false;
                        } else {
                            edt_Login_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);
                            edt_Login_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisibility = true;
                        }
                        edt_Login_pass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        tv_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignupActivity();
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Reset_password.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToSignupActivity() {
        Intent intent = new Intent(MainActivity.this, Signup_Activity.class);
        startActivity(intent);
    }

    public Boolean validateEmail(){
        String val = edt_Login_username.getText().toString();
        if (val.isEmpty()){
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = edt_Login_pass.getText().toString();
        if (val.isEmpty()){
            Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void checkUser() {
        email = edt_Login_username.getText().toString().trim();
        password = edt_Login_pass.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child("Email");
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                SharedPreferences sharedPreferences = getSharedPreferences("status_login",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", "true");
                                editor.apply();
                                //
                                Intent intent = new Intent(MainActivity.this, Main_DesignMusic.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Email hoặc Mật khẩu sai", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                } else {
                    Toast.makeText(MainActivity.this, "Email hoặc Mật khẩu sai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
            }
        });

        }
    }
