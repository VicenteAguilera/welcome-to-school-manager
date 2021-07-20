package com.github.welcome_to_school_manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.helpers.CaptureActivityPortrait;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.github.welcome_to_school_manager.helpers.utility.Encriptacion;
import com.github.welcome_to_school_manager.repository.FirbaseBatches;
import com.github.welcome_to_school_manager.repository.FirestoreHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Invitado, Messages {

    private IntentResult result= null;
    private Button button_scannear;
    private FirestoreHelper fireStoreHelper = new FirestoreHelper();
    private LottieAnimationView animationView;
    private TextView textView_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationView = findViewById(R.id.animationViewG);
        button_scannear = findViewById(R.id.button_scannear);
        button_scannear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanearQR();
            }
        });
        animationView.setMinAndMaxFrame(193,194);
        textView_about = findViewById(R.id.textView_about);
        textView_about.setText(textView_about.getText()+new SimpleDateFormat("yyyy").format(new Date()));
    }

    private void escanearQR()
    {
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Escanear código QR");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if(result.getContents() != null)
            {
                ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Buscando invitación...", true);
                String values = result.getContents();

                try {
                    final String array[] = new Encriptacion().decryptAE(values).split("\\|");
                    if(array[0].length() == 8 && isNumeric(array[0])){
                        fireStoreHelper.getData(array[0], dialog, MainActivity.this, MainActivity.this, MainActivity.this);
                    }else {
                        showAlertQRInvalid(dialog);

                    }
                }catch (Exception e){
                   showAlertQRInvalid(dialog);
                }
            }
            else
            {
                Snackbar.make(findViewById(android.R.id.content), "Cancelaste escaneo.", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this,"Cancelaste escaneo.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNumeric(String s){
        try {
            Integer.valueOf(s);
            return true;
        }catch (NumberFormatException e){ return false; }
    }


    private void showAlertQRInvalid(Dialog dialog){
        dialog.dismiss();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("QR Inválido.");
        alertDialogBuilder.setMessage("El QR escaneado no contiene la información requerida.");
        alertDialogBuilder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alertDialog, int i)
                    {
                        alertDialog.cancel();

                    }
                }
        );
        alertDialogBuilder.show();

    }

    private void showCancelInvitation(Alumno alumno){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            View view = inflater.inflate(R.layout.dialog_cancel_invitation, null);
            builder.setView(view);

            final AlertDialog dialogSearchInvitation =builder.create();
            dialogSearchInvitation.setCancelable(false);
            Button buttonCancelar = view.findViewById(R.id.button_cancelar);
            /*Button buttonClose = view.findViewById(R.id.buttonClose);*/

            TextView textView_Ncontrol = view.findViewById(R.id.textView_Ncontrol);
            TextView textView_Nombre = view.findViewById(R.id.textView_Nombre);
            TextView textView_Carrera = view.findViewById(R.id.textView_Carrera);
            TextView textView_Telefono = view.findViewById(R.id.textView_Telefono);

            //textView_Nsilla.setText("Código de asiento: "+alumno.getNumeroSilla());
            textView_Ncontrol.setText("Número de control: "+alumno.getNumeroControl());
            textView_Nombre.setText("Nombre: "+alumno.getNombre());
            textView_Carrera.setText("Carrera: "+alumno.getCarrera());
            textView_Telefono.setText("Teléfono: "+alumno.getTelefono());

            dialogSearchInvitation.show();

             buttonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alumno.setStatus(alumno.getStatus()+1);
                    ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Cancelando invitación...", true);
                    fireStoreHelper.UpdateData(dialog, MainActivity.this, alumno, MainActivity.this);
                    dialogSearchInvitation.dismiss();
                }
            });

            /*buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogSearchInvitation.dismiss();
                }
            });*/
    }

    @Override
    public void getAlumno(Alumno alumno) {
       /* if(alumno.getStatus()<2)
        {
            showCancelInvitation(alumno);
        }
        else
        {
            AlertDialog.Builder  alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setTitle("Invitaciones canceladas.");
            alertDialogBuilder.setMessage("Los invitados ya están dentro de la ceremonia.");
            alertDialogBuilder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface alertDialog, int i)
                        {
                            alertDialog.cancel();
                        }
                    }
            );
            alertDialogBuilder.show();
        }*/

    }


    @Override
    public void getMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     *  MENU POPUP
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.overflow,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.item_AcercaDe) {
            Toast.makeText(MainActivity.this, getResources().getText(R.string.acerca_de) + "...", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(MainActivity.this, getResources().getText(R.string.gestor_central) + "...", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, ManagementActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}