package com.proyectos.javi.quiniela_jmedinilla;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Clase que interactúa con la memoria externa del dispositivo
 */
public class Memoria {

    public Memoria(){
    }

    /**
     * Método que devuelve un booleano indicando si hay una tarjeta de memoria externa en el dispositivo
     * @return True o False
     */
    public boolean disponibleEscritura(){
        boolean escritura = false;
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED))
            escritura = true;
        return escritura;
    }

    /**
     * Método que da la orden al método de escritura de escribir en memoria externa
     * @param fichero Objeto que indica el nombre del fichero a escribir
     * @param cadena Contenido a escribir en el fichero
     * @param anadir Booleanoi que indica si hay que añadir a un fichero existente o sobreescribir otro
     * @param codigo Codificación del texto
     * @return True o False si ha podido o no escribir
     */
    public boolean escribirExterna(String fichero, String cadena, Boolean anadir, String codigo) {
        File miFichero, tarjeta;
        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);
        return escribir(miFichero, cadena, anadir, codigo);
    }

    /**
     * Método que escribe un contenido en un fichero
     * @param fichero Objecto que indica el nombre del fichero a escribir
     * @param cadena Contenido a escribir en el fichero
     * @param anadir Booleano que indica si hay que añadir a un fichero existente o sobreescribir otro
     * @param codigo Codificación del texto
     * @return True o False si ha podido o no escribir
     */
    private boolean escribir(File fichero, String cadena, Boolean anadir, String codigo) {
        FileOutputStream fos;
        OutputStreamWriter osw;
        BufferedWriter out = null;
        boolean correcto = false;
        try {
            fos = new FileOutputStream(fichero, anadir);
            osw = new OutputStreamWriter(fos, codigo);
            out = new BufferedWriter(osw);
            out.write(cadena);
        } catch (IOException e) {
            Log.e("Error de E/S", e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                    correcto = true;
                }
            } catch (IOException e) {
                Log.e("Error al cerrar", e.getMessage());
            }
        }
        return correcto;
    }
}
