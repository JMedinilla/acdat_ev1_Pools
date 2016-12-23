package com.proyectos.javi.quiniela_jmedinilla;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Clase contenedora de los métodos que examinan y crean ficheros
 */
public class Analisis {

    /**
     * Método que lee el contenido del documento de resultados en XML para obtener los datos de cada jornada
     * @param response Contenido del fichero de resultados en XML
     * @param jornadaInicial Jornada desde la que se comienza a leer
     * @param jornadaFinal Jornada desde en que se termina la lectura
     * @return Lista de jornadas leídas del documento de resultados
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<Jornada> analizarResultadosXml(String response, int jornadaInicial, int jornadaFinal) throws XmlPullParserException, IOException {
        ArrayList<Jornada> result = new ArrayList<>();

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new StringReader(response));
        int eventType = xpp.getEventType();

        Jornada jornada = null;

        while (eventType != XmlPullParser. END_DOCUMENT ) {
            switch (eventType) {
                case XmlPullParser. START_TAG :
                    if(xpp.getName().equals("quiniela")){
                        jornada = new Jornada();
                        jornada.resultadoPartidos = "";

                        jornada.numeroJornada = xpp.getAttributeValue(0);
                        jornada.recaudacionJornada = xpp.getAttributeValue(2);
                        jornada.premio15Aciertos = xpp.getAttributeValue(3);
                        jornada.premio14Aciertos = xpp.getAttributeValue(4);
                        jornada.premio13Aciertos = xpp.getAttributeValue(5);
                        jornada.premio12Aciertos = xpp.getAttributeValue(6);
                        jornada.premio11Aciertos = xpp.getAttributeValue(7);
                        jornada.premio10Aciertos = xpp.getAttributeValue(8);
                    }
                    if(xpp.getName().equals("partit")) {
                        if (jornada != null) {
                            jornada.resultadoPartidos = jornada.resultadoPartidos + xpp.getAttributeValue("", "sig");
                        }
                    }
                    break;
                case XmlPullParser. TEXT :
                    break;
                case XmlPullParser. END_TAG :
                    if(xpp.getName().equals("quiniela")){
                        if (jornada != null) {
                            if (Integer.valueOf(jornada.numeroJornada) >= jornadaInicial) {
                                if (Integer.valueOf(jornada.numeroJornada) <= jornadaFinal) {
                                    result.add(jornada);
                                }
                            }
                        }
                    }
                    break;
            }
            eventType = xpp.next();
        }

        return result;
    }

    /**
     * Método que lee el contenido del documento de resultados en JSON para obtener los datos de cada jornada
     * @param response Contenido del fichero de resultados en JSON
     * @param jornadaInicial Jornada desde la que se comienza a leer
     * @param jornadaFinal Jornada desde en que se termina la lectura
     * @return Lista de jornadas leídas del documento de resultados
     * @throws JSONException
     */
    public static ArrayList<Jornada> analizarResultadosJson(String response, int jornadaInicial, int jornadaFinal) throws JSONException {
        ArrayList<Jornada> result = new ArrayList<>();

        Jornada jornada;

        JSONObject JSON = new JSONObject(response);
        JSONObject quinielista = JSON.getJSONObject("quinielista");
        JSONArray quiniela = quinielista.getJSONArray("quiniela");

        for (int i = jornadaInicial - 1; i < jornadaFinal; i++) {
            jornada = new Jornada();
            jornada.resultadoPartidos = "";

            JSONObject jrnd = quiniela.getJSONObject(i);
            jornada.numeroJornada = jrnd.getString("-jornada");
            jornada.recaudacionJornada = jrnd.getString("-recaudacion");
            jornada.premio15Aciertos = jrnd.getString("-el15");
            jornada.premio14Aciertos = jrnd.getString("-el14");
            jornada.premio13Aciertos = jrnd.getString("-el13");
            jornada.premio12Aciertos = jrnd.getString("-el12");
            jornada.premio11Aciertos = jrnd.getString("-el11");
            jornada.premio10Aciertos = jrnd.getString("-el10");

            JSONArray partit = jrnd.getJSONArray("partit");

            for (int j = 0; j < partit.length(); j++) {
                JSONObject prtt = partit.getJSONObject(j);
                jornada.resultadoPartidos = jornada.resultadoPartidos + prtt.getString("-sig");
            }

            result.add(jornada);
        }

        return result;
    }

    /**
     * Método que devuelve una lista de cadenas de texto con las apuestas leídas del documento de apuestas
     * @param response Contenido del documento de apuestas
     * @return Lista de apuestas
     */
    public static String[] analizarApuestas(String response) {
        return response.split("\n");
    }

