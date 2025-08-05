package com.example.fithit.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.fithit.R;
import com.example.fithit.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login(String email, String password, View view) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentMain);
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void register(String email, String password1, String password2, String username,
                         String phone, int age, boolean wantReminders, View view) {
        if (!validateInputs(email, password1, password2, username, phone, age)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();

                        User newUser = new User(username, phone, age, wantReminders);
                        newUser.setUserId(userId);

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .setValue(newUser)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        Toast.makeText(this,getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view)
                                                .navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                                    } else {
                                        mAuth.getCurrentUser().delete();
                                        String errorMessage = getString(R.string.failed_to_create_user_profile);
                                        if (profileTask.getException() != null) {
                                            errorMessage += ": " + profileTask.getException().getMessage();
                                        }
                                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        String errorMessage = getString(R.string.registration_failed);
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password1, String password2,
                                   String username, String phone, int age) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPassword(password1)) {
            Toast.makeText(this, getString(R.string.password_must_have_at_least_8_characters_one_uppercase_letter_and_one_special_character), Toast.LENGTH_LONG).show();
            return false;
        }

        if (password2.isEmpty()) {
            Toast.makeText(this, getString(R.string.confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password1.equals(password2)) {
            Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.username_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.isEmpty() || !phone.matches("^05\\d{8}$")) {
            Toast.makeText(this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (age < 16 || age > 70) {
            Toast.makeText(this, getString(R.string.age_must), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        if (!password.matches(".*[A-Z].*")) return false;

        return password.matches(".*[^a-zA-Z0-9].*");
    }

}
