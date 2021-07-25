package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.github.welcome_to_school_manager.repository.FirbaseBatches;
import com.google.android.material.snackbar.Snackbar;

public class ManagementActivity extends AppCompatActivity{

    private CardView button_new_generation,button_new,button_update,button_pdf;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        button_new = findViewById(R.id.button_new);
        button_new_generation = findViewById(R.id.button_new_generation);
        button_pdf = findViewById(R.id.button_pdf);
        button_update = findViewById(R.id.button_update);
        options();
    }
    private void options()
    {
        final Intent intent;
        button_new_generation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManagementActivity.this, getResources().getText(R.string.nueva_generacion) + "...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManagementActivity.this, NewGenetationActivity.class));
            }
        });
        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManagementActivity.this, getResources().getText(R.string.agregar) + "...", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(ManagementActivity.this, AddAndUpdateActivity.class);
                intent2.putExtra("OPERATION", "ADD");
                startActivity(intent2);
            }
        });
        button_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManagementActivity.this, getResources().getText(R.string.actualizar_registros) + "...", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(ManagementActivity.this, AddAndUpdateActivity.class);
                intent2.putExtra("OPERATION", "UPDATE");
                startActivity(intent2);
            }
        });
        button_pdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(ManagementActivity.this, getResources().getText(R.string.generar_listas) + "...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManagementActivity.this, GenerarListasActivity.class));
            }
        });
    }


}