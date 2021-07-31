package com.github.welcome_to_school_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.github.welcome_to_school_manager.helpers.utility.StringHelper;
import com.github.welcome_to_school_manager.repository.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class AddAndUpdateActivity extends AppCompatActivity implements Invitado {
    private TextInputLayout textInputLayout_numeroControl,textInputLayout_nombreCompleto,textInputLayout_Carrera,textInputLayout_telefono;
    private ArrayAdapter<String> arrayAdapter_carreras;
    private MaterialButton button_add_update,button_cancelar,button_delete;
    private String ERROR_ESTANDAR = "Campo requerido.";
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private Alumno alumno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_update);

        Bundle parametros = this.getIntent().getExtras();

        textInputLayout_numeroControl = findViewById(R.id.textInputLayout_numeroControl);
        textInputLayout_nombreCompleto = findViewById(R.id.textInputLayout_nombreCompleto);
        textInputLayout_Carrera = findViewById(R.id.textInputLayout_Carrera);
        textInputLayout_telefono = findViewById(R.id.textInputLayout_telefono);
        button_add_update = findViewById(R.id.button_add_update);
        button_cancelar = findViewById(R.id.button_Check);
        button_delete = findViewById(R.id.button_delete);

        arrayAdapter_carreras  = new ArrayAdapter<>(AddAndUpdateActivity.this, R.layout.custom_spinner_item, StringHelper.CARRERAS);
        ((AutoCompleteTextView)textInputLayout_Carrera.getEditText()).setAdapter(arrayAdapter_carreras);

        textWachers(textInputLayout_numeroControl);
        textWachersPhone(textInputLayout_telefono);

        button_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(parametros != null){
            String aux = parametros.getString("OPERATION");
            if(aux.equals("ADD"))
               configurationADD();
            else
                configurationUPDATE();
        } else
            Toast.makeText(AddAndUpdateActivity.this, "Hubo un error al cargar la actividad", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void configurationUPDATE() {
        this.setTitle("Actualizar Alumno");
        textInputLayout_nombreCompleto.getEditText().setEnabled(false);
        textInputLayout_Carrera.getEditText().setEnabled(false);
        textInputLayout_telefono.getEditText().setEnabled(false);
        button_delete.setVisibility(View.GONE);
        button_add_update.setText("Actualizar");
        textInputLayout_Carrera.setHint("Carrera");

        textInputLayout_numeroControl.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numControl = textInputLayout_numeroControl.getEditText().getText().toString();
                if(isNumeric(numControl)) {
                    if (numControl.length() == 8 && !numControl.isEmpty()) {
                        ProgressDialog dialog = ProgressDialog.show(AddAndUpdateActivity.this, "", "Buscando...", true);
                        firestoreHelper.getDataAlumno(AddAndUpdateActivity.this, numControl, dialog, AddAndUpdateActivity.this);
                        ocultarTeclado();
                    } else {
                        textInputLayout_numeroControl.setError("Número de control inválido.");
                    }
                }else {
                    textInputLayout_numeroControl.setError("El campo solo puede recibir numeros");
                }
            }
        });

        button_add_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlumno();
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogConfirm = new AlertDialog.Builder(AddAndUpdateActivity.this);
                dialogConfirm.setTitle("Eliminar Alumno");
                dialogConfirm.setMessage("¿Esta seguro de borrar a "+alumno.getNombre()+"?");
                dialogConfirm.setCancelable(false);
                dialogConfirm.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog progressDialog = ProgressDialog.show(AddAndUpdateActivity.this, "", "Eliminando...", true);
                        firestoreHelper.validDataDeleteAlumno(progressDialog, AddAndUpdateActivity.this,textInputLayout_numeroControl.getEditText().getText().toString());
                        cleanText();
                        estandarUpdate();
                        button_delete.setVisibility(View.GONE);
                    }
                });
                dialogConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogConfirm.show();
            }
        });

    }

    private void updateAlumno(){
        textInputLayout_numeroControl.setError(null);
        textInputLayout_nombreCompleto.setError(null);
        textInputLayout_Carrera.setError(null);
        textInputLayout_telefono.setError(null);

        boolean bandera_num = false;
        boolean bandera_nombre = false;
        boolean bandera_carrera = false;
        boolean bandera_telefono = false;

        String num_control = textInputLayout_numeroControl.getEditText().getText().toString();
        String nombre = textInputLayout_nombreCompleto.getEditText().getText().toString();
        String carrera = textInputLayout_Carrera.getEditText().getText().toString();
        String telefono = textInputLayout_telefono.getEditText().getText().toString();

        if(num_control.length() == 8 && isNumeric(num_control) && !num_control.isEmpty()){
            bandera_num = true;
        }else {
            textInputLayout_numeroControl.setError("Número de control inválido.");
        }

        if(!nombre.isEmpty()){
            bandera_nombre = true;
        }else {
            textInputLayout_nombreCompleto.setError(ERROR_ESTANDAR);
        }

        if(!carrera.isEmpty()){
            bandera_carrera = true;
        }else {
            textInputLayout_Carrera.setError(ERROR_ESTANDAR);
        }

        if(!telefono.isEmpty()){
            if (telefono.length() == 10 && isNumeric(telefono)){
                bandera_telefono = true;
            }else {
                bandera_telefono = false;
                textInputLayout_telefono.setError(ERROR_ESTANDAR);
            }
        }else {
            bandera_telefono = true;
        }

        if(bandera_num && bandera_nombre && bandera_carrera && bandera_telefono ){
            ProgressDialog dialog = ProgressDialog.show(AddAndUpdateActivity.this, "", "Actualizando...", true);
            firestoreHelper.updateDataAlumno(dialog, AddAndUpdateActivity.this, num_control, nombre, carrera, telefono);
            estandarUpdate();
            cleanText();
        }else {
            Toast.makeText(AddAndUpdateActivity.this, "Debes llenar todos los campos requeridos.",Toast.LENGTH_SHORT).show();
        }
    }

    private void configurationADD(){
        this.setTitle("Agregar Alumno");
        textInputLayout_numeroControl.setEndIconVisible(false);
        button_delete.setVisibility(View.GONE);
        button_add_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlumno();
            }
        });

    }

    private void addAlumno(){
        textInputLayout_numeroControl.setError(null);
        textInputLayout_nombreCompleto.setError(null);
        textInputLayout_Carrera.setError(null);
        textInputLayout_telefono.setError(null);

        boolean bandera_num = false;
        boolean bandera_nombre = false;
        boolean bandera_carrera = false;
        boolean bandera_telefono = false;

        String num_control = textInputLayout_numeroControl.getEditText().getText().toString();
        String nombre = textInputLayout_nombreCompleto.getEditText().getText().toString().trim();
        String carrera = textInputLayout_Carrera.getEditText().getText().toString();
        String telefono = textInputLayout_telefono.getEditText().getText().toString();

        if(num_control.length() == 8 && isNumeric(num_control) && !num_control.isEmpty()){
            bandera_num = true;
        }else {
            textInputLayout_numeroControl.setError("Número de control inválido.");
        }

        if(!nombre.isEmpty()){
            bandera_nombre = true;
        }else {
            textInputLayout_nombreCompleto.setError(ERROR_ESTANDAR);
        }

        if(!carrera.isEmpty()){
            bandera_carrera = true;
        }else {
            textInputLayout_Carrera.setError(ERROR_ESTANDAR);
        }

        if(!telefono.isEmpty()){
            if (telefono.length() == 10 && isNumeric(telefono)){
                bandera_telefono = true;
            }else {
                bandera_telefono = false;
                textInputLayout_telefono.setError(ERROR_ESTANDAR);
            }
        }else {
            bandera_telefono = true;
        }

        if(bandera_num && bandera_nombre && bandera_carrera && bandera_telefono ){
            ProgressDialog dialog = ProgressDialog.show(AddAndUpdateActivity.this, "", "Registrando...", true);
            firestoreHelper.validAddAlumno(dialog, num_control, nombre.toUpperCase(), carrera,telefono, AddAndUpdateActivity.this);
            cleanText();
            ocultarTeclado();
        }else {
            Toast.makeText(AddAndUpdateActivity.this, "Debes llenar todos los campos requeridos.",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNumeric(String number){
        try {
            Double.valueOf(number);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void textWachers(final TextInputLayout textInputLayout){
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() == 0){
                    textInputLayout.setError("Campo vacío");
                }else if(charSequence.length() != 8 ){
                    textInputLayout.setError("Debe tener 8 dígitos");
                }else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void textWachersPhone(final TextInputLayout textInputLayout){
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() == 0){
                    textInputLayout.setError("Campo vacío");
                }else if(charSequence.length() != 10 ){
                    textInputLayout.setError("Debe tener 10 dígitos");
                }else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void cleanText(){
        textInputLayout_nombreCompleto.getEditText().getText().clear();
        textInputLayout_numeroControl.getEditText().getText().clear();
        textInputLayout_telefono.getEditText().getText().clear();
        textInputLayout_Carrera.getEditText().getText().clear();
        textInputLayout_numeroControl.setError(null);
        textInputLayout_telefono.setError(null);
        textInputLayout_numeroControl.requestFocus();
    }

    @Override
    public void getAlumno(Alumno alumno) {
        if(alumno != null){
            this.alumno = alumno;
            textInputLayout_nombreCompleto.getEditText().setEnabled(true);
            textInputLayout_Carrera.getEditText().setEnabled(true);
            textInputLayout_telefono.getEditText().setEnabled(true);

            textInputLayout_nombreCompleto.getEditText().setText(alumno.getNombre());
            ((AutoCompleteTextView)textInputLayout_Carrera.getEditText()).setText(alumno.getCarrera(), false);
            textInputLayout_telefono.getEditText().setText(alumno.getTelefono());
            textInputLayout_telefono.setError(null);
            button_delete.setVisibility(View.VISIBLE);
        }else{
          estandarUpdate();
          cleanText();
          button_delete.setVisibility(View.GONE);
        }
    }

    private void estandarUpdate(){
        textInputLayout_nombreCompleto.getEditText().setEnabled(false);
        textInputLayout_Carrera.getEditText().setEnabled(false);
        textInputLayout_telefono.getEditText().setEnabled(false);
        textInputLayout_numeroControl.setError(null);
        textInputLayout_nombreCompleto.setError(null);
        textInputLayout_Carrera.setError(null);
        textInputLayout_telefono.setError(null);

    }

    private void ocultarTeclado(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}