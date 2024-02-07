package com.matias.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private DatabaseReference registrationStatusRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        registrationStatusRef = FirebaseDatabase.getInstance().getReference("registrationStatus");
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            // Establecer ambos registros en false al principio
                            registrationStatusRef.child(userId).child("registro1").setValue(false);
                            registrationStatusRef.child(userId).child("registro2").setValue(false);

                            // Actualizar a true según tus condiciones específicas
                            // Ejemplo: actualizar a true si el usuario se registró correctamente
                            registrationStatusRef.child(userId).child("registro1").setValue(true);

                            // Verificar si ambos registros son true y actualizar el segundo registro
                            registrationStatusRef.child(userId).child("registro1").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean registro1 = snapshot.getValue(Boolean.class);
                                    if (registro1) {
                                        // Ambos registros son true, actualizar registro2
                                        registrationStatusRef.child(userId).child("registro2").setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(RegisterActivity.this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            // Redirigir a la actividad deseada después del registro
                            startActivity(new Intent(RegisterActivity.this, DocumentListActivity.class));
                            finish();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error al registrar. " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("Registro Fallido", errorMessage);
                        }
                    }
                });
    }

}
