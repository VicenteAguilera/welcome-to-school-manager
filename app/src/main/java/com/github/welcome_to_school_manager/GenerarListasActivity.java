package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.welcome_to_school_manager.Interfaces.Registros;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.github.welcome_to_school_manager.helpers.utility.PdfHelper;
import com.github.welcome_to_school_manager.repository.FirestoreHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GenerarListasActivity extends AppCompatActivity implements View.OnClickListener, Registros {

    private CardView button_isc;
    private CardView button_admon;
    private CardView button_civil;
    private CardView button_mecanica;
    private CardView button_mecatronica;
    private CardView button_alimentarias;
    private CardView button_industrial;
    private CardView button_electronica;
    private FirestoreHelper firestoreHelper;
    private List<Alumno> listaAlumnos;
    private PdfHelper pdfHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_listas);
        setTitle("Generar PDF");

        button_isc = findViewById(R.id.button_isc);
        button_admon = findViewById(R.id.button_admon);
        button_civil = findViewById(R.id.button_civil);
        button_mecanica = findViewById(R.id.button_mecanica);
        button_mecatronica = findViewById(R.id.button_mecatronica);
        button_alimentarias = findViewById(R.id.button_alimentarias);
        button_industrial = findViewById(R.id.button_industrial);
        button_electronica = findViewById(R.id.button_electronica);

        firestoreHelper = new FirestoreHelper();

        button_isc.setOnClickListener(this);
        button_admon.setOnClickListener(this);
        button_civil.setOnClickListener(this);
        button_mecanica.setOnClickListener(this);
        button_mecatronica.setOnClickListener(this);
        button_alimentarias.setOnClickListener(this);
        button_industrial.setOnClickListener(this);
        button_electronica.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog dialog = ProgressDialog.show(GenerarListasActivity.this, "", "Cargando datos para pdfs...", true);
        firestoreHelper.getAllRegisters(this, dialog);
    }

    @Override
    public void onClick(View view) {
        int idView = view.getId();

        pdfHelper = new PdfHelper(this, listaAlumnos);

        switch (idView) {
            case R.id.button_isc:
                pdfHelper.crearPDF("ING. EN SISTEMAS COMPUTACIONALES");
                break;

            case R.id.button_admon:
                pdfHelper.crearPDF("ING. EN ADMINISTRACION");
                break;

            case R.id.button_civil:
                pdfHelper.crearPDF("ING. CIVIL");
                break;

            case R.id.button_mecanica:
                pdfHelper.crearPDF("ING. MECANICA");
                break;

            case R.id.button_mecatronica:
                pdfHelper.crearPDF("ING. MECATRONICA");
                break;

            case R.id.button_alimentarias:
                pdfHelper.crearPDF("ING. EN INDUSTRIAS ALIMENTARIAS");
                break;

            case R.id.button_industrial:
                pdfHelper.crearPDF("ING. INDUSTRIAL");
                break;

            case R.id.button_electronica:
                pdfHelper.crearPDF("ING. ELECTRONICA");
                break;
        }
    }

    @Override
    public void getRegistros(List<Alumno> listaAlumnos) {
        if(listaAlumnos.size() != 0)
        {
            Collections.sort(listaAlumnos, new Comparator<Alumno>() {
                @Override
                public int compare(Alumno alumno1, Alumno alumno2) {
                    return alumno1.getNombre().compareTo(alumno2.getNombre());
                }
            });
            this.listaAlumnos = listaAlumnos;
        }
    }
}