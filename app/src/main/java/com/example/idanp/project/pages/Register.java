package com.example.idanp.project.pages;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.idanp.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    EditText etEmail, etPassword, etConfirmPassword;
    Button register;

    String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Initialization
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterPasswordConfirm);
        register = findViewById(R.id.btRegisterRegister);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit text to strings
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                if (verifyPassword() && email.contains("@")) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser l_user = mAuth.getCurrentUser();
                                        Map<String, Object> l_data = new HashMap<>();
                                        l_data.put("email", l_user.getEmail());
                                        db.collection("users").document(l_user.getUid());
                                        db.collection("users").document(l_user.getUid()).set(l_data);

                                        startActivity(new Intent(Register.this, Login.class));
                                    } else {
                                        // sign up fails
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Email is already in use",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });


    }

    /**
     * Checks if both the password and the confirm password match, as well as if the password is at least 4 notes long.
     * @return {@code true} if both passwords match and the password is at least 4 notes longs, {@code false} otherwise
     */
    private boolean verifyPassword() {
        if (!password.equals("") && !confirmPassword.equals("")) {
            if (password.equals(confirmPassword)) {
                if (password.length() > 4) {
                    return true;
                } else {
                    Toast.makeText(this, "Password must be atleast 4 notes long", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
