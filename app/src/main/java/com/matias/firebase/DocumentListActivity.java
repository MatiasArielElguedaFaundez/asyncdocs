package com.matias.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class DocumentListActivity extends AppCompatActivity {
    private ListView documentListView;
    private ArrayList<String> documentTitles;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        documentTitles = new ArrayList<>();

        documentListView = findViewById(R.id.documentListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, documentTitles);
        documentListView.setAdapter(adapter);

        documentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDocumentTitle = documentTitles.get(position);
                openFragmentEditText(selectedDocumentTitle);
            }
        });

        findViewById(R.id.btnAddDocument).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentEditText(null);
            }
        });
    }
    private void openFragmentEditText(String documentTitle) {
        FragmentEditText fragmentEditText = new FragmentEditText();
        Bundle args = new Bundle();
        args.putString("documentTitle", documentTitle);
        fragmentEditText.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentEditText);
        transaction.addToBackStack(null);
        transaction.commit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingScreen();
            }
        }, 1000);
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
