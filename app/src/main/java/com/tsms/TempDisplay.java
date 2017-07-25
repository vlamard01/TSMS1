package com.tsms;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

public class TempDisplay extends AppCompatActivity {

    ArrayList<temp> arrayOfWebData = new ArrayList<temp>();
    class temp {
        public String temperature;
        public String date;

    }


    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        btnHit = (Button) findViewById(R.id.btnHit);

        final String url = "http://test-env.vdxd9zppmk.us-west-2.elasticbeanstalk.com/api/data/temperature";

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

            pd = new ProgressDialog(TempDisplay.this);
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
                    buffer.append(line + "\n");
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
            if (pd.isShowing()) {
                pd.dismiss();
            }
            btnHit.setVisibility(View.INVISIBLE);

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    temp resultRow = new temp();
                    resultRow.temperature = json_data.getString("temperature");
                    resultRow.date = json_data.getString("dateAndTime");


                    arrayOfWebData.add(resultRow);
                }

                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.setVisibility(View.VISIBLE);
                LineGraphSeries<DataPoint> series;
                series = new LineGraphSeries<DataPoint>();
                double y, x;
                int a = arrayOfWebData.size();
                String[] list = new String[a];
                for (int i = 0; i < a; i++) {
                    x = i;
                    if (i % 5 == 0 && i != 0) {
                        list[i] = arrayOfWebData.get(i).date.substring(11, 19);
                    }
                    y = Double.parseDouble(arrayOfWebData.get(i).temperature);
                    series.appendData(new DataPoint(x, y), true, 1000);

                }
                graph.setTitle("temperature-time graph");

                StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);

                staticLabelsFormatter.setHorizontalLabels(list);

                graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


                graph.addSeries(series);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }}