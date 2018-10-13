package com.chopikus.bizzarepizza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MenuActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;

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

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context)
        {
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            TextView textViewId = v.findViewById(R.id.textViewId);
            TextView textViewName = v.findViewById(R.id.textViewName);
            //Toast.makeText(context, textView.getText(), Toast.LENGTH_SHORT).show();
            //context.startActivity(new Intent(context, PizzaActivity.class));
            Intent intent = new Intent(context, PizzaActivity.class);
            intent.putExtra("pizza_id", textViewId.getText().toString());
            intent.putExtra("pizza_name", textViewName.getText().toString());
            context.startActivity(intent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pizza, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.cart:
            {
                startActivity(new Intent(this, CartActivity.class));
            }
        }
        return true;
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    private String saveToInternalStorage(Bitmap bitmapImage, String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myOnClickListener = new MyOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();

        Bitmap carbonaraBitmap = drawableToBitmap(getDrawable(R.drawable.carbonara));
        Bitmap margaritaBitmap = drawableToBitmap(getDrawable(R.drawable.margarita));
        final Context context = this;
        class MyTask extends AsyncTask<Void, Void, Void> {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(context, "Загрузка...",
                        "Загрузка меню. Пожалуйста, подождите", true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SharedPreferences preferences = getSharedPreferences("pizzas", Context.MODE_PRIVATE);
                    SharedPreferences other_info = getSharedPreferences("other_info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor info_edit = other_info.edit();
                    SharedPreferences.Editor editor = preferences.edit();
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = null;
                    response = httpclient.execute(new HttpGet("http://bizzarepizza.xyz/get_products_list.php"));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String[] responseArr = out.toString().split(";");
                        Log.i("get_menu", out.toString());
                        Log.i("get_menu", responseArr.length/4+"");
                        Log.i("get_menu", responseArr.length+"");
                        for (int i=0; i<responseArr.length/4; i++)
                        {

                            String name = responseArr[i*4];
                            Double price = Double.parseDouble(responseArr[i*4+1]);
                            int id = Integer.parseInt(responseArr[i*4+2]);
                            Bitmap bitmap = getBitmapFromURL(responseArr[i*4+3]);
                            String s="";
                            if (bitmap!=null)
                            s = saveToInternalStorage(bitmap, responseArr[i*4+2]+".png");
                            //Toast.makeText(context, "NAME:"+s, Toast.LENGTH_SHORT).show();
                            info_edit.putString(responseArr[i*4+2], name);
                            info_edit.putFloat("price"+responseArr[i*4+2], price.floatValue());
                            Log.i("IMAGE_NAME", s);
                            if (bitmap!=null)
                            new ImageSaver(context).setFileName(responseArr[i*4+2]).setDirectoryName("images").save(bitmap);
                            data.add(new DataModel(name, price, id, bitmap));
                        }
                        editor.apply();
                        info_edit.apply();
                        out.close();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                }
                catch (Exception e)
                {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                dialog.cancel();

                adapter = new CustomAdapter(data);
                recyclerView.setAdapter(adapter);
            }

        }
        new MyTask().execute();

    }


}
