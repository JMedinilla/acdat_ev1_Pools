package com.proyectos.javi.quiniela_jmedinilla;

import java.util.ArrayList;

/**
 * Clase que almacena la información relacionada con un premio calculado
 */
class Premio {
    String apuestaGanadora;
    String cantidadAcertada;

    public Premio() {
        apuestaGanadora = "";
        cantidadAcertada = "";
    }
}

/**
 * Clase que almacena la información relacionada con una jornada leída
 */
public class Jornada {
    String numeroJornada;
    String recaudacionJornada;

    String premio15Aciertos;
    String premio14Aciertos;
    String premio13Aciertos;
    String premio12Aciertos;
    String premio11Aciertos;
    String premio10Aciertos;

    String resultadoPartidos;

    //Un premio equivale a una apuesta y a la cantidad que ha acertado con respecto al resultado
    ArrayList<Premio> listaApuestasAcertantes;

    public Jornada(){
        numeroJornada = "";
        recaudacionJornada = "";

        premio15Aciertos = "";
        premio14Aciertos = "";
        premio13Aciertos = "";
        premio12Aciertos = "";
        premio11Aciertos = "";
        premio10Aciertos = "";

        resultadoPartidos = "";

        listaApuestasAcertantes = new ArrayList<>();
    }
}
