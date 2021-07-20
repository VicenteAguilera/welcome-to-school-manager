package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.github.welcome_to_school_manager.helpers.utility.StringHelper;
import com.github.welcome_to_school_manager.repository.FirbaseBatches;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NewGenetationActivity extends AppCompatActivity implements Invitado, Messages {

    private ArrayList<Alumno> alumnos= new ArrayList<>();
    private Button buttonclear,buttonsetup,buttonadd,buttonclearinfo,buttonadd_firebase;
    private FirbaseBatches firbaseBatches = new FirbaseBatches();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_genetation);
        buttonadd_firebase = findViewById(R.id.buttonadd_firebase);
        buttonadd = findViewById(R.id.buttonadd);
        buttonclear = findViewById(R.id.buttonclear);
        buttonsetup = findViewById(R.id.buttonsetup);
        buttonclearinfo = findViewById(R.id.buttonclearinfo);
        options();
    }
    private void options()
    {
        buttonclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = ProgressDialog.show(NewGenetationActivity.this, "", "eliminando...", true);
                firbaseBatches.getAllCollection(NewGenetationActivity.this,dialog);
            }
        });
        buttonsetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = ProgressDialog.show(NewGenetationActivity.this, "", "Configurando...", true);
                if(alumnos.size()<=400) {
                    firbaseBatches.setupCollection(NewGenetationActivity.this,dialog,alumnos);
                }
                    else
                {
                    getMessage("Solo se pueden 400 registros por turno");
                }
            }
        });
        buttonadd_firebase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = ProgressDialog.show(NewGenetationActivity.this, "", "Agregando...", true);
                if(alumnos.size()<=400) {
                    firbaseBatches.sendAllInformation(NewGenetationActivity.this, dialog, alumnos);
                }
                else
                {
                    getMessage("Solo se pueden 400 registros por turno");
                }
            }
        });
        buttonadd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ingresoDatos();
            }
        });
        buttonclearinfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alumnos.clear();
                getMessage("Datos liberados");
            }
        });
    }

    private void ingresoDatos()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_datos, null);
        builder.setView(view);

        final AlertDialog dialogSearchInvitation =builder.create();
        dialogSearchInvitation.setCancelable(false);
        TextInputLayout editText_alumnos = view.findViewById(R.id.editText_alumnos);
        Button buttonSubir = view.findViewById(R.id.button_subir);


        dialogSearchInvitation.show();

        buttonSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ProgressDialog dialog = ProgressDialog.show(NewGenetationActivity.this, "Subiendo datos", "", true);
                dialog.show();

                String arre [] = editText_alumnos.getEditText().getText().toString().split("\n");
                Log.e("Datos",editText_alumnos.getEditText().getText().toString()+" "+arre.length);
                for (int i =0;i<arre.length;i++)
                {
                    String texto = arre[i].trim();
                    if(Pattern.matches(StringHelper.REGXTEL,texto.toUpperCase()) || Pattern.matches(StringHelper.REGXNOTEL,texto.toUpperCase()))
                    {
                        String valores[] = texto.split(",");
                        Alumno alumno = new Alumno(valores[0],valores[1],valores[2],valores[3]);
                        alumnos.add(alumno);

                    }
                    else {
                        getMessage("Error en el registro "+(i+1) +" "+arre[i]);
                        alumnos.clear();
                        break;
                    }
                }

                dialog.dismiss();
                Toast.makeText(NewGenetationActivity.this, arre.length+" datos agregados correctamente ", Toast.LENGTH_LONG).show();
                dialogSearchInvitation.dismiss();
            }
        });
        if(alumnos.size()>400)
        {

        }
        else
        {

        }


    }

    @Override
    public void getAlumno(Alumno alumno) {

    }

    @Override
    public void getMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}