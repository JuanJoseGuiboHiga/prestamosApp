package cooperativa.evaluacionprestamosapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public TextView label;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callApi();
        setContentView(R.layout.activity_main);
        label = (TextView)findViewById(R.id.label);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void callApi() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Prepare the Request
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, //GET or POST
                "http://192.168.1.13:8080/country",
                null, //Parameters
                new Response.Listener<JSONArray>() { //Listener OK

                    @Override
                    public void onResponse(JSONArray responsePlaces) {
                           // label.setText(responsePlaces.);
                        // Process the JSON
                        try{
                            // Loop through the array elements
                            for(int i=0;i<responsePlaces.length();i++){
                                // Get current json object
                                JSONObject student = responsePlaces.getJSONObject(i);

                                // Get the current student (json object) data
                                String name = student.getString("name");
                                String descripion = student.getString("descripion");

                                label.setText(name);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() { //Listener ERROR

            @Override
            public void onErrorResponse(VolleyError error) {
                label.setText(error.toString());
            }
        });

//Send the request to the requestQueue
        requestQueue.add(request);
    }
}
