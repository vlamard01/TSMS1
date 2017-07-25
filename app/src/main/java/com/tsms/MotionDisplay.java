package com.tsms;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MotionDisplay extends AppCompatActivity {

    ArrayList<motion> arrayOfWebData = new ArrayList<motion>();
    class motion {
        public String acc;
        public String date;

    }
    String data="";

    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_display);

        btnHit = (Button) findViewById(R.id.btnHit);
        txtJson = (TextView) findViewById(R.id.tvJsonItem);
        final String url = "http://test-env.vdxd9zppmk.us-west-2.elasticbeanstalk.com/api/data/motion";

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask().execute(url);
            }
        });
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MotionDisplay.this);
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
                    Log.d("Response: ", "> " + line);

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
            btnHit.setVisibility(View.INVISIBLE);
            // result="hellohbgrvf"+result;

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    motion resultRow = new motion();
                    resultRow.acc = json_data.getString("motion");
                    resultRow.date = json_data.getString("dateAndTime");


                    arrayOfWebData.add(resultRow);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            data=data+"date              time           x-acc.       y=acc.       z-acc.\n";
            txtJson.setText(arrayOfWebData.get(1).acc);
            int a=arrayOfWebData.size();
            String[] list2=new String[a];
            for(int i =0; i<a; i++) {
                if (i % 5 == 0 && i != 0) {
                    list2[i] = arrayOfWebData.get(i).acc.substring(11, 19);
                }
                data=data+arrayOfWebData.get(i).date.substring(1,10)+"  "+arrayOfWebData.get(i).date.substring(11,19)+"           "+arrayOfWebData.get(i).acc.substring(9,10)+"           "+arrayOfWebData.get(i).acc.substring(19,20)+"               "+arrayOfWebData.get(i).acc.substring(29,33)+"\n";

            }
            txtJson.setText(data);
            List<String> list = new ArrayList<String>();

            for (int i=0; i<jsonArray.length(); i++) {
                try {
                    list.add( jsonArray.getString(i) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }

    }}