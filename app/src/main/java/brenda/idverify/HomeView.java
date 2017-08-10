package brenda.idverify;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import brenda.idverify.adapters.AlertDialogManager;
import brenda.idverify.adapters.ServiceHandler;
import brenda.idverify.adapters.Variables;

public class HomeView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout btnScan,btnSearch;
    String id_number = "";

    //create sessions to store/retrieve selections
    SharedPreferences sharedpreferences;

    SharedPreferences.Editor editor;
    public static final String USERPREFERENCES = "UserDetails" ;

    ProgressDialog loading = null;
    private static Variables address = new Variables();
    // API urls
    private static String URL_LOGOUT = address.getAddress() + "/logout";
    private  static String URL_CHECK_STUDENT = address.getAddress() + "/scanUser";

    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sharedpreferences = getSharedPreferences(USERPREFERENCES,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        btnScan = (LinearLayout)findViewById(R.id.btnScan);
        btnSearch = (LinearLayout)findViewById(R.id.btnSearch);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeView.this, SearchView.class);
                startActivity(i);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_blocked) {
            Intent i =new Intent(HomeView.this, BlockedView.class);
            startActivity(i);

        } else if (id == R.id.nav_profile) {
            Intent i =new Intent(HomeView.this, ProfileView.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            //logout
            if(sharedpreferences.contains("token")){

                if(isNetworkAvailable()){
                    new logout().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }else{
                Intent i = new Intent(HomeView.this,SignInView.class);
                startActivity(i);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    /**
     * Async task class to get json by making HTTP call
     * */
    private class logout extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeView.this);
            pDialog.setMessage("signing out...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("api_token", sharedpreferences.getString("token",null)));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_LOGOUT, ServiceHandler.POST,params);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json + sharedpreferences.getString("token",null));
            //result = jsonStr;
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                                 runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    editor.remove("token");
                                    editor.remove("name");
                                    editor.remove("email");
                                    editor.remove("id_number");
                                    editor.commit();
                                    Intent i = new Intent(HomeView.this,SignInView.class);
                                    startActivity(i);
                                    HomeView.this.finish();
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
                                HomeView.this,
                                "Failed",
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



    //product qr code mode
    public void scanQR() {
        if (ContextCompat.checkSelfPermission(HomeView.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeView.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            new IntentIntegrator(HomeView.this).initiateScan();
        }
    }



    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (data != null) {
                if(data.getStringExtra("SCAN_RESULT") != null){

                    //get the extras that are returned from the intent
                    id_number = data.getStringExtra("SCAN_RESULT");
                    String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                    //Toast.makeText(getApplicationContext(),id_number,Toast.LENGTH_SHORT).show();

                    if(id_number.length() < 1){
                        Toast.makeText(getApplicationContext(),"Please scan a valid facility code",Toast.LENGTH_SHORT).show();
                    }else if(!isNetworkAvailable()){
                        Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    }else{
                        new verifyStudent().execute();
                    }

                }
            } else if (resultCode == RESULT_CANCELED) {

                // Handle cancel
            }
            //}
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    /**
     * Async task class to get json by making HTTP call
     * */
    private class verifyStudent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeView.this);
            pDialog.setMessage("checking...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_number", id_number));
            params.add(new BasicNameValuePair("api_token", sharedpreferences.getString("token",null)));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_CHECK_STUDENT, ServiceHandler.GET,params);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json + id_number + sharedpreferences.getString("token",null));
            //result = jsonStr;
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    String token = null;
                    if (jsonObj != null) {
                        String status = jsonObj.get("status").toString();
                        if (status.equals("success")) {
                            // Existing data
                            final JSONObject studentObj = jsonObj.getJSONObject("student");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String name = studentObj.getString("name");
                                        String student_status = studentObj.getString("status");
                                        String registration_date = studentObj.getString("created_at");
                                        String email = studentObj.getString("email");

                                        Bundle bundle = new Bundle();
                                        bundle.putString("name",name);
                                        bundle.putString("student_status",student_status);
                                        bundle.putString("registration_date",registration_date);
                                        bundle.putString("email",email);
                                        bundle.putString("id_number",id_number);
                                        Intent i = new Intent(HomeView.this,StudentView.class);
                                        i.putExtras(bundle);
                                        startActivity(i);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();

                                }
                            });



                        }
                        else if(status.equals("error")){
                            final String message = jsonObj.get("message").toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            HomeView.this,
                                            "Failed",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                }
                            });
                        }

                        else {
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            HomeView.this,
                                            "Failed",
                                            "Invalid licence number provided",
                                            false);
                                    pDialog.dismiss();
                                }
                            });


                        }
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
                                HomeView.this,
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





}
