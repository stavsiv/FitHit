package com.example.fithit.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.fithit.R;
import com.example.fithit.Activities.MainActivity;
import java.util.Objects;

public class FragmentLogin extends Fragment {
    private View rootView;

    public FragmentLogin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailEditText = rootView.findViewById(R.id.emailEditText);
        EditText passwordEditText = rootView.findViewById(R.id.passwordEditText);
        Button loginButton = rootView.findViewById(R.id.buttonLoginMain);
        Button registerButton = rootView.findViewById(R.id.buttonLoginToRegister);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {Toast.makeText(requireContext(), R.string.email_and_password_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login(email, password, rootView)
                        .addOnSuccessListener(authResult -> navigateToMainFragment())
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(),
                                    R.string.login_failed + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(requireContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            if (rootView != null) {
                Navigation.findNavController(rootView).navigate(R.id.action_fragmentLogin_to_fragmentRegister);
            } else {
            }
        });

        return rootView;
    }

    private void navigateToMainFragment() {
        try {
            NavController navController = Navigation.findNavController(requireView());
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.fragmentMain) {
                navController.navigate(R.id.action_fragmentLogin_to_fragmentMain);
            }
        } catch (Exception e) {
            requireActivity().recreate();
        }
    }
}
