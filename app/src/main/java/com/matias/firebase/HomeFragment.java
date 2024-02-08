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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

public class FragmentEditText extends Fragment {
    private EditText etTitle, etBody;
    private Button btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String editingUserId;
    private String documentId;
    private String editorFcmToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_document, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        if (getArguments() != null && getArguments().containsKey("documentId")) {
            documentId = getArguments().getString("documentId");
        }
        if (documentId != null) {
            loadDocumentInfo();
        } else {
            Toast.makeText(getActivity(), "ID del documento es nulo", Toast.LENGTH_SHORT).show();
        }

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
                    Toast.makeText(getActivity(), "El documento no existe", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al cargar el documento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDocument() {
        String newTitle = etTitle.getText().toString().trim();
        String newBody = etBody.getText().toString().trim();

        if (newTitle.isEmpty() || newBody.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
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

    private void showEditingMessage() {
        Toast.makeText(getActivity(), "Este documento está siendo editado por otro usuario", Toast.LENGTH_SHORT).show();
    }

    private void sendEditingNotification(String editorToken) {
        String currentUserToken = "token_del_usuario_actual";

        // hay que mejorar esto
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(editorToken)
                .setMessageId(Integer.toString(0))
                .addData("title", "Documento en edición")
                .addData("body", "El documento que estás editando está siendo modificado por otro usuario.")
                .build());
        Toast.makeText(getActivity(), "Notificación enviada al usuario que está editando.", Toast.LENGTH_SHORT).show();
    }
}