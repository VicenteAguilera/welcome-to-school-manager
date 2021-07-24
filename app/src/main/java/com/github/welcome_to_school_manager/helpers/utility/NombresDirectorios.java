package com.github.welcome_to_school_manager.helpers.utility;

public enum NombresDirectorios
{
    NOMBRE_DIRECTORIO("PDFsNuevoIngreso"),NOMBRE_DOCUMENTO("PDF_NuevoIngreso"),ETIQUETA_ERROR("ERROR");

    public String texto;
    NombresDirectorios(String texto)
    {
        this.texto=texto;
    }
}
