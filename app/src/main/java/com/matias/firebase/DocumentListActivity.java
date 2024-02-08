package com.matias.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DocumentListActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        db = FirebaseFirestore.getInstance();

        // Obtener el ID del documento que deseas abrir (puedes pasarlo desde otra parte de tu aplicación o usar un valor fijo)
        String documentId = "1";

        // Abrir la actividad de edición directamente con el ID del documento
        openEditActivity(documentId);
    }

    private void openEditActivity(String documentId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener la referencia al documento específico
        DocumentReference documentRef = db.collection("documents").document(documentId);

        documentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // Documento encontrado, abrir la actividad de edición
                    Intent intent = new Intent(DocumentListActivity.this, EditActivity.class);
                    intent.putExtra("documentId", documentId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("title", documentSnapshot.getString("title"));
                    intent.putExtra("body", documentSnapshot.getString("body"));
                    startActivity(intent);
                    startActivity(intent);
                } else {
                    // Documento no encontrado, mostrar un mensaje o tomar alguna acción
                    Toast.makeText(this, "El documento no existe", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Error al obtener el documento, mostrar un mensaje o tomar alguna acción
                Toast.makeText(this, "Error al cargar el documento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}