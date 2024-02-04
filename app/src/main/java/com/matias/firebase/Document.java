package com.matias.firebase;

import android.icu.text.CaseMap;
import android.widget.Button;

public class Document {
    private String title;
    private String body;
    private Button saveDocument;
    private boolean isBeingEditing;

    public Document() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Button getSaveDocument() {
        return saveDocument;
    }

    public void setSaveDocument(Button saveDocument) {
        this.saveDocument = saveDocument;
    }

    public boolean isBeingEditing() {
        return isBeingEditing;
    }

    public void setEditing(boolean editing) {
        isBeingEditing = editing;
    }
}
