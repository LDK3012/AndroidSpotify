package project.lon.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import project.lon.R;

public class EditProfile extends AppCompatActivity {
    Button btnCancel, btnSave;
    ImageButton imgEditAvatar;
    EditText edtUsername;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    DatabaseReference userRef, userRefs;
    FirebaseUser firebaseUser;
    String urlDefault = "https://cdn0.iconfinder.com/data/icons/seo-web-4-1/128/Vigor_User-Avatar-Profile-Photo-01-512.png";

    boolean isUsernameChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent intent = getIntent();
        //
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users").child("Google").child(firebaseUser.getUid());
        userRefs = firebaseDatabase.getReference().child("users").child("Email").child(firebaseUser.getUid());
         addControls();
         addEvents();

        // Lấy tên người dùng từ SharedPreferences và gán vào edtUsername
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "");
        //
        edtUsername.setText(userName);
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isUsernameChanged = !s.toString().equals(userName);
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì ở đây
            }
        });
        //
        setAvatar();

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
                if (isUsernameChanged){
                    showConfirmationDialog();
                }
                else
                    finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save changes logic here
                String newUsername = edtUsername.getText().toString();
                // Update the new username to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", newUsername);
                editor.apply();

                // Determine the provider and update the correct branch
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

                // Update the new username to the correct Firebase Realtime Database branch
                DatabaseReference correctUserRef = isGoogle ? userRef : userRefs;
                if (firebaseUser != null) {
                    correctUserRef.child("name").setValue(newUsername).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the new username in Firebase Authentication
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUsername)
                                    .build();

                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(EditProfile.this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfile.this, "Cập nhật tên thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditProfile.this, "Cập nhật tên thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                finish();
            }
        });
        updateSaveButtonState();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Bạn có dữ liệu đang thay dổi.Có chắc chắn thoát?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void updateSaveButtonState() {
        btnSave.setEnabled(isUsernameChanged);
        btnSave.setAlpha(isUsernameChanged ? 1.0f : 0.5f);
    }

    private void setAvatar() {
        firebaseUser = mAuth.getCurrentUser();
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
            Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(imgEditAvatar);
        } else {
            Picasso.get().load(urlDefault.toString()).into(imgEditAvatar);

        }
    }

    @Override
    public void onBackPressed() {
        if (isUsernameChanged) {
            showConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }
}