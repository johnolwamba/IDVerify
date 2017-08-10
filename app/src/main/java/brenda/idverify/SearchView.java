package brenda.idverify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import brenda.idverify.adapters.AlertDialogManager;
import brenda.idverify.adapters.ServiceHandler;
import brenda.idverify.adapters.Variables;

public class SearchView extends AppCompatActivity {
Button btnSearch;
    EditText txtSearch;

    String first_name = "";
    String last_name = "";
    String other_name = "";
  //  String id_number = "";
    String dob = "";
    String gender = "";

    //create sessions to store/retrieve selections
    SharedPreferences sharedpreferences;

    SharedPreferences.Editor editor;
    public static final String USERPREFERENCES = "UserDetails" ;

    ProgressDialog loading = null;
    private static Variables address = new Variables();
    // API urls
    private  static String URL_SEARCH_ID = "https://www.eresident.co.ke/api/oauth/user-lookup?";

    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        // Font path
        String fontPath = "myfonts/AdventPro-Regular.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
//
//        // Applying font
        txtSearch.setTypeface(tf);
        btnSearch.setTypeface(tf);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable()){
                    new searchID().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class searchID extends AsyncTask<Void, Void, Void> {
        String id_number = txtSearch.getText().toString().trim();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchView.this);
            pDialog.setMessage("checking...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_SEARCH_ID+"id_number="+id_number, ServiceHandler.POST,null);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json);
            //result = jsonStr;
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);

                    if (jsonObj != null) {

                            first_name = jsonObj.get("first_name").toString();
                            last_name = jsonObj.get("last_name").toString();
                            other_name = jsonObj.get("other_name").toString();
                            dob = jsonObj.get("dob").toString();
                            gender = jsonObj.get("gender").toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Bundle bundle = new Bundle();
                                    bundle.putString("name",first_name + " " + last_name + " " + other_name);
                                    bundle.putString("gender",gender);
                                    bundle.putString("dob",dob);
                                    Intent i = new Intent(SearchView.this,SearchResultsView.class);
                                    i.putExtras(bundle);
                                    startActivity(i);

                                    if (pDialog.isShowing())
                                        pDialog.dismiss();

                                }
                            });

                    }

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            alert.showAlertDialog(
                                    SearchView.this,
                                    "Sorry",
                                    "ID Number cannot be traced",
                                    false);
                            pDialog.dismiss();
                        }
                    });
                   // e.printStackTrace();
                }

            } else {
                //Log.e("JSON Data", "Didn't receive any data from server!");
                // Error in connection
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alert.showAlertDialog(
                                SearchView.this,
                                "Error",
                                "No internet connection",
                                false);
                        pDialog.dismiss();
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
            //add intent
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
