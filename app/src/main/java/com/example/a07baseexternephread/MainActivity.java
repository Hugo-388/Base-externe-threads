package com.example.a07baseexternephread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.AsyncListUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText etDebut;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvResult = findViewById(R.id.tvResult);
        etDebut = findViewById(R.id.etDebut);
    }

    public void rechercher(View v) {
        String ville = etDebut.getText().toString();


        //Appeler le service web pour rechercher dans la base
        String urlServiceWeb = "http://172.16.47.18/commune/commune.php?debut=" + ville;

        //Aficher
        tvResult.setText("Communes débute par \"" + ville + "\" : \n");
        tvResult.append(getServerDataJSON(urlServiceWeb));


    }

    private class TacheAsychrone extends AsyncTask<Void, Integer, String> {
        //Méthode implémenter

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}