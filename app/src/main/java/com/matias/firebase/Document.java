package com.matias.firebase;

import com.google.firebase.firestore.PropertyName;

public class Document {
    private String title;
    private String body;
    private String documentId;

    public Document(){

    }

    public Document(String title, String body, String documentId) {
        this.title = title;
        this.body = body;
        this.documentId = documentId;
    }
    @PropertyName("title")
    public String getTitle() {
        return title;
    }
    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }
    @PropertyName("body")
    public String getBody() {
        return body;
    }
    @PropertyName("body")
    public void setBody(String body) {
        this.body = body;
    }
    @PropertyName("documentId")
    public String getDocumentId() {
        return documentId;
    }
    @PropertyName("documentId")
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
