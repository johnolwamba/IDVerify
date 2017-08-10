package brenda.idverify;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileView extends AppCompatActivity {

    TextView txtName,txtEmail,txtID;

    //create sessions to store/retrieve selections
    SharedPreferences sharedpreferences;

    SharedPreferences.Editor editor;
    public static final String USERPREFERENCES = "UserDetails" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        sharedpreferences = getSharedPreferences(USERPREFERENCES,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtID = (TextView) findViewById(R.id.txtID);

        txtName.setText(sharedpreferences.getString("name",null));
        txtEmail.setText(sharedpreferences.getString("email",null));
        txtID.setText(sharedpreferences.getString("id_number",null));
    }

}
