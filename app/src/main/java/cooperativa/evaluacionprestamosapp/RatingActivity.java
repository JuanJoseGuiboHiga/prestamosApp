package cooperativa.evaluacionprestamosapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class RatingActivity  extends AppCompatActivity {
    Button button;
    RatingBar ratingBar;
    EditText commentTxt;
    String idUsuario;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        String userUID= getIntent().getStringExtra("userUID");
        button = (Button)findViewById(R.id.submitRateBtn);
        ratingBar = (RatingBar)findViewById(R.id.ratingsBar);
        commentTxt = (EditText)findViewById(R.id.reviewED);
        idUsuario = userUID;
        progressDialog = new ProgressDialog(this);
       final RequestQueue requestQueue  = Volley.newRequestQueue(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comentario = commentTxt.getText().toString();
                try
                {
                    comentario = URLEncoder.encode(comentario,"UTF-8");
                }catch(Exception e)
                {

                }

                String rating=String.valueOf(ratingBar.getRating());
                String url = "http://192.168.1.8:8080/sql/"+idUsuario+"/"+rating+"/"+comentario;
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Log.d("Response", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );

                requestQueue.add(getRequest);
                progressDialog.setMessage("Enviando calificación");
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                    }
                }, 3000); // 3000 milliseconds delay


            }
        });

    }


}
