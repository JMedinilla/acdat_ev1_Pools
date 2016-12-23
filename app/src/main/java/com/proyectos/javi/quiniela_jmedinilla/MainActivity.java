package com.proyectos.javi.quiniela_jmedinilla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        NumberPicker.OnValueChangeListener,
        CompoundButton.OnCheckedChangeListener {

    //Controles de la aplicación
    RadioButton rdbXml;
    RadioButton rdbJson;
    EditText tbxResultados;
    EditText tbxApuestas;
    EditText tbxPremios;
    EditText tbxExtension_Uno;
    EditText tbxExtension_Dos;
    NumberPicker nmpkMenor;
    NumberPicker nmpkMayor;
    Button btnCotejar;

    //Clientes de descarga de los documentos
    AsyncHttpClient clientXml;
    AsyncHttpClient clientJson;
    AsyncHttpClient clientTxt;

    //Variables que almacenan la información obtenida de los controles de la
    //aplicación al pulsar en el botón
    int jornadaInicial;
    int jornadaFinal;
    String ficheroResultados;
    String ficheroApuestas;
    String ficheroPremios;

    //Variables que almacenan la información obtenida de los documentos descargados
    ArrayList<Jornada> listaDeJornadas;
    String[] listaDeApuestas;

    /**
     * Punto de entrada de la aplicación
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();
    }

    /**
     * Método que inicializa los controles de la aplicación
     */
    private void inicializar() {
        rdbXml = (RadioButton)findViewById(R.id.rdbXml);
        rdbJson = (RadioButton)findViewById(R.id.rdbJson);
        rdbJson.setOnCheckedChangeListener(this);

        tbxResultados = (EditText)findViewById(R.id.tbxResultados);
        tbxApuestas = (EditText)findViewById(R.id.tbxApuestas);
        tbxPremios = (EditText)findViewById(R.id.tbxPremios);

        tbxExtension_Uno = (EditText)findViewById(R.id.tbxExtension_Uno);
        tbxExtension_Dos = (EditText)findViewById(R.id.tbxExtension_Dos);

        nmpkMenor = (NumberPicker)findViewById(R.id.nmpkMenor);
        nmpkMenor.setMinValue(1);
        nmpkMenor.setMaxValue(34);
        nmpkMenor.setOnValueChangedListener(this);

        nmpkMayor = (NumberPicker)findViewById(R.id.nmpkMayor);
        nmpkMayor.setMinValue(1);
        nmpkMayor.setMaxValue(34);

        btnCotejar = (Button)findViewById(R.id.btnCotejar);
        btnCotejar.setOnClickListener(this);
    }

    /**
     * Evento que se lanza cuando se pulsa en el botón, iniciando así el proceso
     * de descarga de los documentos y la comparación de apuestas para escribir
     * el fichero de premios
     */
    @Override
    public void onClick(View v) {
        jornadaInicial = nmpkMenor.getValue();
        jornadaFinal = nmpkMayor.getValue();
        ficheroResultados = tbxResultados.getText().toString() + tbxExtension_Uno.getText().toString();
        ficheroApuestas = tbxApuestas.getText().toString();
        ficheroPremios = tbxPremios.getText().toString() + tbxExtension_Dos.getText().toString();

        if(tbxResultados.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No se ha establecido un nombre para el fichero de resultados", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(tbxApuestas.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No se ha establecido un nombre para el fichero de apuestas", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(tbxPremios.getText().toString().length() == 0){
            Toast.makeText(MainActivity.this, "No se ha establecido un nombre para el fichero de premios", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(MainActivity.this, "Comenzando la descarga . . .", Toast.LENGTH_SHORT).show();

        listaDeJornadas = new ArrayList<>();
        listaDeApuestas = new String[0];

        if (rdbXml.isChecked()) {
            descargarResultadosXml();
        }
        if (rdbJson.isChecked()) {
            descargarResultadosJson();
        }
    }

    /**
     * Evento que se lanza cuando cambia el valor del NumberPicker que representa
     * la jornada inicial. Usado para establecer el número elegido como mínimo del
     * NumberPicker que represanta la jornada final
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        nmpkMayor.setMinValue(newVal);
    }

    /**
     * Evento que se lanza cuando se cambia el RadioButton escogido. Cambia las
     * extensiones del fichero de resultados y premios
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            tbxExtension_Uno.setText(R.string.punto_json);
            tbxExtension_Dos.setText(R.string.punto_json);
        }
        else {
            tbxExtension_Uno.setText(R.string.punto_xml);
            tbxExtension_Dos.setText(R.string.punto_xml);
        }
    }

    /**
     * Método que ejecuta una tarea asíncrona para descargar los resultados en formato XML
     * Si consigue descargar el documento, ejecuta la descarga de apuestas
     */
    public void descargarResultadosXml() {
        clientXml = new AsyncHttpClient();

        clientXml.get(ficheroResultados, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "No se ha podido descargar el fichero de resultados. Compruebe la dirección al fichero", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(MainActivity.this, "Resultados obtenidos en XML", Toast.LENGTH_SHORT).show();
                //
                try {
                    listaDeJornadas = Analisis.analizarResultadosXml(responseString, jornadaInicial, jornadaFinal);
                } catch (XmlPullParserException e) {
                    Toast.makeText(MainActivity.this, "Hay un fallo con el fichero XML indicado", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Hay: " + String.valueOf(listaDeJornadas.size()) + " jornadas", Toast.LENGTH_SHORT).show();

                descargarApuestas();
            }
        });
    }

    /**
     * Método que ejecuta una tarea asíncrona para descargar los resultados en formato JSON
     * Si consigue descargar el documento, ejecuta la descarga de apuestas
     */
    public void descargarResultadosJson() {
        clientJson = new AsyncHttpClient();

        clientJson.get(ficheroResultados, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "No se ha podido descargar el fichero de resultados. Compruebe la dirección al fichero", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(MainActivity.this, "Resultados obtenidos en JSON", Toast.LENGTH_SHORT).show();
                //
                try {
                    listaDeJornadas = Analisis.analizarResultadosJson(responseString, jornadaInicial, jornadaFinal);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Hay un fallo con el fichero JSON indicado", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(MainActivity.this, "Hay " + String.valueOf(listaDeJornadas.size()) + " jornadas", Toast.LENGTH_SHORT).show();

                descargarApuestas();
            }
        });
    }

    /**
     * Método que ejecuta una tarea asíncrona para descargar las apuestas
     * Si consigue descargar el documento, ejecuta el método de comparación de apuestas
     */
    public void descargarApuestas() {
        clientTxt = new AsyncHttpClient();

        clientTxt.get(ficheroApuestas, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Toast.makeText(MainActivity.this, "No se han podido descargar las apuestas", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Toast.makeText(MainActivity.this, "Apuestas obtenidas", Toast.LENGTH_LONG).show();
                //
                try {
                    listaDeApuestas = Analisis.analizarApuestas(response);

                    Toast.makeText(MainActivity.this, "Hay " + String.valueOf(listaDeApuestas.length) + " apuestas", Toast.LENGTH_SHORT).show();

                    cotejarApuestas();
                }
                catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Hay un fallo con el fichero de apuestas indicado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Método que compara las apuestas descargadas con los resultados de cada partido para
     * todas las jornadas obtenidas del documento de resultados
     *
     * Cuando termina ejecuta el método de escritura de premios según el documento descargado,
     * ya sea en XML o en JSON
     */
    public void cotejarApuestas() {
        Analisis.cotejarApuestas(listaDeJornadas, listaDeApuestas);
        Toast.makeText(MainActivity.this, "Se han terminado de cotejar todas las apuestas", Toast.LENGTH_SHORT).show();

        String textoPremios = "";
        try {
            if(rdbXml.isChecked()) {
                Toast.makeText(MainActivity.this, "Escribiendo XML . . .", Toast.LENGTH_SHORT).show();
                textoPremios = Analisis.escribirXml(listaDeJornadas);
            }
            if(rdbJson.isChecked()) {
                Toast.makeText(MainActivity.this, "Escribiendo JSON . . .", Toast.LENGTH_SHORT).show();
                textoPremios = Analisis.escribirJson(listaDeJornadas);
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        guardarFicheroPremios(textoPremios);
    }

    /**
     * Método que llama al método de escritura en memoria externa lo que hay que escribir en un fichero
     * @param ficPremios Contenido a escribir en el documento
     */
    public void guardarFicheroPremios(String ficPremios) {
        Memoria memoria = new Memoria();
        if(memoria.disponibleEscritura()) {
            memoria.escribirExterna(ficheroPremios, ficPremios, false, "UTF-8");
            Toast.makeText(MainActivity.this, "¡Premios escritos con éxito!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this, "No se ha podido acceder a la memoria externa", Toast.LENGTH_SHORT).show();
        }
    }
}
