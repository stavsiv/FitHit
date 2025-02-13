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
    private EditText editTextEmail;
    private EditText editTextPassword1;
    private EditText editTextPassword2;
    private EditText editTextUsername;
    private EditText editTextPhone;
    private EditText editTextAge;
    private EditText editTextWeight;
    private CheckBox checkBoxReminders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword1 = view.findViewById(R.id.editTextPassword1);
        editTextPassword2 = view.findViewById(R.id.editTextPassword2);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        checkBoxReminders = view.findViewById(R.id.checkBoxReminders);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password1 = editTextPassword1.getText().toString().trim();
            String password2 = editTextPassword2.getText().toString().trim();
            String username = editTextUsername.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            String ageStr = editTextAge.getText().toString().trim();
            String weightStr = editTextWeight.getText().toString().trim();

            if (ageStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int age = Integer.parseInt(editTextAge.getText().toString().trim());
                double weight = Double.parseDouble(editTextWeight.getText().toString().trim());
                boolean wantReminders = checkBoxReminders.isChecked();

                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.register(email, password1, password2, username, phone,
                            age, weight, wantReminders, view);
                } else {
                    Toast.makeText(getContext(), "Error: Activity not found", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for age and weight", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }
}