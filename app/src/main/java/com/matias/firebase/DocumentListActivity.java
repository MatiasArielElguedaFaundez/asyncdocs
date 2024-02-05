package com.matias.firebase;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DocumentListActivity extends AppCompatActivity {
    private ListView documentListView;
    private List<Document> documentList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        documentList = new ArrayList<>();
        documentListView = findViewById(R.id.documentListView);

        // Configurando el adaptador personalizado
        DocumentAdapter adapter = new DocumentAdapter(this, documentList);
        documentListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        documentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Document selectedDocument = documentList.get(position);
                openFragmentEditText(selectedDocument.getDocumentId());
            }
        });

        findViewById(R.id.btnAddDocument).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentEditText(null);
            }
        });

        // Obtener documentos de Firebase
        fetchDocumentsFromFirebase();
    }

    private void fetchDocumentsFromFirebase() {
        showLoadingScreen("Cargando documentos...");

        db.collection("documents")
                .get()
                .addOnCompleteListener(task -> {
                    hideLoadingScreen();

                    if (task.isSuccessful()) {
                        documentList.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Document document = documentSnapshot.toObject(Document.class);
                            if (document != null) {
                                documentList.add(document);
                                Log.d("DocumentListActivity", "Document ID: " + documentSnapshot.getId());
                                Log.d("DocumentListActivity", "Document Title: " + document.getTitle());
                                Log.d("DocumentListActivity", "Document Body: " + document.getBody());
                            } else {
                                Log.e("DocumentListActivity", "Error deserializando documento: " + documentSnapshot.getId());
                            }
                        }
                        ((DocumentAdapter) documentListView.getAdapter()).notifyDataSetChanged();
                    } else {
                        Log.e("DocumentListActivity", "Error obteniendo documentos", task.getException());
                        handleFetchDocumentsError(task.getException());
                    }
                });
    }

    private void handleFetchDocumentsError(Exception exception) {
        if (exception != null) {
            String errorMessage = exception.getMessage();
            Toast.makeText(this, "Error al cargar documentos: " + errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void openFragmentEditText(String documentId) {
        FragmentEditText fragmentEditText = new FragmentEditText();
        Bundle args = new Bundle();
        args.putString("documentId", documentId);
        fragmentEditText.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentEditText);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hideLoadingScreen() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showLoadingScreen(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
