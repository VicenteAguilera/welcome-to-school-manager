package com.github.welcome_to_school_manager.repository;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.welcome_to_school_manager.AddAndUpdateActivity;
import com.github.welcome_to_school_manager.Interfaces.Invitado;
import com.github.welcome_to_school_manager.Interfaces.Messages;
import com.github.welcome_to_school_manager.Interfaces.Registros;
import com.github.welcome_to_school_manager.helpers.models.Alumno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class FirestoreHelper {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final CollectionReference AlumnosCollection = db.collection("alumnos");
    private Alumno alumno;

    public void getData(String document, final ProgressDialog dialog, final Context context, Invitado invitado, Messages message) {
        dialog.show();
        AlumnosCollection.document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = document.getData();
                    if (Objects.requireNonNull(document).exists()) {
                        alumno = new Alumno(document.getId(), String.valueOf(data.get("nombre")), String.valueOf(data.get("carrera")), String.valueOf(data.get("telefono")), data.get("status").toString());
                        Log.e("Alumno: ", alumno.getNombre() + " " + alumno.getTelefono());
                        invitado.getAlumno(alumno);
                        dialog.dismiss();
                    } else {
                        message.getMessage("Este alumno no esta en la lista de invitados.");
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void UpdateData(final ProgressDialog dialog, final Context context, Alumno alumno, Messages message) {
        dialog.show();

        Map<String, Object> dataUpdate = new HashMap<>();
        dataUpdate.put("status", alumno.getStatus());

        AlumnosCollection.document(alumno.getNumeroControl()).update(dataUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message.getMessage("Invitacion cancelada.");
                        dialog.dismiss();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        message.getMessage("Invitacion no cancelada, verifica tu conexión a Internet.");
                        dialog.dismiss();
                    }
                });
    }

    public void getAllRegisters(final Registros registros, ProgressDialog dialog) {
        final List<Alumno> listaAlumnos = new ArrayList<>();
        final Alumno[] alumnos = new Alumno[1];
        AlumnosCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final Map<String, Object> alumno_add = document.getData();
                                alumnos[0] = new Alumno(document.getId(), alumno_add.get("nombre").toString(),
                                        alumno_add.get("carrera").toString(), alumno_add.get("telefono").toString(), alumno_add.get("status").toString());
                                listaAlumnos.add(alumnos[0]);
                            }
                            registros.getRegistros(listaAlumnos);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void validAddAlumno(final ProgressDialog dialog, final String num, final String nombre, final String carrera, final String telefono, final Context context) {
        final Map<String, Object> alumno = new HashMap<>();
        alumno.put("nombre", nombre);
        alumno.put("carrera", carrera);
        alumno.put("status", "");
        alumno.put("telefono", telefono);

        AlumnosCollection.document(num).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder.setMessage("El número de control ya existe en la base de datos");

                        alertDialogBuilder.setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface alertDialog, int i) {
                                        alertDialog.dismiss();
                                    }
                                }
                        );

                        dialog.dismiss();
                        alertDialogBuilder.show();
                    } else {
                        addAlumno(num, dialog, alumno, context);
                    }
                } else {
                    Toast.makeText(context, "Error, verifique su conexión a Internet, si los problemas continuan contacte al administrador", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addAlumno(final String num, final ProgressDialog dialog, final Map<String, Object> data, final Context context) {
        AlumnosCollection.document(num).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Aviso");
                    alertDialogBuilder.setMessage("Alumno registrado en el banco de datos.");
                    alertDialogBuilder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface alertDialog, int i) {
                                    alertDialog.cancel();
                                }
                            }
                    );

                    alertDialogBuilder.show();
                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Aviso");
                    alertDialogBuilder.setMessage("Error al registrar alumno en el banco de datos.");
                    alertDialogBuilder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface alertDialog, int i) {
                                    alertDialog.cancel();
                                }
                            }
                    );
                    alertDialogBuilder.show();
                }
            }
        });
    }

    public void getDataAlumno(final Invitado listaAlumno, String num, final ProgressDialog dialog, final Context context) {
        AlumnosCollection.document(num).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        Map<String, Object> data = document.getData();
                        alumno = new Alumno(document.getId(), data.get("nombre").toString(), data.get("carrera").toString(), data.get("telefono").toString());
                        listaAlumno.getAlumno(alumno);
                    } else {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder.setMessage("El alumno buscado no existe en la base de datos");

                        alertDialogBuilder.setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface alertDialog, int i) {
                                        alertDialog.dismiss();
                                    }
                                }
                        );
                        dialog.dismiss();
                        alertDialogBuilder.show();
                        alumno = null;
                        listaAlumno.getAlumno(alumno);
                    }


                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Aviso");
                    alertDialogBuilder.setMessage("Error, verifique su conexión a Internet, si los problemas continuan contacte al administrador");
                    alertDialogBuilder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface alertDialog, int i) {
                                    alertDialog.cancel();
                                }
                            }
                    );
                    alertDialogBuilder.show();
                }
            }
        });
    }

    public void updateDataAlumno(final ProgressDialog dialog, final Context context, final String num, String nombre, String carrera, String telefono) {
        final Map<String, Object> data_alumno = new HashMap<>();
        data_alumno.put("nombre", nombre);
        data_alumno.put("carrera", carrera);
        data_alumno.put("telefono", telefono);

        AlumnosCollection.document(num).update(data_alumno)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder.setMessage("Se actualizaron los datos del alumno.");
                        alertDialogBuilder.setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface alertDialog, int i) {
                                        alertDialog.cancel();
                                    }
                                }
                        );

                        alertDialogBuilder.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Aviso");
                        alertDialogBuilder.setMessage("No se actualizaron los datos del alumno, verifica tu conexión a Internet.");
                        alertDialogBuilder.setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface alertDialog, int i) {
                                        alertDialog.cancel();
                                    }
                                }
                        );

                        alertDialogBuilder.show();
                    }
                });
    }

}
