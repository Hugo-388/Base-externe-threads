package com.example.a07baseexternephread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText etDebut;
    private TextView tvResult;
    private String ville;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        etDebut = findViewById(R.id.etDebut);
    }

    public void rechercher(View v) {
        ville = etDebut.getText().toString();

        // Afficher
        tvResult.setText("Communes débute par \"" + ville + "\" : \n");
        // Mettre la tâche asynchrone
        TacheAsynchrone tacheAsynchrone = new TacheAsynchrone();
        tacheAsynchrone.execute();
    }

    public String getServerDataTexteBrut(String urlAJoindre) {
        StringBuilder res = new StringBuilder();
        String ligne;
        URL url;
        try {
            url = new URL(urlAJoindre);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            while ((ligne = bufferedReader.readLine()) != null) {
                res.append(" - ").append(ligne).append("\n");
            }
        } catch (Exception e) {
            Log.d("Byapp", "Erreur échange avec serveur : " + e.toString());
            return "";
        }
        return res.toString();
    }

    private class TacheAsynchrone extends AsyncTask<Void, Integer, String> {
        private String urlServiceWeb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Appeler le service web pour rechercher dans la base
            urlServiceWeb = "http://172.16.47.18//commune/commune.php?debut=" + ville;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getServerDataTexteBrut(urlServiceWeb);
        }

        @Override
        protected void onPostExecute(String resultatRecherche) {
            super.onPostExecute(resultatRecherche);
            // Ajout de la recherche sur l'interface
            tvResult.append(resultatRecherche);
        }
    }

    private String getServerDataJSON(String urlAJoindre) {
        // Autoriser les opérations réseau sur le thread principal
        StringBuilder res = new StringBuilder();

        URL url;
        try {
            url = new URL(urlAJoindre);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.connect();
            InputStream inputStream = connexion.getInputStream();

            // Récupérer le flux JSON
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String ligne;
            StringBuilder ch = new StringBuilder();
            while ((ligne = bufferedReader.readLine()) != null) {
                ch.append(ligne);
            }
            // Décoder le flux JSON
            JSONArray jsonArray = new JSONArray(ch.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                res.append(" - ").append(jsonObject.getString("nom")).append(" ")
                        .append(jsonObject.getString("cp")).append(" ( ")
                        .append(String.format("%.2f", jsonObject.getDouble("lat"))).append(" ")
                        .append(String.format("%.2f", jsonObject.getDouble("lon"))).append(" )\n");
            }
        } catch (Exception e) {
            Log.d("MyApp", "Erreur échange avec serveur :" + e.toString());
        }
        return res.toString();
    }
}