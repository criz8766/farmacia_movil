package cl.cerda.clgrupo3_cerda_ricciardi;

import android.content.Intent; // Importar Intent
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu; // Importar Menu
import android.view.MenuInflater; // Importar MenuInflater
import android.view.MenuItem; // Importar MenuItem
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull; // Importar NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth; // Importar FirebaseAuth

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {

    private ListView listaFarmaciasView;
    private adaptadorFarmacia adaptador;
    private List<Farmacia> listaCompletaFarmacias;
    private SearchView searchView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth; // Instancia de FirebaseAuth para cerrar sesión

    // URL de la API
    private static final String API_URL = "https://midas.minsal.cl/farmacia_v2/WS/getLocalesTurnos.php"; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        Toolbar toolbar = findViewById(R.id.toolbar_menu_principal);
        setSupportActionBar(toolbar);

        listaFarmaciasView = findViewById(R.id.listaRutinas);
        searchView = findViewById(R.id.searchview_rutinas);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance(); // Inicializar FirebaseAuth

        listaCompletaFarmacias = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adaptador != null) {
                    adaptador.filter(newText);
                }
                return false;
            }
        });

        new ObtenerFarmaciasTask().execute(API_URL);
    }

    // Método para inflar el menú en la Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu); // Inflar el menú que creaste
        return true;
    }

    // Método para manejar la selección de elementos del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) { // Si el ID del elemento es el de cerrar sesión
            mAuth.signOut(); // Cerrar sesión de Firebase
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            // Redirigir a la pantalla de Login (MainActivity)
            Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar el historial de actividades
            startActivity(intent);
            finish(); // Finalizar MenuPrincipal
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ObtenerFarmaciasTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            listaFarmaciasView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                result = stringBuilder.toString();
                Log.d("API_Response_Raw", "Respuesta completa de la API: " + result);
            } catch (Exception e) {
                Log.e("MenuPrincipal", "Error al obtener datos de la API", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            listaFarmaciasView.setVisibility(View.VISIBLE);

            if (result != null) {
                try {
                    JSONArray farmaciasArray = new JSONArray(result);

                    listaCompletaFarmacias.clear();

                    for (int i = 0; i < farmaciasArray.length(); i++) {
                        JSONObject farmaciaJson = farmaciasArray.getJSONObject(i);

                        String nombre = farmaciaJson.optString("local_nombre", "N/A");
                        String comuna = farmaciaJson.optString("comuna_nombre", "N/A");
                        String horarioCierre = farmaciaJson.optString("funcionamiento_hora_cierre", "N/A");
                        String direccion = farmaciaJson.optString("local_direccion", "N/A");
                        String telefono = farmaciaJson.optString("local_telefono", "");
                        double latitud = farmaciaJson.optDouble("local_lat", 0.0);
                        double longitud = farmaciaJson.optDouble("local_lng", 0.0);

                        if (comuna.equalsIgnoreCase("Viña del Mar")) {
                            Farmacia farmacia = new Farmacia(nombre, comuna, horarioCierre, direccion, telefono, latitud, longitud);
                            listaCompletaFarmacias.add(farmacia);
                        }
                    }

                    Log.d("MenuPrincipal", "Farmacias de Viña del Mar cargadas: " + listaCompletaFarmacias.size());

                    adaptador = new adaptadorFarmacia(MenuPrincipal.this, listaCompletaFarmacias);
                    listaFarmaciasView.setAdapter(adaptador);

                } catch (JSONException e) {
                    Log.e("MenuPrincipal", "Error al parsear JSON. Contenido recibido: " + result, e);
                    Toast.makeText(MenuPrincipal.this, "Error al procesar los datos de la farmacia. Formato inesperado de la API.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MenuPrincipal.this, "No se pudieron obtener los datos de la farmacia. Verifica tu conexión a internet.", Toast.LENGTH_LONG).show();
            }
        }
    }
}