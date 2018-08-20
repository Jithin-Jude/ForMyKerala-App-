package in.co.iodev.formykerala.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import in.co.iodev.formykerala.Constants.Constants;
import in.co.iodev.formykerala.HTTPGet;
import in.co.iodev.formykerala.HTTPPost;
import in.co.iodev.formykerala.HTTPPostGet;
import in.co.iodev.formykerala.R;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ReceiverSelectRequirement extends AppCompatActivity {

SharedPreferences sharedPref;
String url= Constants.Get_Item_list;
String url2=Constants.Register_Case;
ArrayList Mainproducts,products;
ListView product_request_list;
String TimeIndex;
String StringData;
Product_Request_Adapter adapter;
Boolean submit=false;
Button submit_button;
ImageView search_button;
EditText item_search;
Map<String,String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_select_requirement);
        sharedPref=getDefaultSharedPreferences(getApplicationContext());

/*
        if(sharedPref.getBoolean("Login",FALSE))
        {
            Toast.makeText(getApplicationContext(),"LOGGED IN ALREADY--REDIRECT",Toast.LENGTH_LONG).show();
        }*/
        TimeIndex=sharedPref.getString("TimeIndex","");

       items=new HashMap<>();


        Toast.makeText(getApplicationContext(),StringData,Toast.LENGTH_LONG).show();
        product_request_list=findViewById(R.id.product_request_listview);
        adapter=new Product_Request_Adapter();
        new HTTPAsyncTask2().execute(url);
        submit_button=findViewById(R.id.submit_button);
        search_button=findViewById(R.id.search_button);
        item_search=findViewById(R.id.item_search);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject timeindex=new JSONObject();
                try {
                    timeindex.put("TimeIndex",TimeIndex);
                    timeindex.put("Items",items);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringData=timeindex.toString();
                submit=true;
                new HTTPAsyncTask2().execute(url);
            }
        });
       search_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               search();
           }
       });
    }

    private void search() {
        if(!item_search.getText().toString().equals(""))
        {products.clear();
        for (int i=0;i<Mainproducts.size();i++)
        {
            if(Mainproducts.get(i).equals(item_search.getText().toString()))
            {
                products.add(Mainproducts.get(i));

            }
        }}
        else {
            products.clear();
            products.addAll(Mainproducts);
        }
        product_request_list.setAdapter(adapter);
        Log.d("Items",items.toString()+" "+products.toString()+" "+Mainproducts.toString());

    }

    public void request(View view) {

    }

    public void view_items(View view) {
    }

   private class Product_Request_Adapter extends BaseAdapter {

        @Override
        public int getCount() {

            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder1 holder = null;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.product_select_list_item,parent,false);
                holder = new ViewHolder1(view);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder1) view.getTag();
            }


            try {
                final ViewHolder1 finalHolder = holder;
                holder.ProductName.setText(String.valueOf(products.get(position)));
                if(items.containsKey(holder.ProductName.getText().toString())) {
                   Log.d("Items",items.get(holder.ProductName.getText()));
                    holder.Quantity.setText(items.get(holder.ProductName.getText().toString()));
                    holder.selected.setChecked(true);

                }
                else {
                    holder.selected.setChecked(FALSE);
                    holder.Quantity.setText("");
                }
                holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked)
                        {
                            items.put(finalHolder.ProductName.getText().toString(),finalHolder.Quantity.getText().toString());

                        }
                        else
                        {
                            items.remove(finalHolder.ProductName.getText());
                        }

                    }
                });
                holder.Quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        finalHolder.selected.setChecked(FALSE);

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        finalHolder.selected.setChecked(FALSE);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }
                });

            }catch (Exception e){
            }



            return view;
        }
    }
    private class ViewHolder1 {
        TextView ProductName;
        CheckBox selected;
        EditText Quantity;



        public ViewHolder1(View v) {
            ProductName = (TextView) v.findViewById(R.id.product_name);
            selected=v.findViewById(R.id.check_product);
            Quantity=v.findViewById(R.id.edit_quantity);






        }
    }

    private class HTTPAsyncTask2 extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        String response=null;
              // params comes from the execute() call: params[0] is the url.
        try {
            try {

                if (!submit)
                    response= HTTPGet.getJsonResponse(urls[0]);
                else
                    response= HTTPPostGet.getJsonResponse(url2,StringData);
                Log.i("jisjoe",response.toString());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error!";
            }
        } catch (Exception e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        JSONObject responseObject= null;
        try {
            if (!submit)
            {JSONArray parentObject = new JSONObject(result).getJSONArray("Items");

            products = new ArrayList<String>();
            Mainproducts=new ArrayList<String>();
            if (parentObject!= null) {
                for (int i=0;i<parentObject.length();i++){
                    products.add(parentObject.getString(i));
                    Mainproducts.add(parentObject.getString(i));

                }
            }

            Log.d("Responseitem",products.toString());
            product_request_list.setAdapter(adapter);}
            else
            {Log.d("Responseitem",result);
            submit=false;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }    }


}
}