package com.example.weathro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText, unitEditText;
    private TextView weatherDescription, temperatureText;
    private ImageView weatherImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        unitEditText = findViewById(R.id.unitEditText);
        weatherDescription = findViewById(R.id.weatherDescription);
        temperatureText = findViewById(R.id.temperatureText);
        weatherImageView = findViewById(R.id.weatherImageView);
    }

    public void getWeather(View view) {
        String city = cityEditText.getText().toString();
        String unit = unitEditText.getText().toString().toLowerCase();

        if (city.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please enter both city and unit", Toast.LENGTH_SHORT).show();
            return;
        }

        String unitParam = "metric"; // Default unit is Celsius
        if (unit.equals("k")) {
            unitParam = "standard";
        } else if (unit.equals("f")) {
            unitParam = "imperial";
        }

        String apiKey = "3df6836ada8bae1010a0706d2b45f0af";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=" + unitParam;

        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();
                urlConnection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(stringBuilder.toString());
                JSONObject main = response.getJSONObject("main");
                double temperature = main.getDouble("temp");
                String description = response.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = response.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                runOnUiThread(() -> {
                    weatherDescription.setText("Weather: " + description);
                    temperatureText.setText("Temperature: " + temperature + "°");
                    Picasso.get().load(iconUrl).into(weatherImageView);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to get weather data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
