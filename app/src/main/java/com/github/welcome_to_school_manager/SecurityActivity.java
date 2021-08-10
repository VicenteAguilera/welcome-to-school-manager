package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.Interfaces.Password;
import com.github.welcome_to_school_manager.helpers.utility.SharedPreferencesHelper;
import com.github.welcome_to_school_manager.repository.FirebasePasswords;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class SecurityActivity extends AppCompatActivity implements Password, Messages {

    private Button button_CheckSecurity;
    private TextInputLayout editText_PassSecurity;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private FirebasePasswords firebasePasswords;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        button_CheckSecurity = findViewById(R.id.button_Security);
        editText_PassSecurity = findViewById(R.id.editText_Security);

        sharedPreferencesHelper = new SharedPreferencesHelper(SecurityActivity.this);
        firebasePasswords = new FirebasePasswords();

        button_CheckSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferencesHelper.hasData()) {
            pass = sharedPreferencesHelper.getPreferences().get("pass").toString();
            if(!pass.isEmpty()){
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
            //ProgressDialog dialog = ProgressDialog.show(SecurityActivity.this, "", "Verificando contraseña...", true);
            //firebasePasswords.getPassword("super_master_key", dialog, SecurityActivity.this, SecurityActivity.this);
        }
    }

    private void checkPassword() {
        if (!editText_PassSecurity.getEditText().getText().toString().isEmpty()) {
            pass = editText_PassSecurity.getEditText().getText().toString();
            ProgressDialog dialog = ProgressDialog.show(SecurityActivity.this, "", "Verificando contraseña...", true);
            firebasePasswords.getPassword("super_master_key", dialog, SecurityActivity.this, SecurityActivity.this);
        } else {
            editText_PassSecurity.setError("Campo requerido");
        }
    }

    @Override
    public void getMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void getPasswordFirebase(String pass) {
        if (pass.equals(this.pass)) {
            Map<String, Object> savePass = new HashMap<>();
            savePass.put("pass", pass);
            sharedPreferencesHelper.addPreferences(savePass);
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Contraseña incorrecta, intenta de nuevo.", Snackbar.LENGTH_LONG).show();
        }
    }
}