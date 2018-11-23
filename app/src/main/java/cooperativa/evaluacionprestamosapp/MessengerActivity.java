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

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.adroitandroid.chipcloud.FlowLayout;
import com.android.volley.RequestQueue;
import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;

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
    private ChatView mChatView;
    final AIConfiguration configuration = new AIConfiguration("60d5fd9490c14a1a9f31fbf13814dbda");
    AIDataService dataService = new AIDataService(configuration);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
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
        chipCloud.addChip("Informacion General");
        chipCloud.addChip("Como solicitar un prestamo?");
        chipCloud.addChip("Donde nos puede ubicar?");
        chipCloud.addChip("Agendar una reunion con un representante");
        chipCloud.setGravity(ChipCloud.Gravity.CENTER);
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                Message message12 = new Message.Builder()
                        .setUser(me)
                        .setRight(false)
                        .setText("Usted selecciono:"+index)
                        .hideIcon(false)
                        .build();
                mChatView.send(message12);
                chipCloud.removeAllViews();
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
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRight(true)
                        .setText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                mChatView.send(message);
                int sendDelay = (new Random().nextInt(4) + 1) * 1000;
                try
                {
                   TimeUnit.MILLISECONDS.sleep(900);
                   new Connection().execute();
                   TimeUnit.MILLISECONDS.sleep(900);

                }catch(Exception e)
                {

                }
                final Message receivedMessage = new Message.Builder()
                        .setUser(you)
                        .setRight(false)
                        .setText(respuestaDialogflow)
                        .build();

                try
                {
                    //TimeUnit.MILLISECONDS.sleep(1000);
                    //mChatView.receive(receivedMessage);

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

            }
                //Receive message


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
            return null;
        }

    }

}

