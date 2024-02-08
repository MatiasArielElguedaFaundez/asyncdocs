package com.matias.firebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class FragmentEditText extends Fragment {
    private EditText etTitle, etBody, etDocumentId, documentIdEditText;
    private Button btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration editingListener;
    private String documentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_text, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        documentId = "1";
        loadDocumentInfo();

        btnUpdate.setOnClickListener(v -> updateDocument());

        return view;
    }

    private void loadDocumentInfo() {
        DocumentReference documentRef = db.collection("documents").document(documentId);
        editingListener = documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                handleError(e.getMessage());
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String currentTitle = documentSnapshot.getString("title");
                String currentBody = documentSnapshot.getString("body");
                etTitle.setText(currentTitle);
                etBody.setText(currentBody);
            } else {
                handleError("El documento no existe.");
            }
        });
    }

    private void updateDocument() {
        documentId = documentIdEditText.getText().toString().trim();

        if (documentId == null) {
            handleError("ID del documento es nulo.");
            return;
        }

        String newTitle = etTitle.getText().toString().trim();
        String newBody = etBody.getText().toString().trim();

        if (newTitle.isEmpty() || newBody.isEmpty()) {
            handleError("Por favor, complete todos los campos.");
            return;
        }

        DocumentReference documentRef = db.collection("documents").document(documentId);
        documentRef.update("title", newTitle, "body", newBody)
                .addOnSuccessListener(aVoid -> {
                    showUpdateDialog();
                })
                .addOnFailureListener(e -> handleError("Error al actualizar el documento."));
    }

    private void showUpdateDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Documento actualizado")
                .setMessage("El documento ha sido actualizado por otro usuario.")
                .setPositiveButton("Reiniciar", (dialog, which) -> {
                    // Vuelve a cargar el documento con los datos actualizados
                    loadDocumentInfo();
                })
                .show();
    }

    private void handleError(String errorMessage) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (editingListener != null) {
            editingListener.remove();
        }
    }
}
