package com.matias.firebase;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.ValueEventListener; // Importante: Agrega esta línea
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import androidx.annotation.NonNull;

public class FragmentEditText extends Fragment {
    private EditText etTitle, etBody, etDocumentId, documentIdEditText;
    private Button btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String editingUserId;
    private String documentId;
    private String editorFcmToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_text, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        etDocumentId = view.findViewById(R.id.etDocumentId);
        documentIdEditText = view.findViewById(R.id.editTextDocumentId);

        // Solo cargar el documento si el ID es "1"
        documentId = "1";
        documentIdEditText.setText(documentId);
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
                    String currentTitle = documentSnapshot.getString("title");
                    String currentBody = documentSnapshot.getString("body");
                    editingUserId = documentSnapshot.getString("editingUserId");
                    editorFcmToken = documentSnapshot.getString("fcmToken");
                    etTitle.setText(currentTitle);
                    etBody.setText(currentBody);

                    if (editingUserId != null && mAuth.getCurrentUser() != null && !editingUserId.equals(mAuth.getCurrentUser().getUid())) {
                        showEditingMessage();
                        sendEditingNotification(editorFcmToken);
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("El documento no existe.")
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Error al cargar el documento.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }


    private void updateDocument() {
        documentId = documentIdEditText.getText().toString().trim();

        if (documentId == null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("ID del documento es nulo.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        String newTitle = etTitle.getText().toString().trim();
        String newBody = etBody.getText().toString().trim();

        if (newTitle.isEmpty() || newBody.isEmpty()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("Por favor, complete todos los campos.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        DocumentReference documentRef = db.collection("documents").document(documentId);
        documentRef
                .update("title", newTitle, "body", newBody)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Éxito")
                                .setMessage("Documento actualizado correctamente.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Error")
                                .setMessage("Error al actualizar el documento.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    }
                });
    }

    private void showEditingMessage() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Documento en edición")
                .setMessage("Este documento está siendo editado por otro usuario.")
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void sendEditingNotification(String editorToken) {
        String currentUserToken = FirebaseMessaging.getInstance().getToken().getResult();
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(editorToken)
                .setMessageId(Integer.toString(0))
                .addData("title", "Documento en edición")
                .addData("body", "El documento que estás editando está siendo modificado por otro usuario.")
                .build());
        new AlertDialog.Builder(getActivity())
                .setTitle("Éxito")
                .setMessage("Notificación enviada al usuario que está editando.")
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
