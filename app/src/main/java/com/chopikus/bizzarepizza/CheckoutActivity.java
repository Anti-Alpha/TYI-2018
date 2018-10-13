package com.chopikus.bizzarepizza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class CheckoutActivity extends AppCompatActivity {

    float total_price=0;
    String products="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        total_price = getIntent().getFloatExtra("allPrice",0 );
        products = getIntent().getStringExtra("products");
        Toast.makeText(this, products, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, String.valueOf(total_price), Toast.LENGTH_SHORT).show();
        TextView textView = (TextView) findViewById(R.id.textView);
        final EditText first_name = (EditText) findViewById(R.id.editText);
        final EditText last_name = (EditText) findViewById(R.id.editText2);
        final EditText address = (EditText) findViewById(R.id.editText3);
        final EditText phone = (EditText) findViewById(R.id.editText4);
        textView.setText("Общая стоимость : "+total_price+" грн.");
        Button checkout = (Button) findViewById(R.id.checkout);

        final Context context = this;
        class MyTask extends AsyncTask<Void, Void, Void> {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(context, "Загрузка...",
                        "Оформление заказа. Пожалуйста, подождите", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                HttpResponse response = null;
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
                    client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
                    HttpGet request = new HttpGet();
                    String str = "http://bizzarepizza.xyz/db/create_order.php?first_name="+URLEncoder.encode(first_name.getText().toString(), "UTF-8")+"&last_name="
                            +URLEncoder.encode(last_name.getText().toString(), "UTF-8")+"&address="
                            +URLEncoder.encode(address.getText().toString(), "UTF-8")+"&total="+URLEncoder.encode(String.valueOf(total_price), "UTF-8")
                            +"&phone="+URLEncoder.encode(phone.getText().toString(), "UTF-8")+"&products="+URLEncoder.encode(products, "UTF-8");

                    request = new HttpGet(str);
                    response = client.execute(request);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    Log.d("resultS", out.toString());

                } catch (Exception e)
                {
                    Log.e("EXCEPTION", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                dialog.cancel();
                /*SharedPreferences preferences = getSharedPreferences("other_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences.edit();
                SharedPreferences preferences1 = getSharedPreferences("pizzas", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.clear();
                editor.apply();
                editor1.apply();
                finishAffinity();*/
            }

        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyTask().execute();
            }
        });


    }
}
