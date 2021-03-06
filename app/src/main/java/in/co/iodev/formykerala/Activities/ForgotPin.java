package in.co.iodev.formykerala.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.iodev.formykerala.Controllers.CheckInternet;
import in.co.iodev.formykerala.Controllers.HTTPPostGet;
import in.co.iodev.formykerala.Controllers.ProgressBarHider;
import in.co.iodev.formykerala.Models.DataModel;
import in.co.iodev.formykerala.Models.data;
import in.co.iodev.formykerala.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static in.co.iodev.formykerala.Constants.Constants.Forgot_PIN_Generate;
import static in.co.iodev.formykerala.Constants.Constants.Generate_OTP_Forget;
import static in.co.iodev.formykerala.Constants.Constants.Resend_OTP;

public class ForgotPin extends AppCompatActivity {
    EditText phone;
    Button submit;
    Gson gson = new Gson();
    SharedPreferences sharedPref;
    Boolean flag=true;
    ProgressBarHider hider;

    DataModel d;
    ImageView back;
    Context context;
    Spinner countryCodeSpinner;
    String countrycode[];
    String code;
    ArrayAdapter adapter;
    String StringData,request_post_url=Forgot_PIN_Generate,request_post_url1=Generate_OTP_Forget,TimeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setAppLocale(MainActivity.languagePreferences.getString("LOCALE_CODE", null), getResources());
        setContentView(R.layout.activity_otpverification);
        phone=findViewById(R.id.phone);
        submit=findViewById(R.id.request_otp_button);
        sharedPref=getDefaultSharedPreferences(getApplicationContext());
        back=findViewById(R.id.back_button);
        context=this;
        hider=new ProgressBarHider(submit.getRootView(),submit);
        countryCodeSpinner=findViewById(R.id.countrycode);
        adapter= new ArrayAdapter<String>(this,
                R.layout.spinner_layout, data.countryNames);
        adapter.setDropDownViewResource(R.layout.drop_down_tems);
        countryCodeSpinner.setAdapter(adapter);
        countryCodeSpinner.setSelection(79);
        countrycode= data.countryAreaCodes;
        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                code="+"+countrycode[i];
                //Toast.makeText(getApplicationContext(),code,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void verify() {
        hider.show();

        if(phone.getText().toString().equals(""))
        {   hider.hide();
            String toastText = getString(R.string.toast_valid_ph_no);
            Toast.makeText(getApplicationContext(), toastText,Toast.LENGTH_LONG).show();
        }
        else{
            StringData=code+phone.getText().toString();
        d=new DataModel();
        d.setPhoneNumber(StringData);
        StringData=gson.toJson(d);
        Log.i("jisjoe",StringData);

        new HTTPAsyncTask2().execute(request_post_url);}



    }

    private class HTTPAsyncTask2 extends AsyncTask<String, Void, String> {
        String response="Network error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    response= HTTPPostGet.getJsonResponse(urls[0],StringData);
                    Log.i("jisjoe",response.toString());
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error!";
                }
                finally {
                    hider.hide();
                }
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }
        @Override
        protected void onPreExecute() {
            CheckInternet CI=new CheckInternet();
            CI.isOnline(context);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
             JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(),responseObject.getString("Message"),Toast.LENGTH_LONG).show();
                if(responseObject.getString("Message").equals("Success")){
                    new HTTPAsyncTask3().execute(request_post_url1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }private class HTTPAsyncTask3 extends AsyncTask<String, Void, String> {
        String response="Network Error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    response= HTTPPostGet.getJsonResponse(urls[0],StringData);
                    Log.i("jisjoe",response.toString());
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error!";
                }finally {
                    hider.hide();
                }
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPreExecute() {
            CheckInternet CI=new CheckInternet();
            CI.isOnline(context);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            hider.hide();
            JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                Toast.makeText(getApplicationContext(),responseObject.getString("Message"),Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("TimeIndex", responseObject.getString("TimeIndex"));
                editor.putString("PhoneNumber", d.getPhoneNumber());
                editor.apply();
                startActivity(new Intent(ForgotPin.this,ForgotPinOTPValidation.class));


            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPin.this,ReceiverLogin.class));
        ForgotPin.this.finish();
        super.onBackPressed();
    }
}

