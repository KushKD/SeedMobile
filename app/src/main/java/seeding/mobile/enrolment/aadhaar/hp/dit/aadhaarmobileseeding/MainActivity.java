package seeding.mobile.enrolment.aadhaar.hp.dit.aadhaarmobileseeding;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity
{

    private Boolean initailize = false;
    private EditText et_Aadhaar,et_Mobile, et_email;
    private Button bt_Submit;
    private String aadhaar_Send=null;
    private String email_Send=null;
    private String phone_Send=null;
    private String occupationsend=null;
    private String sendingmobilesend = null;
    URL url;
    HttpURLConnection conn;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initailize = InitializeUI();

        if(initailize){
            Toast.makeText(MainActivity.this, "Initialized", Toast.LENGTH_SHORT).show();

            /**
             * Button Submit Click
             */
            bt_Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //GetData
                    GetData();

                }
            });

        }else{
            Toast.makeText(MainActivity.this, "Something went really bad behind the white scree.", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetData() {
        aadhaar_Send = et_Aadhaar.getText().toString().trim();
        phone_Send = et_Mobile.getText().toString().trim();
        email_Send= et_email.getText().toString().trim();

        if(aadhaar_Send.length()!=0 && phone_Send.length()!=0){
            if(aadhaar_Send.length()==12){
                if(phone_Send.length()==10){


                    //Check weather the internet is there on not
                    if(isOnline()){
                        Toast.makeText(getBaseContext(),"Internet Available",Toast.LENGTH_SHORT).show();
                        //Create JSon and Start Async Task
                        SendData SD = new SendData();
                        SD.execute(aadhaar_Send,phone_Send,email_Send,occupationsend,sendingmobilesend);
                    }else{
                        Toast.makeText(getApplicationContext(),"Internet Not Available",Toast.LENGTH_SHORT).show();
                        StringBuilder SB = new StringBuilder();
                        SB.append("HP UID UPD");SB.append(" ");
                        SB.append(aadhaar_Send);SB.append(" ");
                        SB.append(phone_Send); SB.append(" ");
                        SB.append(email_Send); SB.append(" ");
                        String DATASEND = SB.toString().trim();
                        //Send an SMS Alert
                        ShowAlert(DATASEND);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter a valid Phone Number",Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(),"Please enter a valid Aadhaar Number",Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Please Enter your Aadhaar Number and Phone Number",Toast.LENGTH_LONG).show();
        }

    }

    private void ShowAlert(final String DataSend) {

        Log.d("SMS is ==========",DataSend);
        final Dialog dialog = new Dialog(MainActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_demo);
        dialog.setTitle("Send SMS");
        dialog.setCancelable(false);
        dialog.show();

        Button agree = (Button)dialog.findViewById(R.id.dialog_ok);
        Button disagree = (Button)dialog.findViewById(R.id.dialog_cancel);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Phone Number
                 * Message
                 */
                sendSMS("51969",DataSend);
                dialog.dismiss();
            }
        });

        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    /**
     * Initialize Methord
     * @return
     */

    private Boolean InitializeUI() {

        try {
            et_Aadhaar = (EditText) findViewById(R.id.etaadhaar);
            et_Mobile = (EditText) findViewById(R.id.etphone);
            et_email = (EditText) findViewById(R.id.etemail);
            bt_Submit = (Button) findViewById(R.id.submit);

            return true;
        }catch(Exception e){
            return false;

        }

    }

    /**
     * IS Online Check weather Internet is there or Not
     */
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    class SendData extends AsyncTask<String,String,String>{

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Sending Data to Server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... param) {

            try {
                url=new URL("http://10.241.9.72/eservice/mobileseed");
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONStringer userJson = new JSONStringer()
                        .object().key("Mobile_Seed")
                        .object()
                        .key("AadhaarNo").value(param[0])
                        .key("MobileNo").value(param[1])
                        .key("Email").value(param[2])
                        .key("Occupation").value(param[3])
                        .key("SendingMobileNo").value(param[4])
                        .endObject()
                        .endObject();

                   //Testing to be done

                System.out.println(userJson.toString());
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(userJson.toString());
                out.close();

                int HttpResult =conn.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();


                }else{
                    System.out.println(conn.getResponseMessage());
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally{
                if(conn!=null)
                    conn.disconnect();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

           /* JsonParser JP = new JsonParser();
            String finalResult = JP.POST(result);

            if(finalResult.equals(Constants.DATASENT)){
                clearData();
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,Home.class);
                startActivity(i);
                MainActivity.this.finish();
            }
            else{
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), finalResult, Toast.LENGTH_SHORT).show();
            }*/
        }
    }
}
