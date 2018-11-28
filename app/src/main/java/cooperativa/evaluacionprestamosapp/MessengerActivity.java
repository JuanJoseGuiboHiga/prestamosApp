package cooperativa.evaluacionprestamosapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.adroitandroid.chipcloud.FlowLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MessengerActivity extends Activity {
    RequestQueue requestQueue;
    String respuesta;
    String respuestaDialogflow;
    boolean solicitaFechaVen;
    boolean solicitaEstadoSol;
    private ChatView mChatView;
    final AIConfiguration configuration = new AIConfiguration("");
    AIDataService dataService = new AIDataService(configuration);
    AsyncTask<Object, Void, String> asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        solicitaFechaVen = false;
        solicitaEstadoSol = false;
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        //User name
        String myName = "JJ";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = "Bot";

        final ChatUser me = new ChatUser(myId, myName, myIcon);
        final ChatUser you = new ChatUser(yourId, yourName, yourIcon);
        mChatView = (ChatView) findViewById(R.id.chat_view);
        final ChipCloud chipCloud = (ChipCloud) findViewById(R.id.chip_cloud);
     //   chipCloud.setSelectTransitionMS(500);
        chipCloud.addChip("Empezar");
        chipCloud.setGravity(ChipCloud.Gravity.CENTER);
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                if(index == 0)
                {
                    Message message12 = new Message.Builder()
                            .setUser(you)
                            .setRight(false)
                            .setText("Bienvenido, que servicio desea?")
                            .hideIcon(true)
                            .build();
                    mChatView.send(message12);
                }

                chipCloud.setSelectTransitionMS(300);
                chipCloud.removeAllViews();
                chipCloud.addChip("Solicitar un prestamo");
                chipCloud.addChip("Agendar una reunion con un representante");
                chipCloud.addChip("Consultar estado de solicitud de préstamo");
                if(chipCloud.getChildAt(index).getTag().toString()=="Solicitar un prestamo")
                {
                    Message message4 = new Message.Builder()
                            .setUser(you)
                            .setRight(true)
                            .setText("Para solicitar un préstamo tiene que generar una solicitud de prestamos")
                            .hideIcon(true)
                            .build();
                    mChatView.send(message4);
                }
            }

            @Override
            public void chipDeselected(int index) {
            }
        });

        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);

        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sendDelay = (new Random().nextInt(4) + 1) * 1000;
                if(solicitaFechaVen == false && solicitaEstadoSol == false)
                {
                    Message message = new Message.Builder()
                            .setUser(me)
                            .setRight(true)
                            .setText(mChatView.getInputText())
                            .hideIcon(true)
                            .build();
                    mChatView.send(message);
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(900);
                      //  new Connection().execute();
                        asyncTask = new AsyncTask<Object, Void, String>() {

                            @Override
                            protected String doInBackground(Object... params) {

                                respuestaDialogflow = callDialog(mChatView.getInputText());
                                try
                                {
                                    TimeUnit.MILLISECONDS.sleep(900);
                                    sendToChat();
                                    TimeUnit.MILLISECONDS.sleep(900);
                                }catch(Exception e)
                                {

                                }
                                return "";
                            }
                        };
                        asyncTask.execute();

                        TimeUnit.MILLISECONDS.sleep(900);
                    }catch(Exception e)
                    {

                    }
                    final Message receivedMessage = new Message.Builder()
                            .setUser(you)
                            .setRight(false)
                            .hideIcon(true)
                            .setText(respuestaDialogflow)
                            .build();

                    try
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChatView.receive(receivedMessage);
                            }
                        }, sendDelay);
                    }catch(Exception e)
                    {
                        Log.e("error",e.toString());
                    }

                    if(respuestaDialogflow.contains("Por favor, ingrese su número de préstamo y su dni"))
                    {
                        solicitaFechaVen = true;
                    }

                    if(respuestaDialogflow.contains("Por favor para poder consultar su solicitud de prestamo ingrese su numero de dni y el número de solicitud"))
                    {
                        solicitaEstadoSol = true;
                    }
                }else if(solicitaFechaVen == true)
                {
                    Message messageFechaVenSended = new Message.Builder()
                            .setUser(me)
                            .setRight(true)
                            .setText(mChatView.getInputText())
                            .hideIcon(true)
                            .build();
                    mChatView.send(messageFechaVenSended);

                    Message messageFechaVen = new Message.Builder()
                            .setUser(you)
                            .setRight(false)
                            .setText("Su cuota vence el 25/11/2018")
                            .hideIcon(true)
                            .build();
                    mChatView.send(messageFechaVen);
                    solicitaFechaVen = false;
                }else
                {
                    Message messageEstadoSol = new Message.Builder()
                            .setUser(me)
                            .setRight(true)
                            .setText(mChatView.getInputText())
                            .hideIcon(true)
                            .build();
                    mChatView.send(messageEstadoSol);

                    Message messageEstadoSol2 = new Message.Builder()
                            .setUser(you)
                            .setRight(false)
                            .setText("Su solicitud se encuentra en estado: En Proceso")
                            .hideIcon(true)
                            .build();
                    mChatView.send(messageEstadoSol2);
                    solicitaEstadoSol = false;
                }




            }

        });

        }

  /*  private String callApi() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Prepare the Request
        String conversacion = userInput.getText().toString();
        String url = "http://192.168.43.136:8080/nlp/"+conversacion;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, //GET or POST
                url,
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
                                respuesta = student.getString("respuesta");

                             //   label.setText(respuesta);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() { //Listener ERROR

            @Override
            public void onErrorResponse(VolleyError error) {
               // userInput.setText(error.toString());
                Log.e("Error",error.toString());
            }
        });
        requestQueue.add(request);
        return respuesta;
    }*/

   public String callDialog(String conversacion)
    {
       String respuestas=null;
        try {
            AIRequest request = new AIRequest(conversacion);
            AIResponse response = dataService.request(request);

            String respuestaChat1 = null;
            if (response.getStatus().getCode() == 200) {
                respuestaChat1 =response.getResult().getFulfillment().getSpeech();
            }
            respuestas = respuestaChat1;
        } catch (Exception e) {
            Log.e("error",e.toString());
        }
        return respuestas;

    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            respuestaDialogflow = callDialog(mChatView.getInputText());
            try
            {
                TimeUnit.MILLISECONDS.sleep(900);
                sendToChat();
                TimeUnit.MILLISECONDS.sleep(900);
            }catch(Exception e)
            {

            }

            return null;
        }

    }

      private void sendToChat() {
          RequestQueue queue = Volley.newRequestQueue(this);
          StringRequest sr = new StringRequest(Request.Method.POST,"http://192.168.1.8:8089/chat", new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                    String msgerror = error.toString();
                    msgerror = "";
              }
          }){
              @Override
              protected Map<String,String> getParams(){
                  Map<String,String> params = new HashMap<String, String>();
                  params.put("msg",mChatView.getInputText());
                  params.put("chat",respuestaDialogflow);
                  return params;
              }

              @Override
              public Map<String, String> getHeaders() throws AuthFailureError {
                  Map<String,String> params = new HashMap<String, String>();
                  params.put("Content-Type","application/x-www-form-urlencoded");
                  return params;
              }
          };
          queue.add(sr);
   }


}

