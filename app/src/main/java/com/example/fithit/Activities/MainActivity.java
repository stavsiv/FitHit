//package com.example.fithit.Activities;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.navigation.Navigation;
//
//import com.example.fithit.R;
//
//import com.example.fithit.Models.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class MainActivity extends AppCompatActivity {
//
//
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        mAuth = FirebaseAuth.getInstance();
//    }
//    public FirebaseAuth getFirebaseAuth() {
//        return mAuth;
//    }
//
//    public void login(String email, String password, View view) {
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//                //Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentMain);
//            } else {
//                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }        });
//    }
//
//    public void register(String email, String password1, String password2, View view) {
//        if (!validateInputs(email, password1, phoneNumber)) {
//
//
//            return;
//        }
//
//        mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, task -> {
//            if (task.isSuccessful()) {
//                addDataToDatabase(email, phoneNumber);
//                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(view).navigate(R.id.action_fragmentRegister_to_fragmentLogin);
//            } else {
//                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private boolean validateInputs(String email, String password1, String password2, String phoneNumber) {
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (password1.isEmpty()) {
//            Toast.makeText(this, "Password 1 cannot be empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!isValidPassword(password1)) {
//            Toast.makeText(this, "Password 1 must have at least 8 characters, one uppercase letter, and one special character", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (password2.isEmpty()) {
//            Toast.makeText(this, "Password 2 cannot be empty", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!password1.equals(password2)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (phoneNumber.isEmpty() || !phoneNumber.matches("^05\\d{8}$")) {
//            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean isValidPassword(String password) {
//        if (password.length() < 8) return false;
//
//        if (!password.matches(".[A-Z].")) return false;
//
//        return password.matches(".[^a-zA-Z0-9].");
//    }
//    public void addDataToDatabase(String email, String phone) {
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//
//        User user = new User(email, phone); // check again
//        usersRef.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        EdgeToEdge.enable(this);
////        setContentView(R.layout.activity_main);
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
////            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
////            return insets;
////        });
////    }
//
