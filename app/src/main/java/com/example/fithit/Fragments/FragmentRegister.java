package com.example.fithit.Fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fithit.Activities.MainActivity;
import com.example.fithit.R;


public class FragmentRegister extends Fragment {
    private EditText editTextUsername;
    private EditText editTextPhone;
    private EditText editTextAge;
    private EditText editTextWeight;
    private CheckBox checkBoxReminders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        EditText editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        checkBoxReminders = view.findViewById(R.id.checkBoxReminders);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password1 = editTextPassword.getText().toString().trim();
            //String password2 = password2EditText.getText().toString().trim();
            String phoneNumber = editTextPhone.getText().toString().trim();

            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                //mainActivity.register(email, password1, password2, phoneNumber, view);
                mainActivity.register(email, password1, phoneNumber, view);
            } else {
                Toast.makeText(getContext(), "Error: Activity not found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    private boolean validateInput() {
        String username = editTextUsername.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();

        // Check username
        if (username.isEmpty()) {
            editTextUsername.setError("Please enter username");
            return false;
        }

        // Check phone number
        if (phone.length() != 10 || !phone.matches("\\d+")) {
            editTextPhone.setError("Please enter valid phone number (10 digits)");
            return false;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age < 16 || age > 70) {
                editTextAge.setError("Age must be between 16 and 70");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextAge.setError("Please enter valid age");
            return false;
        }

        if (weight.isEmpty()) {
            editTextWeight.setError("Please enter weight");
            return false;
        }
        return true;
    }

    private void register() {
        String username = editTextUsername.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        int age = Integer.parseInt(editTextAge.getText().toString().trim());
        double weight = Double.parseDouble(editTextWeight.getText().toString().trim());
        boolean wantReminders = checkBoxReminders.isChecked();
        // Add your registration logic here
    }

}
