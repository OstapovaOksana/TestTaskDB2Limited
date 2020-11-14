package com.testtaskcurrency;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class chart extends AppCompatActivity {

    LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chart = (LineChart) findViewById(R.id.chart);
        chart.setNoDataText("Завантаження даних...");
        ChartTask chartTask =  new ChartTask();
        chartTask.execute();

    }

    private class ChartTask extends AsyncTask<String, String, String>{
        int[] years = {2015,2016,2017,2018,2019,2020};
        double[] saleRate = new double[years.length];

        @Override
        protected String doInBackground(String... strings) {
            try{
                for(int i = 0; i < years.length; i++){
                URL url = new URL("https://api.privatbank.ua/p24api/exchange_rates?json&date=01.01." + years[i]);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response responses = client.newCall(request).execute();
                    String jsonData = responses.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    JSONArray exchangeRate = Jobject.getJSONArray("exchangeRate");
                    for(int j = 1; j< exchangeRate.length(); j++){

                            JSONObject row = exchangeRate.getJSONObject(j);
                            String currency = row.getString("currency");
                            if(currency.equals("USD")){
                                String saleRateNB = row.getString("saleRateNB");
                                double sale = Double.parseDouble(saleRateNB);
                                saleRate[i] = sale;
                            }

                    }
                }
            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dataFetched) {


            List<Entry> entries = new ArrayList<Entry>();
            for(int i = 0; i < years.length ; i ++){
                entries.add(new Entry(years[i], (float)saleRate[i]));
            }
            LineDataSet dataSet = new LineDataSet(entries, "Курс $");
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            Description description = new Description();
            description.setText("Зображено курс доллара між 2015-2020 рр");
            chart.setDescription(description);
            chart.invalidate();
        }
    }

}
