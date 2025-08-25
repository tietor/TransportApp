package ch.teko.transportapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import ch.teko.transportapp.entity.Coordinate;
import ch.teko.transportapp.entity.Place;
import ch.teko.transportapp.entity.Station;

public class MainActivity extends AppCompatActivity {
    private Place response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(view -> {
            EditText editTextInput = findViewById(R.id.ort);
            Editable text = editTextInput.getText();
            if (text != null) {
                String place = text.toString();
                Thread thread = executeRequestAndSetResponse(place);

                try {
                    thread.join();
                    if (this.response != null) {
                        setStationsInTable(this.response.getStations());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @NonNull
    private Thread executeRequestAndSetResponse(String place) {
        Thread thread = new Thread(() -> {
            try {
                URL url;
                url = new URL("http://transport.opendata.ch/v1/locations?query=" + place);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responseCode) {
                    StringBuilder response = getResponse(con);
                    this.response = mapResponseToPlace(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return thread;
    }

    private void setStationsInTable(ArrayList<Station> stations) {
        TableLayout tableLayout = findViewById(R.id.stations);
        tableLayout.removeAllViews();
        stations.forEach(station -> {
            TextView stationNameView = new TextView(this);
            stationNameView.setText(station.getName());

            TextView iconView = new TextView(this);
            iconView.setText(station.getIcon());

            TableRow tableRow = new TableRow(this);
            tableRow.addView(stationNameView);
            tableRow.addView(iconView);

            tableRow.setOnClickListener(view -> {
                Coordinate coordinate = station.getCoordinate();
                Uri mapView = Uri.parse("geo:" + coordinate.getX() + "," + coordinate.getY());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapView);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            });

            tableLayout.addView(tableRow);
        });

    }

    private static Place mapResponseToPlace(StringBuilder response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.toString(), Place.class);
    }

    @NonNull
    private static StringBuilder getResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }
}