package brenda.idverify;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import brenda.idverify.adapters.AlertDialogManager;
import brenda.idverify.adapters.ServiceHandler;
import brenda.idverify.adapters.Variables;

public class StudentView extends AppCompatActivity {
    TextView txtName,txtID,txtStatus;
    Button btnBlock;

    String name = "";
    String student_status = "";
    String registration_date = "";
    String email = "";
    String id_number = "";
    String reasons = "";

    //create sessions to store/retrieve selections
    SharedPreferences sharedpreferences;

    SharedPreferences.Editor editor;
    public static final String USERPREFERENCES = "UserDetails" ;

    ProgressDialog loading = null;
    private static Variables address = new Variables();
    // API urls
    private  static String URL_BLOCK_STUDENT = address.getAddress() + "/blockUser";

    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view);

        sharedpreferences = getSharedPreferences(USERPREFERENCES,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        txtName = (TextView)findViewById(R.id.txtName);
        txtID = (TextView)findViewById(R.id.txtID);
        txtStatus = (TextView)findViewById(R.id.txtStatus);
        btnBlock = (Button) findViewById(R.id.btnBlock);



        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            name = bundle.getString("name");
            student_status = bundle.getString("student_status");
            registration_date = bundle.getString("registration_date");
            email = bundle.getString("email");
            id_number = bundle.getString("id_number");

            if(student_status == "1"){
                txtStatus.setText("ALLOWED");
            }else{
                txtStatus.setText("BLOCKED");
            }

            txtName.setText(name);
            txtID.setText(id_number);
        }

        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockDialog();
            }
        });
    }


    @Override
    public void onBackPressed() {

    }


    public void blockDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(StudentView.this);
        dialog.setContentView(R.layout.block_dialog);
        dialog.setTitle("Block "+ name);

        final EditText txtReason = (EditText) dialog.findViewById(R.id.txtReason);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        // if button is clicked, close the custom dialog
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtReason.getText().toString().trim().length() > 0 ){
                    reasons = txtReason.getText().toString().trim();
                    new blockUser().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Please input the reason for blocking",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class blockUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StudentView.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_number", id_number));
            params.add(new BasicNameValuePair("description", reasons));
            params.add(new BasicNameValuePair("api_token", sharedpreferences.getString("token",null)));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_BLOCK_STUDENT, ServiceHandler.GET, params);

            //shows the response that we got from the http request on the logcat
            Log.d("my_Response: ", "> " + json + "URL:" + URL_BLOCK_STUDENT);

            if(json != null){
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        String status = jsonObj.getString("status");
                        if(status.equals("success")){
                            final String message = jsonObj.getString("message");
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(StudentView.this, HomeView.class);
                                    startActivity(i);
                                    StudentView.this.finish();
                                }
                            });
                        }else{

                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"User Blocking failed. Please try again.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
//                        // Existing data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert.showAlertDialog(
                                        StudentView.this,
                                        "Failed",
                                        "Failed",
                                        false);
                                pDialog.dismiss();

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                // Error in connection
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alert.showAlertDialog(
                                StudentView.this,
                                "Error",
                                "No internet connection",
                                false);
                        pDialog.dismiss();
                        alert.notify();
                    }
                });

            }
            return null;
        }

        protected void onPostExecute(Void result) {
            // dismiss the dialog once done
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }


    // Private class isNetworkAvailable
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
