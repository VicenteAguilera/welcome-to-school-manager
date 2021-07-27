package com.github.welcome_to_school_manager.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.Interfaces.Password;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class FirebasePasswords {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final CollectionReference PassCollection = db.collection("passwords");

    public void getPassword(String document, final ProgressDialog dialog, Messages messages, Password password) {
        dialog.show();
        PassCollection.document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = document.getData();
                    if (Objects.requireNonNull(document).exists()) {
                        String pass = String.valueOf(data.get("key"));
                        Log.e("pass: ", pass);
                        password.getPasswordFirebase(pass);
                        dialog.dismiss();
                    } else {
                        messages.getMessage("Error, intenta de nuevo.");
                        dialog.dismiss();
                    }
                }
            }
        });
    }

}
