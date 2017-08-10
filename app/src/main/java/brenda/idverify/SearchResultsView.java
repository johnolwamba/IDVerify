package brenda.idverify;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SearchResultsView extends AppCompatActivity {
    TextView txtName,txtGender,txtDOB;
    String name = "";
    String gender = "";
    String dob = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_view);

        txtName = (TextView)findViewById(R.id.txtName);
        txtGender = (TextView)findViewById(R.id.txtGender);
        txtDOB = (TextView)findViewById(R.id.txtDOB);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            name = bundle.getString("name");
            gender = bundle.getString("gender");
            dob = bundle.getString("dob");

            txtName.setText(name);
            txtGender.setText(gender);
            txtDOB.setText(dob);
        }


    }



}
