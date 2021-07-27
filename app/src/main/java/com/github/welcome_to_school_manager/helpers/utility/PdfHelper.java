package com.github.welcome_to_school_manager.helpers.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.github.welcome_to_school_manager.GenerarListasActivity;
import com.github.welcome_to_school_manager.R;
import com.github.welcome_to_school_manager.helpers.models.Alumno;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

import harmony.java.awt.Color;

import static com.github.welcome_to_school_manager.helpers.utility.NombresDirectorios.ETIQUETA_ERROR;
import static com.github.welcome_to_school_manager.helpers.utility.NombresDirectorios.NOMBRE_DIRECTORIO;
import static com.github.welcome_to_school_manager.helpers.utility.NombresDirectorios.NOMBRE_DOCUMENTO;

public class PdfHelper {

    private GenerarListasActivity generarListasActivity;
    private List<Alumno> arrayListAlumnos;

    public PdfHelper(GenerarListasActivity generarListasActivity, List<Alumno> arrayListAlumnos) {
        this.generarListasActivity = generarListasActivity;
        this.arrayListAlumnos = arrayListAlumnos;
    }

    public void crearPDF(String carrera) {
        File directory;
        File file;
        directory = new File(generarListasActivity.getExternalFilesDir(null) + "/" + NOMBRE_DIRECTORIO.texto);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (arrayListAlumnos != null) {
            try {
                file = new File(directory, "/" + NOMBRE_DOCUMENTO.texto + "_" + carrera +"_" + DateHelper.obtenerFecha() + ".pdf");
                Document documento = new Document(PageSize.LETTER.rotate());
                OutputStream os = generarListasActivity.getContentResolver().openOutputStream(Uri.fromFile(file));
                dibujarPDF(documento, (FileOutputStream) os, carrera);
                Toast.makeText(generarListasActivity, "Se creo tu archivo pdf " + Uri.fromFile(file).getPath(), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(generarListasActivity.getApplicationContext(), "No hay datos para generar el pdf.", Toast.LENGTH_SHORT).show();
        }
    }


    private void dibujarPDF(Document documento, FileOutputStream ficheroPdf, String carrera) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(generarListasActivity.getResources(), R.drawable.logo_tec2);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);
            writer.setPageEvent(new WaterMark(imagen));

            //PdfWriter.getInstance(documento, ficheroPdf);
            //documento.add(new Chunk(""));

            Font fontHeaderFooter = FontFactory.getFont(FontFactory.HELVETICA_BOLD, FontFactory.defaultEncoding, FontFactory.defaultEmbedding, 12, Font.BOLD, Color.BLACK);
            Paragraph paragraphHeader = new Paragraph("TECNOLÓGICO NACIONAL DE MÉXICO\n" +
                    "INSTITUTO TECNOLÓGICO SUPERIOR DE URUAPAN\n\n" +
                    "REGISTRO DE ASISTENCIA NUEVO INGRESO\n" + carrera + "\n", fontHeaderFooter);

            Phrase phraseFooter = new Phrase("Educación para  tranformar la vida, TecNM campus Uruapan.\n", fontHeaderFooter);

            HeaderFooter cabecera = new HeaderFooter(new Phrase(Objects.requireNonNull(paragraphHeader)), false);
            cabecera.setAlignment(Element.ALIGN_CENTER);

            HeaderFooter pie = new HeaderFooter(new Phrase(phraseFooter), false);
            pie.setAlignment(Element.ALIGN_CENTER);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            Phrase numControl = new Phrase("NO. CONTROL", fontHeaderFooter);
            Phrase nombreEstudiante = new Phrase("NOMBRE COMPLETO", fontHeaderFooter);
            Phrase carreraAux = new Phrase("CARRERA", fontHeaderFooter);
            Phrase telefono = new Phrase("TELÉFONO", fontHeaderFooter);
            Phrase status = new Phrase("STATUS", fontHeaderFooter);

            PdfPCell cellNumControl = new PdfPCell(numControl);
            cellNumControl.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellNumControl.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellNombreEstudiante = new PdfPCell(nombreEstudiante);
            cellNombreEstudiante.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellNombreEstudiante.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellCarrera = new PdfPCell(carreraAux);
            cellCarrera.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCarrera.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellTelefono = new PdfPCell(telefono);
            cellTelefono.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTelefono.setBackgroundColor(Color.LIGHT_GRAY);

            PdfPCell cellStatus = new PdfPCell(status);
            cellStatus.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellStatus.setBackgroundColor(Color.LIGHT_GRAY);

            documento.open();

            PdfPTable TABLA = new PdfPTable(5);
            TABLA.setWidthPercentage(100);
            TABLA.setWidths(new float[]{(float) 12.5, (float) 30.0, (float) 32.5, (float) 15.0, (float) 10.0});
            TABLA.addCell(cellNumControl);
            TABLA.addCell(cellNombreEstudiante);
            TABLA.addCell(cellCarrera);
            TABLA.addCell(cellTelefono);
            TABLA.addCell(cellStatus);
            TABLA.setHeaderRows(1);

            //int c = 0;
            for (int i = 0; i < arrayListAlumnos.size(); i++) {
                String carreraAux2 = arrayListAlumnos.get(i).getCarrera() + "";
                //Log.e("CAux2", carreraAux2);
                //Log.e("CC", carrera);
                if (carrera.equals(carreraAux2)) {
                    //Log.e("Num", c + "");
                    TABLA.addCell(arrayListAlumnos.get(i).getNumeroControl() + "");
                    TABLA.addCell(arrayListAlumnos.get(i).getNombre() + "");
                    TABLA.addCell(arrayListAlumnos.get(i).getCarrera() + "");
                    TABLA.addCell(arrayListAlumnos.get(i).getTelefono() + "");
                    TABLA.addCell(arrayListAlumnos.get(i).getStatus() + "");
                }
                //c++;
            }
            TABLA.setHorizontalAlignment(Element.ALIGN_CENTER);
            documento.add(TABLA);

        } catch (DocumentException e) {
            Log.e(ETIQUETA_ERROR.texto, Objects.requireNonNull(e.getMessage()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerramos el documento.
            documento.close();
        }
    }
}

