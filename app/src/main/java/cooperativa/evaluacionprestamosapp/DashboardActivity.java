package cooperativa.evaluacionprestamosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    CardView mycard ;
    Intent i ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mycard = (CardView) findViewById(R.id.chatbot);
        i = new Intent(this,MessengerActivity.class);
        String userUID= getIntent().getStringExtra("userUID");
        i.putExtra("userUID", userUID);
        mycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

    }
}
