package com.example.tableforone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.*;
import java.io.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RetrieveFeedTask().execute(urlToRssFeed);

    }


    public void onBtnClick(View view) {
        EditText inputFood = (EditText) findViewById(R.id.foodName);
        TextView prompt = (TextView) findViewById(R.id.prompt);
        String foodName = inputFood.getText().toString();
        String urlParameters = "param1=a&param2=b&param3=c";
        String ingredients = executePost(foodName, urlParameters);
        prompt.setText(ingredients);
    }

    public static String executePost(String foodName, String urlParameters) {
        HttpURLConnection connection = null;
        String targetURL = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + foodName;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            try {
                connection.connect();
            }
            catch (Exception e) {
                return e.toString();
            }

            //DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            //Send request
            /*
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
             */
            return "yes";
            //return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return foodName; // Originally null
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, RSSFeed>{
        private Exception exception;

        protected RSSFeed doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlreader = parser.getXMLReader();
                RssHandler theRSSHandler = new RssHandler();
                xmlreader.setContentHandler(theRSSHandler);
                InputSource is = new InputSource(url.openStream());
                xmlreader.parse(is);

                return theRSSHandler.getFeed();
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
                is.close();
            }
        }

        protected void onPostExecute(RSSFeed feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}