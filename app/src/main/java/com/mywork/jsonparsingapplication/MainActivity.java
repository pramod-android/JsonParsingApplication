package com.mywork.jsonparsingapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHit = (Button) findViewById(R.id.btnHit);
        txtJson = (TextView) findViewById(R.id.tvJsonItem);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncTaskToGetData().execute("http://api.androidhive.info/contacts/");
               // new JsonTask().execute("http://api.myjson.com/bins/d5y1e");//http://api.androidhive.info/contacts/");
            }
        });


    }



    private class asyncTaskToGetData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            String jsonStr=null;
            try {
                 //Making a request to url and getting response
                    HttpClient client = new DefaultHttpClient();
                   HttpGet request = new HttpGet();
                 request.setURI(new URI(params[0]));
                 HttpResponse response = client.execute(request);
                  jsonStr = EntityUtils.toString(response.getEntity());
                 } catch (MalformedURLException e) {
                  Log.e(TAG, "MalformedURLException: " + e.getMessage());
                 } catch (ProtocolException e) {
                   Log.e(TAG, "ProtocolException: " + e.getMessage());
                 } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e.getMessage());
                 } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                 }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            txtJson.setText(result);

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=new JSONArray();
                jsonArray=jsonObject.getJSONArray("contacts");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject data=jsonArray.getJSONObject(i);
                    String name=data.getString("name");
                    JSONObject numbers=data.getJSONObject("phone");
                    String mobile=numbers.getString("mobile");
                    String home=numbers.getString("home");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            txtJson.setText(result);
        }
    }


}