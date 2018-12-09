package cooperativa.evaluacionprestamosapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import android.view.KeyEvent;
import android.view.Menu;

import javax.net.ssl.HttpsURLConnection;

public class MessengerActivity extends AppCompatActivity {
     RequestQueue requestQueue;
    String respuesta;
    String respuestaDialogflow;
    boolean solicitaFechaVen;
    boolean solicitaEstadoSol;
    boolean respuestaDefault;
    private ChatView mChatView;
    String userUID = null;
    final AIConfiguration configuration = new AIConfiguration("60d5fd9490c14a1a9f31fbf13814dbda");
    AIDataService dataService = new AIDataService(configuration);
    AsyncTask<Object, Void, String> asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        requestQueue = Volley.newRequestQueue(this);
        solicitaFechaVen = false;
        solicitaEstadoSol = false;
        respuestaDefault = false;

        int myId = 0;
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        String myName = "";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = "Bot";

        final ChatUser me = new ChatUser(myId, myName, myIcon);
        final ChatUser you = new ChatUser(yourId, yourName, yourIcon);
        mChatView = (ChatView) findViewById(R.id.chat_view);
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
                if (solicitaFechaVen == false && solicitaEstadoSol == false) {
                    sendUserMsg(me,mChatView.getInputText());
                    obtenerReespuesta(mChatView.getInputText(), new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {
                            evaluateConversation(result);
                            sendBotMsg(you,result);
                        }
                    });

                }else if(solicitaEstadoSol == true) {
                    sendUserMsg(me,mChatView.getInputText());
                    obtenerEstado(mChatView.getInputText(), new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {
                            evaluateConversation(result);
                            String msg = null;
                            if(result.equals("null"))
                            {
                                msg = "No existe la solicitud ingresada";
                            }else
                            {
                                msg = result;
                            }
                            sendBotMsg(you,msg);
                            solicitaEstadoSol = false;
                        }
                    });
                }else if(solicitaFechaVen == true) {
                    sendUserMsg(me,mChatView.getInputText());
                    obtenerFechaVencimiento(mChatView.getInputText(), new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {
                            evaluateConversation(result);
                            String msg = null;
                            if(result.equals("null"))
                            {
                                msg = "No se ha encontrado cuota relacionada a su documento de identidad";
                            }else
                            {
                                msg = "Su cuota vence el: "+result;
                            }
                            sendBotMsg(you,msg);
                            solicitaFechaVen = false;
                        }
                    });
                }


            }

        });

    }

    @Override
    public void  onBackPressed() {
        Intent i = new Intent(this, RatingActivity.class);
        String userUID = getIntent().getStringExtra("userUID");
        i.putExtra("userUID", userUID);
        startActivity(i);
    }

    private void obtenerReespuesta(String conversacion,final ServerCallback callback) {
        try {
            conversacion = URLEncoder.encode(conversacion, "UTF-8");
        } catch (Exception e) {

        }
        String url = "http://192.168.1.8:8080/nlp/" + conversacion;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            callback.onSuccess(response.get("respuesta").toString());
                        }catch(Exception e)
                        {

                        }

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

    }
    private void obtenerFechaVencimiento(String idCliente,final ServerCallback callback) {
        String url = "http://192.168.1.8:8080/fechaVencimiento/"+idCliente;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            callback.onSuccess(response.get("fechaVencimiento").toString());
                        }catch(Exception e)
                        {

                        }

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
    }
    private void obtenerEstado(String numeroSolicitud,final ServerCallback callback) {
        String url = "http://192.168.1.8:8080/estadoSolicitud/"+numeroSolicitud;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            callback.onSuccess(response.get("estado").toString());
                        }catch(Exception e)
                        {

                        }

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
    }
    private void registrarPreguntaSinRespuesta(String comentario) {
        final String userUID2= getIntent().getStringExtra("userUID");
        try
        {
            comentario = URLEncoder.encode(comentario,"UTF-8");
        }catch(Exception e)
        {

        }

        String url = "http://192.168.1.8:8080/conversacion/"+userUID2+"/"+comentario;
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
    }
    private void sendUserMsg(ChatUser me, String msg)
    {
        Message message = new Message.Builder()
                .setUser(me)
                .setRight(true)
                .setText(msg)
                .hideIcon(true)
                .build();
        mChatView.send(message);
    }

    private void sendBotMsg(ChatUser youactivity, String mensaje)
    {
        try
        {
            final String msg = mensaje;
            final ChatUser you = youactivity;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message messageEstadoSol2 = new Message.Builder()
                            .setUser(you)
                            .setRight(false)
                            .setText(msg)
                            .hideIcon(true)
                            .build();
                    mChatView.send(messageEstadoSol2);
                }
            }, 300);
        }catch(Exception e)
        {
            Log.e("error",e.toString());
        }
    }

    private void evaluateConversation(String msg)
    {
        if(msg.contains("Por favor, ingrese su numero de documento"))
        {
            solicitaFechaVen = true;
        }

        if(msg.contains("Por favor para poder consultar su solicitud de prestamo ingrese su codigo de solicitud"))
        {
            solicitaEstadoSol = true;
        }
        if(msg.contains("Disculpe, vuelva a ingresar su consulta. "))
        {
            respuestaDefault = true;
            registrarPreguntaSinRespuesta(mChatView.getInputText());
            respuestaDefault = false;
        }
    }
}

