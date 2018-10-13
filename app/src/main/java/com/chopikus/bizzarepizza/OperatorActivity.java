package com.chopikus.bizzarepizza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OperatorActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    static View.OnClickListener myOnClickListener;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;
        String chosen="";
        private MyOnClickListener(Context context)
        {
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            final TextView textViewId = v.findViewById(R.id.textViewId);
            TextView textViewName = v.findViewById(R.id.textViewName);
            TextView textViewPhone = v.findViewById(R.id.textViewPhone);
            String id = textViewId.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(textViewName.getText().toString());
            builder.setMessage("Номер телефона : " + textViewPhone.getText().toString());

            View linearlayout = getLayoutInflater().inflate(R.layout.order_change_dialog, null);

            final String[] arr = {"в обработке", "не определен", "выполнен", "отменен"};
            final String[] arr2 = {"processing", "uncertain", "completed", "cancelled"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arr);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) linearlayout.findViewById(R.id.spinner);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    chosen = arr2[position];
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            builder.setView(linearlayout);
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    class MyTask extends AsyncTask<Void, Void, Void> {
                        ProgressDialog dialog;
                        ArrayList<OrderModel> data = new ArrayList<>();
                        RecyclerView.Adapter adapter;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            dialog = ProgressDialog.show(context, "Загрузка...",
                                    "Загрузка меню. Пожалуйста, подождите", true);
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpResponse response;
                            try {
                                response = httpclient.execute(new HttpGet("http://bizzarepizza.xyz/operator/change_order_status.php?id="+textViewId.getText().toString()+"&status="+chosen));
                                StatusLine statusLine = response.getStatusLine();
                                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    response.getEntity().writeTo(out);

                                    out.close();
                                } else{
                                    //Closes the connection.
                                    response.getEntity().getContent().close();
                                    throw new IOException(statusLine.getReasonPhrase());
                                }
                            } catch (ClientProtocolException e) {
                            } catch (IOException e) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            super.onPostExecute(result);
                            dialog.cancel();
                            new OperatorActivity.MyTask().execute();;
                        }

                    }
                    new MyTask().execute();
                }
            });
            builder.show();
        }

    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        ArrayList<OrderModel> data = new ArrayList<>();
        RecyclerView.Adapter adapter;
        final Context context = OperatorActivity.this;
        final Bitmap completed_bitmap = drawableToBitmap(getDrawable(R.drawable.completed));
        final Bitmap processing_bitmap = drawableToBitmap(getDrawable(R.drawable.processing));
        final Bitmap uncertain_bitmap = drawableToBitmap(getDrawable(R.drawable.uncertain));
        final Bitmap canceled_bitmap = drawableToBitmap(getDrawable(R.drawable.canceled));
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Загрузка...",
                    "Загрузка меню. Пожалуйста, подождите", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            try {
                response = httpclient.execute(new HttpGet("http://bizzarepizza.xyz/operator/get_all_orders.php"));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    Log.i("resultBackground", out.toString());
                    String[] resArr = out.toString().split(";");
                    for (int i=0; i<resArr.length/6; i++)
                    {
                        Bitmap bitmap;
                        if (resArr[i*6+4].equals("processing"))
                            bitmap = processing_bitmap;
                        else
                        if (resArr[i*6+4].equals("completed"))
                            bitmap = completed_bitmap;
                        else
                        if (resArr[i*6+4].equals("uncertain"))
                            bitmap = uncertain_bitmap;
                        else
                            bitmap = canceled_bitmap;
                        String name = "Заказ №"+resArr[i*6+3]+"("+resArr[i*6]+" "+resArr[i*6+1]+")";
                        if (resArr[i*6+4].equals("cancelled"))
                        {
                            name =  "Заказ №"+resArr[i*6+3]+"(отменен)";
                        }
                        data.add(new OrderModel(name, Double.parseDouble(resArr[i*6+2]), Integer.parseInt(resArr[i*6+3]), bitmap, resArr[i*6+5]));
                    }
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.cancel();
            OperatorAdapter adapter = new OperatorAdapter(data);
            recyclerView.setAdapter(adapter);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);
        myOnClickListener = new MyOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new MyTask().execute();
    }
}
