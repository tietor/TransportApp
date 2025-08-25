package ch.teko.transportapp;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

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
                Thread thread = new Thread(() -> {
                    try {
                        URL url;
                        url = new URL("http://transport.opendata.ch/v1/locations?query=" + place);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        int responseCode = con.getResponseCode();
                        if (HttpURLConnection.HTTP_OK == responseCode) {
                            System.out.println("successfully executed request");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();



            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}