    /**
     * Método que compara las apuestas descargadas con los resultados de los partidos de cada jornada
     * @param listaDeJornadas Lista de jornadas obtenidas del documento de resultados
     * @param listaDeApuestas Lista de apuestas obtenidas del documento de apuestas
     */
    public static void cotejarApuestas(ArrayList<Jornada> listaDeJornadas, String[] listaDeApuestas) {
        for (int i = 0; i < listaDeJornadas.size(); i++) {
            for (String listaDeApuesta : listaDeApuestas) {
                int coincidencias = 0;

                String uno = listaDeJornadas.get(i).resultadoPartidos;

                for (int k = 0; k < 14; k++) {
                    if (uno.charAt(k) == listaDeApuesta.charAt(k)) {
                        coincidencias++;
                    }
                }

                if (coincidencias == 14) {
                    String final_uno = String.valueOf(uno.charAt(14)) + String.valueOf(uno.charAt(15));
                    String final_dos = String.valueOf(listaDeApuesta.charAt(14)) + String.valueOf(listaDeApuesta.charAt(15));

                    if (final_uno.contains(final_dos)) {
                        coincidencias++;
                    }
                }

                if (coincidencias >= 10) {
                    Premio prm = new Premio();
                    prm.apuestaGanadora = listaDeApuesta;
                    prm.cantidadAcertada = String.valueOf(coincidencias);
                    listaDeJornadas.get(i).listaApuestasAcertantes.add(prm);
                }
            }
        }
    }

    /**
     * Método que devuelve al método de escritura la cadena que debe escribir en formato XML
     * @param listaDeJornadas Lista de jornadas que hay que escribir en el documento
     * @return Contenido a escribir en el fichero
     * @throws IOException
     */
    public static String escribirXml(ArrayList<Jornada> listaDeJornadas) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);
        //Comienzo del documento
        xmlSerializer.startDocument("UTF-8", true);
        //Etiqueta inicial <quiniela>
        xmlSerializer.startTag("", "quiniela");
        //Bucle del contenido
        for (int i = 0; i < listaDeJornadas.size(); i++) {
            //Etiqueta inicial <jornada>
            xmlSerializer.startTag("", "jornada");
            xmlSerializer.attribute("", "numero", listaDeJornadas.get(i).numeroJornada);
            xmlSerializer.attribute("", "recaudacion", listaDeJornadas.get(i).recaudacionJornada);
            xmlSerializer.attribute("", "premio_de_15", listaDeJornadas.get(i).premio15Aciertos);
            xmlSerializer.attribute("", "premio_de_14", listaDeJornadas.get(i).premio14Aciertos);
            xmlSerializer.attribute("", "premio_de_13", listaDeJornadas.get(i).premio13Aciertos);
            xmlSerializer.attribute("", "premio_de_12", listaDeJornadas.get(i).premio12Aciertos);
            xmlSerializer.attribute("", "premio_de_11", listaDeJornadas.get(i).premio11Aciertos);
            xmlSerializer.attribute("", "premio_de_10", listaDeJornadas.get(i).premio10Aciertos);

            for (int j = 0; j < listaDeJornadas.get(i).listaApuestasAcertantes.size(); j++) {

                //Etiqueta inicial <premio>
                xmlSerializer.startTag("", "premio");
                xmlSerializer.attribute("", "acertante", listaDeJornadas.get(i).listaApuestasAcertantes.get(j).apuestaGanadora);
                xmlSerializer.attribute("", "cantidad", listaDeJornadas.get(i).listaApuestasAcertantes.get(j).cantidadAcertada);
                //Etiqueta final <premio>
                xmlSerializer.endTag("", "premio");

            }
            //Etiqueta final <jornada>
            xmlSerializer.endTag("", "jornada");
        }
        //Etiqueta final <quiniela>
        xmlSerializer.endTag("", "quiniela");
        //Final del documento
        xmlSerializer.endDocument();

        return writer.toString();
    }

    /**
     * Método que devuelve al método de escritura la cadena que debe escribir en formato JSON
     * @param listaDeJornadas Lista de jornadas que hay que escribir en el documento
     * @return Contenido a escribir en el fichero
     * @throws JSONException
     */
    public static String escribirJson(ArrayList<Jornada> listaDeJornadas) throws JSONException {
        JSONObject quinielista = new JSONObject();
        JSONArray quiniela = new JSONArray();

        for (int i = 0; i < listaDeJornadas.size(); i++) {
            JSONObject jornada = new JSONObject();
            jornada.put("numero", listaDeJornadas.get(i).numeroJornada);
            jornada.put("recaudacion", listaDeJornadas.get(i).recaudacionJornada);
            jornada.put("premio_de_15", listaDeJornadas.get(i).premio15Aciertos);
            jornada.put("premio_de_14", listaDeJornadas.get(i).premio14Aciertos);
            jornada.put("premio_de_13", listaDeJornadas.get(i).premio13Aciertos);
            jornada.put("premio_de_12", listaDeJornadas.get(i).premio12Aciertos);
            jornada.put("premio_de_11", listaDeJornadas.get(i).premio11Aciertos);
            jornada.put("premio_de_10", listaDeJornadas.get(i).premio10Aciertos);

            JSONArray premios = new JSONArray();

            for (int j = 0; j < listaDeJornadas.get(i).listaApuestasAcertantes.size(); j++) {
                JSONObject premio = new JSONObject();
                premio.put("acertante", listaDeJornadas.get(i).listaApuestasAcertantes.get(j).apuestaGanadora);
                premio.put("cantidad", listaDeJornadas.get(i).listaApuestasAcertantes.get(j).cantidadAcertada);

                premios.put(premio);
            }

            jornada.put("premios", premios);
            quiniela.put(jornada);
        }
        quinielista.put("jornada", quiniela);

        return quinielista.toString();
    }
}
