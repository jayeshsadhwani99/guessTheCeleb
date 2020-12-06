package com.example.guesstheceleb;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenCeleb;
    ImageView imageView;
    String[] answers = new String[4];
    int locationOfAnswer = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view) {
        if(view.getTag().toString().equals(Integer.toString(locationOfAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebnames.get(chosenCeleb) , Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newQuestion() {
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celeburls.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celeburls.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfAnswer = rand.nextInt(4);

            int incorrectAnswerLocation;

            for(int i = 0 ; i < 4 ; i++) {
                if(i == locationOfAnswer) {
                    answers[i] = celebnames.get(locationOfAnswer);
                } else {
                    incorrectAnswerLocation = rand.nextInt(celeburls.size());
                    while(incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(celeburls.size());
                    }
                    answers[i] = celebnames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button);
        button0 = findViewById(R.id.button1);
        button0 = findViewById(R.id.button2);
        button0 = findViewById(R.id.button3);
        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] splitResult = result.split("<div class=\"media\">");

            Pattern p = Pattern.compile("img src = \"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);


            while(m.find()) {
                celeburls.add(m.group(1));
            }

            p = Pattern.compile("<h4>(.*?)</h4>");
            m = p.matcher(splitResult[0]);

            while(m.find()) {
                celebnames.add(m.group(1));
            }
            newQuestion();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
