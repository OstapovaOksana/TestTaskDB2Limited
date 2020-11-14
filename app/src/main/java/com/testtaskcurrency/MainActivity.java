package com.testtaskcurrency;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    EditText date1;
    EditText date2;
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    TableLayout tablePB;
    TableLayout tableNBU;
    ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        date1 = (EditText) findViewById(R.id.edit1);
        date2 = (EditText) findViewById(R.id.edit2);
        tablePB = (TableLayout) findViewById(R.id.tableLayout2);
        tableNBU = (TableLayout) findViewById(R.id.tableLayout4);
        scroll = (ScrollView) findViewById(R.id.scroll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_graph) {
            Intent intent = new Intent(MainActivity.this, chart.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,R.style.DatePickerDialog, firstDateListener, year, month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void onClickbtn2(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,R.style.DatePickerDialog, secondDateListener, year, month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


        DatePickerDialog.OnDateSetListener firstDateListener =  new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String text = dayOfMonth + "." + (monthOfYear+1) + "." + year;
            date1.setText(text);

            PBTask fetchDataTask = new PBTask();
            fetchDataTask.execute(text);

            }
        };

    DatePickerDialog.OnDateSetListener secondDateListener =  new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String text = dayOfMonth + "." + (monthOfYear+1) + "." + year;
            date2.setText(text);
            NBUTask nbuTask = new NBUTask();
            nbuTask.execute(text);
        }
    };


    private class PBTask extends AsyncTask<String, String, String> {

        JSONArray exchangeRate = null;
        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("https://api.privatbank.ua/p24api/exchange_rates?json&date="+strings[0]);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response responses = client.newCall(request).execute();
                String jsonData = responses.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                exchangeRate = Jobject.getJSONArray("exchangeRate");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

            @Override
        protected void onPostExecute(String dataFetched) {
            //parse the JSON data and then display
            //Log.e("LOOOOOOOOOOOOOOG", this.currency);
               if(tablePB.getChildCount() > 1){
                   tablePB.removeViews(1,tablePB.getChildCount()-1);
               }
                TextView[] textArray = new TextView[this.exchangeRate.length()];
                TextView[] sale = new TextView[this.exchangeRate.length()];
                TextView[] pur = new TextView[this.exchangeRate.length()];
                TableRow[] tableRow = new TableRow[this.exchangeRate.length()];
                for(int i = 1; i< this.exchangeRate.length(); i++){
                    try {
                        JSONObject row = this.exchangeRate.getJSONObject(i);
                        if(row.has("saleRate")){
                            String currency = row.getString("currency");
                            String saleRate = row.getString("saleRate");
                            String purchaseRate = row.getString("purchaseRate");
                            tableRow[i] = new TableRow(MainActivity.this);
                            tableRow[i].setId(i+1);

                            textArray[i] = new TextView(MainActivity.this);
                            textArray[i].setId(i+111);
                            textArray[i].setText(currency);
                            textArray[i].setTextSize(20);
                            textArray[i].setGravity(Gravity.CENTER);

                            sale[i] = new TextView(MainActivity.this);
                            sale[i].setId(i+111);
                            sale[i].setText(saleRate);
                            sale[i].setTextSize(20);
                            sale[i].setGravity(Gravity.CENTER);

                            pur[i] = new TextView(MainActivity.this);
                            pur[i].setId(i+111);
                            pur[i].setText(purchaseRate);
                            pur[i].setTextSize(20);
                            pur[i].setGravity(Gravity.CENTER);

                            tableRow[i].addView(textArray[i], 0);
                            tableRow[i].addView(pur[i], 1);
                            tableRow[i].addView(sale[i], 2);
                            tableRow[i].setClickable(true);
                            tableRow[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    for(int i = 0; i < tableNBU.getChildCount(); i++){
                                        TableRow nburow = (TableRow) tableNBU.getChildAt(i);
                                        nburow.setBackgroundResource(R.drawable.row_border);
                                    }

                                    TableRow t = (TableRow) v;
                                    TextView firstTextView = (TextView) t.getChildAt(0);
                                    String text = firstTextView.getText().toString();
                                    for(int i = 0; i < tableNBU.getChildCount(); i++){
                                        TableRow nburow = (TableRow) tableNBU.getChildAt(i);
                                        TextView nbutextview = (TextView) nburow.getChildAt(0);
                                        String nbutext = nbutextview.getText().toString();
                                        if(nbutext.equals(text)){
                                            scroll.scrollTo(0,nburow.getTop());
                                            nburow.setBackgroundColor(Color.YELLOW);
                                            break;
                                        }
                                    }

                                }
                            });
                            tableRow[i].setBackgroundResource(R.drawable.row_border);
                            tablePB.addView(tableRow[i], new TableLayout.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.MATCH_PARENT)
                            );

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        }
    }


    private class NBUTask extends AsyncTask<String, String, String> {

        JSONArray exchangeRate = null;
        @Override
        protected String doInBackground(String ... strings) {

            try {
                URL url = new URL("https://api.privatbank.ua/p24api/exchange_rates?json&date="+strings[0]);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response responses = client.newCall(request).execute();
                String jsonData = responses.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                exchangeRate = Jobject.getJSONArray("exchangeRate");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String dataFetched) {

            if(tableNBU.getChildCount() > 1){
                tableNBU.removeViews(1,tableNBU.getChildCount()-1);
            }
            TextView[] textArray = new TextView[this.exchangeRate.length()];
            TextView[] sale = new TextView[this.exchangeRate.length()];
            TextView[] pur = new TextView[this.exchangeRate.length()];

            TableRow[] tableRow = new TableRow[this.exchangeRate.length()];
            for(int i = 1; i< this.exchangeRate.length(); i++){
                try {
                    JSONObject row = this.exchangeRate.getJSONObject(i);

                        String currency = row.getString("currency");
                        String saleRate = row.getString("saleRateNB");
                        String purchaseRate = row.getString("purchaseRateNB");
                        tableRow[i] = new TableRow(MainActivity.this);
                        tableRow[i].setId(i+1);

                        textArray[i] = new TextView(MainActivity.this);
                        textArray[i].setId(i+111);
                        textArray[i].setText(currency);
                        textArray[i].setTextSize(20);
                        textArray[i].setGravity(Gravity.CENTER);

                        sale[i] = new TextView(MainActivity.this);
                        sale[i].setId(i+111);
                        sale[i].setText(saleRate);
                        sale[i].setTextSize(20);
                        sale[i].setGravity(Gravity.CENTER);

                        pur[i] = new TextView(MainActivity.this);
                        pur[i].setId(i+111);
                        pur[i].setText(purchaseRate);
                        pur[i].setTextSize(20);
                        pur[i].setGravity(Gravity.CENTER);

                        tableRow[i].addView(textArray[i], 0);
                        tableRow[i].addView(pur[i], 1);
                        tableRow[i].addView(sale[i], 2);
                        tableRow[i].setBackgroundResource(R.drawable.row_border);

                        tableNBU.addView(tableRow[i], new TableLayout.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT)
                        );


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
