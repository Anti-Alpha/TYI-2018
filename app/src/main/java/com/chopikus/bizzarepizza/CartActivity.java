package com.chopikus.bizzarepizza;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    static View.OnClickListener myOnClickListener;
    SharedPreferences count_pizzas;
    LayoutInflater inflater;
    RecyclerView recyclerView;
    Bitmap loadImageFromStorage(String path)
    {
        try {
            File f=new File(path);
            Log.i("IMAGE_PATH", path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

    }

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

        private MyOnClickListener(Context context)
        {
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final NumberPicker numberPicker = new NumberPicker(context);
            final SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
            final TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
            final TextView textViewId = (TextView) v.findViewById(R.id.textViewId);
            numberPicker.setMaxValue(10);
            numberPicker.setMinValue(0);
            builder.setView(numberPicker);
            builder.setTitle(textViewName.getText().toString());
            builder.setMessage("Изменить число заказанных пицц..");
            final SharedPreferences preferences = getSharedPreferences("pizzas", Context.MODE_PRIVATE);
            final SharedPreferences other_info = getSharedPreferences("other_info", Context.MODE_PRIVATE);
            final String id = textViewId.getText().toString();
            builder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (numberPicker.getValue()==0)
                        editor.remove(id);
                    else
                        editor.putInt(id, numberPicker.getValue());
                    editor.apply();
                    final ArrayList<CartModel> data = new ArrayList<>();
                    Map<String, ?> allEntries = preferences.getAll();
                    Bitmap carbonaraBitmap = drawableToBitmap(getDrawable(R.drawable.carbonara));
                    String products="";
                    SharedPreferences.Editor edittor = other_info.edit();
                    edittor.putFloat("allPrice", 0);
                    edittor.apply();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        if (!entry.getKey().startsWith("bitmap")){
                            Bitmap bitmap = new ImageSaver(context).setFileName(entry.getKey()).setDirectoryName("images").load();
                            data.add(new CartModel(other_info.getString(entry.getKey(), ""), Integer.parseInt(entry.getValue().toString()), Integer.parseInt(entry.getKey()), bitmap));

                            products+=entry.getKey()+";"+Integer.parseInt(entry.getValue().toString())+";";

                            edittor.putFloat("allPrice", other_info.getFloat("allPrice", 0)+other_info.getFloat("price"+entry.getKey(), 0)*Integer.parseInt(entry.getValue().toString()));
                            edittor.putString("products", products);

                        }
                    }
                    edittor.apply();
                    final CartAdapter adapter = new CartAdapter(data);
                    recyclerView.setAdapter(adapter);

                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        inflater = getLayoutInflater();
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Button button = (Button) findViewById(R.id.buy);
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CheckoutActivity.class);
                //intent.putExtra("pizza_id", );
                //intent.putExtra("pizza_name", );
                SharedPreferences sharedPreferences = getSharedPreferences("other_info", Context.MODE_PRIVATE);
                intent.putExtra("allPrice", sharedPreferences.getFloat("allPrice", 0));
                intent.putExtra("products", sharedPreferences.getString("products", ";"));
                startActivity(intent);
            }
        });
        myOnClickListener = new CartActivity.MyOnClickListener(this);
        setTitle("Корзина");
        recyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Bitmap carbonaraBitmap = drawableToBitmap(getDrawable(R.drawable.carbonara));
        Bitmap margaritaBitmap = drawableToBitmap(getDrawable(R.drawable.margarita));
       // data.add(new DataModel("Карбонара", 144.99, 0, carbonaraBitmap));
       // data.add(new DataModel("Маргарита", 199.99, 1, margaritaBitmap));
        SharedPreferences preferences = getSharedPreferences("pizzas", Context.MODE_PRIVATE);

        final ArrayList<CartModel> data = new ArrayList<>();
        Map<String, ?> allEntries = preferences.getAll();
        final SharedPreferences other_info = getSharedPreferences("other_info", Context.MODE_PRIVATE);
        String products="";
        SharedPreferences.Editor editor = other_info.edit();
        editor.putFloat("allPrice", 0);
        editor.apply();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (!entry.getKey().startsWith("bitmap")) {
                String s = preferences.getString("bitmap" + entry.getKey(), "");
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                Bitmap bitmap = new ImageSaver(CartActivity.this).setFileName(entry.getKey()).setDirectoryName("images").load();
                data.add(new CartModel(other_info.getString(entry.getKey(), ""), Integer.parseInt(entry.getValue().toString()), Integer.parseInt(entry.getKey()), bitmap));

                products+=entry.getKey()+";"+Integer.parseInt(entry.getValue().toString())+";";
                editor.putFloat("allPrice", other_info.getFloat("allPrice", 0)+other_info.getFloat("price"+entry.getKey(), 0)*Integer.parseInt(entry.getValue().toString()));
                editor.putString("products", products);
                editor.apply();
            }
        }

        final CartAdapter adapter = new CartAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    public void checkout()
    {

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
