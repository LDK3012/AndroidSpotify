package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.lon.R;

public class Signup_Activity extends AppCompatActivity {
    EditText edtMail, edtName, edtConfirmpassword, edtPassword;
    TextView txtLogin;
    Button btn_Signup_Step0;
    FirebaseDatabase sDatabase;

    FirebaseAuth firebaseAuth;
    DatabaseReference sReference;

    Runnable checkEmailVerification;
    Handler handler;
    String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toast.makeText(Signup_Activity.this, "Bạn có thể đăng kí bây giờ", Toast.LENGTH_SHORT).show();
        addControls();
        addEvents();
        firebaseAuth = FirebaseAuth.getInstance();
        handler = new Handler();
        checkEmailVerification = new Runnable() {
            @Override
            public void run() {
                checkEmailVerificationStatus();
                handler.postDelayed(this, 3000);
            }
        };
    }



    private void addControls() {
        edtMail = findViewById(R.id.edt_signup_mail);
        edtName = findViewById(R.id.edt_Signup_name);
        edtConfirmpassword = findViewById(R.id.edt_confirm_password);
        edtPassword = findViewById(R.id.edt_signup_password);
        txtLogin = findViewById(R.id.loginRedirectText);
        btn_Signup_Step0 = findViewById(R.id.btn_Signup_Step0);
    }

    private void addEvents() {
        btn_Signup_Step0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!performLogin()) {
                    return;
                }
                String email = edtMail.getText().toString();
                String password = edtConfirmpassword.getText().toString();

                validateDataAndRegister(email, password);
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private Boolean performLogin() {
        String name = edtName.getText().toString().trim();
        String email = edtMail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmpassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "Nhập mật khẩu có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.charAt(0) == ' ' || password.charAt(password.length() - 1) == ' ') {
            Toast.makeText(this, "Mật khẩu không được bắt đầu hoặc kết thúc bằng khoảng trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void validateDataAndRegister(String email , String password){
        // set btn disable when checking password and otp
        edtPassword.setEnabled(false);
        edtConfirmpassword.setEnabled(false);
        btn_Signup_Step0.setEnabled(false);
        firebaseAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailOTP();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthUserCollisionException){
                    //
                    if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getProviderData() != null){
                        for (UserInfo userInfo : firebaseAuth.getCurrentUser().getProviderData()){
                            if (EmailAuthProvider.PROVIDER_ID.equals(userInfo.getProviderId())){
                                firebaseAuth.getCurrentUser().unlink(userInfo.getProviderId())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                //remove existing email provider
                                                sendEmailOTP();
                                            }
                                        }) ;
                            }
                        }
                    }
                    // i want to remove email provide in siggnIn method here
                }else {
                    Toast.makeText(Signup_Activity.this, "Opps , Có lỗi xảy ra , vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                    // Bật lại các thành phần UI để người dùng có thể thử lại
                    edtPassword.setEnabled(true);
                    edtConfirmpassword.setEnabled(true);
                    btn_Signup_Step0.setEnabled(true);
                }

            }
        });
    }

    private void sendEmailOTP() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup_Activity.this, "Vui lòng kiểm tra hộp thư để xác thực!", Toast.LENGTH_SHORT).show();
                        handler.post(checkEmailVerification);
                    } else {
                        Toast.makeText(Signup_Activity.this, "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                        btn_Signup_Step0.setEnabled(true);
                    }
                }
            });
        }
    }

    private void checkEmailVerificationStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (user.isEmailVerified()) {
                        String name = edtName.getText().toString();
                        String email = edtMail.getText().toString();
                        String password = edtConfirmpassword.getText().toString();
                        String profile = "https://www.iconfinder.com/icons/2147887/avatar_photo_profile_user_icon";
                        sDatabase = FirebaseDatabase.getInstance();
                        sReference = sDatabase.getReference("users").child("Email");
                        UserHelper userClass = new UserHelper(name, email, password, profile);
                        sReference.child(user.getUid()).setValue(userClass);
                        Intent intent = new Intent(Signup_Activity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(Signup_Activity.this, "Email đã được xác thực, đăng nhập ngay!", Toast.LENGTH_SHORT).show();
                        finish();
                        handler.removeCallbacks(checkEmailVerification);
                    }
                }
            });
        }
    }
}
