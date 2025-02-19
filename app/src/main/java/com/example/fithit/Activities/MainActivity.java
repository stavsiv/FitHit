package com.example.fithit.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.R;
import com.example.fithit.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public Task<AuthResult> login(String email, String password, View view) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentMain);
                        Log.d("Login", "Navigating to Main Fragment");
                    } else {
                        Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void register(String email, String password1, String password2, String username,
                         String phone, int age, double weight, boolean wantReminders, View view) {
        if (!validateInputs(email, password1, password2, username, phone, age, weight)) {
            return;
        }

        // First create the authentication user
        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {  // בדיקת null
                        // Get the new user's ID
                        String userId = mAuth.getCurrentUser().getUid();

                        // Now create the user profile in the database
                        FirebaseManager.getInstance()
                                .createNewUser(userId, email, username, phone, age, weight, wantReminders)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view)
                                                .navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                                    } else {
                                        mAuth.getCurrentUser().delete()
                                                .addOnCompleteListener(deleteTask -> {
                                                    String errorMessage = "Failed to create user profile";
                                                    if (profileTask.getException() != null) {
                                                        errorMessage += ": " + profileTask.getException().getMessage();
                                                    }
                                                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                });
                    } else {
                        String errorMessage = "Registration failed";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String email, String password1, String password2,
                                   String username, String phone, int age, double weight) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPassword(password1)) {
            Toast.makeText(this, "Password must have at least 8 characters, one uppercase letter, and one special character", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password2.isEmpty()) {
            Toast.makeText(this, "Confirm password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password1.equals(password2)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.isEmpty() || !phone.matches("^05\\d{8}$")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (age < 16 || age > 70) {
            Toast.makeText(this, "Age must be between 16 and 70", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight <= 0 || weight > 200) {
            Toast.makeText(this, "Invalid weight", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        if (!password.matches(".*[A-Z].*")) return false;

        return password.matches(".*[^a-zA-Z0-9].*");
    }

    public void addDataToDatabase(String email, String username, String phone,
                                  int age, double weight, boolean wantReminders) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        User user = new User();//(email, username, phone, age, weight, wantReminders);
        usersRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
