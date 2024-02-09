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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Obtener referencia al documento controlador
        DocumentReference controladorRef = FirebaseFirestore.getInstance().collection("users").document("controlador");

        // Incrementar el contador y obtener el nuevo valor
        controladorRef.update("registros", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    controladorRef.get().addOnSuccessListener(documentSnapshot -> {
                        long nuevoContador = documentSnapshot.getLong("registros");
                        if (nuevoContador <= 2) {
                            // Continuar con el registro del usuario
                            performUserRegistration(email, password);
                        } else {
                            // Deshacer el incremento del contador
                            controladorRef.update("registros", FieldValue.increment(-1));
                            // No permitir más registros
                            Toast.makeText(RegisterActivity.this, "No se pueden realizar más registros", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Error al incrementar el contador
                    Toast.makeText(RegisterActivity.this, "Error al incrementar el contador", Toast.LENGTH_SHORT).show();
                });
    }

    private void performUserRegistration(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        // Redirigir a la actividad deseada después del registro
                        startActivity(new Intent(RegisterActivity.this, EditActivity.class));
                        finish();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error al registrar. " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("Registro Fallido", errorMessage);

                        // Deshacer el incremento del contador si el registro falla
                        DocumentReference controladorRef = FirebaseFirestore.getInstance().collection("users").document("controlador");
                        controladorRef.update("registros", FieldValue.increment(-1));
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
