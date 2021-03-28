package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Random rand;
    Pattern p;
    Matcher m;
    Bitmap[] images;
    String[] names,options;
    ImageView imageView;
    Button option1,option2,option3,option4;
    int max=99, min=0,num,correctOption;
    int num1,num2,num3,num4;
    String answer="",message = "Wrong! ";


    public class ImageDownload extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap img = BitmapFactory.decodeStream(in);
                return img;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadWebContent extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result= new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1)
                {
                    char curr = (char)data;
                    result.append(curr);
                    data=reader.read();
                }
                return result.toString();
            }
            catch (Exception e){
                e.printStackTrace();
                return "FAILED!";
            }
        }
    }

    public void game(){
        rand = new Random();
        num = rand.nextInt(max-min+1)+min;
        correctOption = rand.nextInt(4-1+1)+1;
        answer = names[num];
        imageView.setImageBitmap(images[num]);
        num1 = rand.nextInt(max-min+1)+min;
        while(num1==num){num1 = rand.nextInt(max-min+1)+min;}
        options[0] = names[num1];
        num2 = rand.nextInt(max-min+1)+min;
        while(num2==num || num2==num1){num2 = rand.nextInt(max-min+1)+min;}
        options[1] = names[num2];
        num3 = rand.nextInt(max-min+1)+min;
        while(num3==num || num3==num1 || num3==num2){num3 = rand.nextInt(max-min+1)+min;}
        options[2] = names[num3];
        num4 = rand.nextInt(max-min+1)+min;
        while(num4==num || num4==num1 || num4==num2 || num4==num3){num4 = rand.nextInt(max-min+1)+min;}
        options[3] = names[num4];
        options[correctOption-1] = answer;
        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);
        option4.setText(options[3]);
    }

    public void onClick(View view){
        if(view.getId()==R.id.option1 && options[0].equals(answer)==true)message = "That's Correct";
        else if(view.getId()==R.id.option2 && options[1].equals(answer)==true)message = "That's Correct";
        else if(view.getId()==R.id.option3 && options[2].equals(answer)==true)message = "That's Correct";
        else if(view.getId()==R.id.option4 && options[3].equals(answer)==true)message = "That's Correct";
        else message+="The correct answer is "+answer;
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
        message="Wrong! ";
        game();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        option1 = (Button) findViewById(R.id.option1);
        option2 = (Button) findViewById(R.id.option2);
        option3 = (Button) findViewById(R.id.option3);
        option4 = (Button) findViewById(R.id.option4);
        images = new Bitmap[200];
        names = new String[200];
        options = new String[4];
        DownloadWebContent task = new DownloadWebContent();
        String result = null;
        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] splitResult = result.split("<div class=\"lister-list\">");
            int count=0;
            p = Pattern.compile("img alt=\"(.*?)\"");
            m = p.matcher(splitResult[1]);
            while(m.find() && count<100)
            {
               names[count]=m.group(1);
                Log.i("Celebrity "+Integer.toString(count),m.group(1));
                count++;
            }
            count=0;
            p = Pattern.compile("src=\"(.*?)\"");
            m = p.matcher(splitResult[1]);
            while(m.find() && count<100)
            {
                ImageDownload imgD = new ImageDownload();
                images[count] = imgD.execute(m.group(1)).get();
                count++;
            }
            game();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.i("RESULT",result);
    }
}