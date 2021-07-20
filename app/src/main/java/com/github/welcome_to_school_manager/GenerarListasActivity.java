package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GenerarListasActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_listas);
        setTitle("Generar PDF");
    }
}