package com.matias.firebase;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

public class FragmentEditText extends Fragment {
    private EditText etTitle, etBody;
    private Button btnUpdate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // ID del documento que se está editando
    private String documentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_text, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        // Obtener el ID del documento de los argumentos (puedes pasarlo desde la actividad principal)
        if (getArguments() != null && getArguments().containsKey("documentId")) {
            documentId = getArguments().getString("documentId");
        }

        // Cargar la información actual del documento y mostrarla en los EditText
        loadDocumentInfo();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDocument();
            }
        });

        return view;
    }

    private void loadDocumentInfo() {
        DocumentReference documentRef = db.collection("documents").document(documentId);
        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores actuales del documento y mostrarlos en los EditText
                    String currentTitle = documentSnapshot.getString("title");
                    String currentBody = documentSnapshot.getString("body");

                    etTitle.setText(currentTitle);
                    etBody.setText(currentBody);
                }
            }
        });
    }

    private void updateDocument() {
        // Obtener los nuevos valores del título y el cuerpo
        String newTitle = etTitle.getText().toString().trim();
        String newBody = etBody.getText().toString().trim();

        // Actualizar el documento en Firestore
        DocumentReference documentRef = db.collection("documents").document(documentId);
        documentRef
                .update("title", newTitle, "body", newBody)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al actualizar el documento", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
