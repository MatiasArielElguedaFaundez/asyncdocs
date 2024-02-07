package com.matias.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private Button goToRegisterButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        goToRegisterButton = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                            // Acceder a los valores de registro1 y registro2
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean registro1 = snapshot.child("registro1").getValue(Boolean.class);
                                    boolean registro2 = snapshot.child("registro2").getValue(Boolean.class);

                                    // Verificar si ambos registros están en true
                                    if (registro1 && registro2) {
                                        // Ambos registros están en true, no permitir el inicio de sesión
                                        Toast.makeText(LoginActivity.this, "No puedes iniciar sesión. Ambos registros completados.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut(); // Cerrar sesión
                                    } else {
                                        // Al menos uno de los registros no está en true, permitir el inicio de sesión
                                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, DocumentListActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
