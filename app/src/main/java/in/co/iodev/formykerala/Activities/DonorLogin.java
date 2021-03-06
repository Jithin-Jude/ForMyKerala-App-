package in.co.iodev.formykerala.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.iodev.formykerala.Controllers.CheckInternet;
import in.co.iodev.formykerala.Controllers.HTTPPostGet;
import in.co.iodev.formykerala.Controllers.ProgressBarHider;
import in.co.iodev.formykerala.Models.DataModel;
import in.co.iodev.formykerala.Controllers.OTPTextEditor;
import in.co.iodev.formykerala.Models.data;
import in.co.iodev.formykerala.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static in.co.iodev.formykerala.Constants.Constants.Donor_Login;
import static java.lang.Boolean.FALSE;

public class DonorLogin extends AppCompatActivity {
    EditText phone;
    Button submit;
    Gson gson = new Gson();
    SharedPreferences sharedPref;
    Boolean flag=true;
    ProgressBarHider hider;
    DataModel d;
    ImageView back;
    EditText otp1,otp2,otp3,otp4;
    TextView forgot,register;
    Context context;
    String StringData,StringData1,request_post_url=Donor_Login,TimeIndex;
    Spinner countryCodeSpinner;
    String countrycode[];
    String code;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }catch (Exception e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_donor_login);
        MainActivity.setAppLocale(MainActivity.languagePreferences.getString("LOCALE_CODE", null), getResources());
        phone=findViewById(R.id.phone);
        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        forgot=findViewById(R.id.forg);
        back=findViewById(R.id.back_button);
        otp1.addTextChangedListener(new OTPTextEditor(otp1,otp1.getRootView()));
        otp2.addTextChangedListener(new OTPTextEditor(otp2,otp2.getRootView()));
        otp3.addTextChangedListener(new OTPTextEditor(otp3,otp3.getRootView()));
        otp4.addTextChangedListener(new OTPTextEditor(otp4,otp4.getRootView()));
        sharedPref=getDefaultSharedPreferences(getApplicationContext());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        context=this;
        final String localeCode=MainActivity.languagePreferences.getString("LOCALE_CODE", null);
        final ImageView voice=findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice.setClickable(false);
                MediaPlayer mp = new MediaPlayer();

                try {
                    if(localeCode.equals("ml"))
                    { mp=MediaPlayer.create(getApplicationContext(),R.raw.login_mal);
                        mp.start();}
                    else if (localeCode.equals("en"))
                    {
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.login_eng);
                        mp.start();
                    }
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            voice.setClickable(true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        submit=findViewById(R.id.request_otp_button);
        hider=new ProgressBarHider(submit.getRootView(),submit);
        sharedPref=getDefaultSharedPreferences(getApplicationContext());
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
                /*Toast.makeText(getApplicationContext(),String.valueOf(i),Toast.LENGTH_SHORT).show();*/
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
        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonorLogin.this,DOTPVerification.class));
                DonorLogin.this.finish();
            }
        });  forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonorLogin.this,DForgotPin.class));
                DonorLogin.this.finish();
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
        if(otp1.getText().toString().equals("")||otp2.getText().toString().equals("")||otp3.getText().toString().equals("")||otp4.getText().toString().equals("")||phone.getText().toString().equals("")){
            String toastText = getString(R.string.toast_valid_ph_no);
            Toast.makeText(getApplicationContext(), toastText,Toast.LENGTH_LONG).show();
        }
        else {
         hider.show();
        StringData=code+phone.getText().toString();
        StringData1=otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString();

        d=new DataModel();
        d.setPhoneNumber(StringData);
        d.setPIN(StringData1);
        StringData=gson.toJson(d);
        Log.i("jisjoe",StringData);

        new HTTPAsyncTask2().execute(request_post_url);}
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DonorLogin.this,MainActivity.class));
        DonorLogin.this.finish();
        try {
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void forgot(View view) {
       startActivity(new Intent(DonorLogin.this,DForgotPin.class));

    }

    private class HTTPAsyncTask2 extends AsyncTask<String, Void, String> {
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
        hider.hide();
        JSONObject responseObject;
        try {
            responseObject = new JSONObject(response);
            Log.i("jisjoe",result.toString());
             Toast.makeText(getApplicationContext(),responseObject.getString("Message"),Toast.LENGTH_LONG).show();
           if(responseObject.getString("Message").equals("Success")) {
               SharedPreferences.Editor editor = sharedPref.edit();
               editor.putString("TimeIndex", responseObject.getString("TimeIndex"));
               editor.putString("PhoneNumber", d.getPhoneNumber());

               editor.apply();

               TimeIndex=sharedPref.getString("TimeIndex","");
               if(responseObject.getString("Details_Entered").equals("False"))
               {editor.putBoolean(TimeIndex+"FirstLogin",true);
                   editor.putBoolean(TimeIndex+"DEditedR", false);
               }
               else
               {   editor.putBoolean(TimeIndex+"FirstLogin",false);
                   editor.putBoolean(TimeIndex+"DEditedR", true);
               }
               editor.putBoolean(TimeIndex+"DLogin", true);
             //  editor.putBoolean(TimeIndex+"DEditedR", true);
               //editor.putBoolean(TimeIndex+"DEdited", true);
               editor.apply();
               if(sharedPref.getBoolean(TimeIndex+"DEditedR",FALSE)) {
                   Intent intent = new Intent(DonorLogin.this, DonorHomeActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   DonorLogin.this.finish();

               }

               else{
                   Intent intent = new Intent(DonorLogin.this, DonorDetails.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   DonorLogin.this.finish();


               }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }    }


}}

