package com.chopikus.bizzarepizza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PizzaActivity extends AppCompatActivity {
    String pizza_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        pizza_id = getIntent().getStringExtra("pizza_id");
        final String pizza_name = getIntent().getStringExtra("pizza_name");
        final SharedPreferences sharedPreferences = getSharedPreferences("pizzas", Context.MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(pizza_id, sharedPreferences.getInt(pizza_id, 0)+1);
                editor.apply();
                Snackbar.make(view, "Продукт был добавлен в корзину", Snackbar.LENGTH_LONG)
                        .setAction("Отмена", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (sharedPreferences.getInt(pizza_id, 0)==1)
                                {
                                    editor.remove(pizza_id);
                                }
                                else
                                    editor.putInt(pizza_id, sharedPreferences.getInt(pizza_id, 0)-1);
                                editor.apply();
                            }
                        }).show();

            }
        });

        class MyTask extends AsyncTask<Void, Void, Void> {
            Toolbar toolbar;
            TextView longTextView;
            String pizza_desc="";
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle("");
                setSupportActionBar(toolbar);
                longTextView = (TextView) findViewById(R.id.longTextView);
                dialog = ProgressDialog.show(PizzaActivity.this, "", "Загрузка...");
                if (getSupportActionBar()!=null)
                {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }

            }

            @Override
            protected Void doInBackground(Void... params) {
                //http://bizzarepizza.xyz/drivers/get_product_desc.php?id=87

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = null;
                    response = httpclient.execute(new HttpGet("http://bizzarepizza.xyz/get_product_desc.php?id="+pizza_id));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        pizza_desc = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                }
                catch (Exception e)
                {}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                toolbar.setTitle(pizza_name);

                longTextView.setText(pizza_desc);
                dialog.cancel();
            }
        }
        MyTask task = new MyTask();
        task.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
