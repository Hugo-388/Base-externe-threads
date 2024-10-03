package com.example.a07baseexternephread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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

    private EditText etDebut; // Champ de texte pour entrer le début du nom de la ville
    private TextView tvResult; // TextView pour afficher les résultats
    private String ville; // Variable pour stocker le début du nom de la ville

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        tvResult = findViewById(R.id.tvResult);
        etDebut = findViewById(R.id.etDebut);
    }

    // Méthode appelée lors du clic sur le bouton de recherche
    public void rechercher(View v) {
        ville = etDebut.getText().toString(); // Récupérer le texte entré par l'utilisateur

        // Afficher le texte initial dans le TextView
        tvResult.setText("Communes débute par \"" + ville + "\" : \n");

        // Exécuter la tâche asynchrone pour rechercher les villes
        TacheAsynchrone tacheAsynchrone = new TacheAsynchrone();
        tacheAsynchrone.execute();
    }

    // Méthode pour obtenir les données du serveur en texte brut
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

            // Lire chaque ligne de la réponse et l'ajouter au résultat
            while ((ligne = bufferedReader.readLine()) != null) {
                res.append(" - ").append(ligne).append("\n");
            }
        } catch (Exception e) {
            Log.d("Byapp", "Erreur échange avec serveur : " + e.toString());
            return "";
        }
        return res.toString();
    }

    // Classe interne pour exécuter la tâche asynchrone
    private class TacheAsynchrone extends AsyncTask<Void, Integer, String> {
        //Méthode texte brut
        //private String urlServiceWeb;
        //Méthode texte JSON
        private String urlAJoindre;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Construire l'URL du service web avec le début du nom de la ville
            urlAJoindre = "http://172.16.47.18//commune/commune.php?debut=" + ville;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Obtenir les données du serveur en texte brut
            return getServerDataJSON(urlAJoindre);
        }

        @Override
        protected void onPostExecute(String resultatRecherche) {
            super.onPostExecute(resultatRecherche);
            // Ajouter les résultats de la recherche à l'interface utilisateur
            tvResult.append(resultatRecherche);
        }
    }

    // Méthode pour obtenir les données du serveur en format JSON
    private String getServerDataJSON(String urlAJoindre) {
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
                // Ajouter les informations de chaque ville au résultat
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