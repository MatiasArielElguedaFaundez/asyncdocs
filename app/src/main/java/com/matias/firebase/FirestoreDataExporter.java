package com.matias.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matias.firebase.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FirestoreDataExporter {
    private static final String FILENAME = "firestore_data.json";
    private static final String TAG = "FirestoreDataExporter";

    private FirebaseFirestore db;

    public FirestoreDataExporter() {
        db = FirebaseFirestore.getInstance();
    }

    public void exportFirestoreData(Context context) {
        CollectionReference documentsRef = db.collection("documents");

        documentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Document> documentList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    Document document = documentSnapshot.toObject(Document.class);
                    if (document != null) {
                        documentList.add(document);
                    }
                }

                if (!documentList.isEmpty()) {
                    saveDataToFile(context, documentList);
                }
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void saveDataToFile(Context context, List<Document> documentList) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonData = gson.toJson(documentList);

            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(jsonData);
            outputStreamWriter.close();

            Log.d(TAG, "Firestore data exported to file: " + FILENAME);
        } catch (IOException e) {
            Log.e(TAG, "Error saving data to file: " + e.getMessage());
        }
    }
}
