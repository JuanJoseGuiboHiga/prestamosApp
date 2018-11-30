package cooperativa.evaluacionprestamosapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class RegisterActivity extends Activity {
    EditText emailtxt,passwordtxt;
    Button registerButton, loginButton;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailtxt = (EditText) findViewById(R.id.editEmail);
        passwordtxt = (EditText) findViewById(R.id.editPassword);
        registerButton = (Button) findViewById(R.id.buttonRegister);
        loginButton = (Button) findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtxt.getText().toString();
                String password = passwordtxt.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Por favor, ingrese su correo",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Por favor ingrese su contraseña",Toast.LENGTH_SHORT).show();
                }

                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"La contraseña debe ser de al menos 6 digitos",Toast.LENGTH_SHORT).show();
                }
                progressDialog.setMessage("Realizando registro en linea...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try
                                {
                                    if(task.isSuccessful()){
                                        finish();
                                        Intent intencion = new Intent(getApplication(), DashboardActivity.class);
                                        String usrUID = firebaseAuth.getUid();
                                        intencion.putExtra("userUID",usrUID);
                                        startActivity(intencion);
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"El correo o la contraseña son incorrectos",Toast.LENGTH_SHORT).show();
                                    }
                                }catch(Exception e)
                                {
                                    Log.e("error",e.toString());
                                }

                            }
                        });
            }
        });

    loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Realizando consulta en linea...");
                progressDialog.show();
                final String email = emailtxt.getText().toString().trim();
                String password = passwordtxt.getText().toString().trim();

                //loguear usuario
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //checking if success
                                if (task.isSuccessful()) {
                                    int pos = email.indexOf("@");
                                    String user = email.substring(0, pos);
                                    Toast.makeText(RegisterActivity.this, "Bienvenido: " + emailtxt.getText(), Toast.LENGTH_LONG).show();
                                    Intent intencion = new Intent(getApplication(), DashboardActivity.class);
                                    String usrUID = firebaseAuth.getUid();
                                    intencion.putExtra("userUID",usrUID);
                                    startActivity(intencion);


                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                        Toast.makeText(RegisterActivity.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                                    }
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });

    }
}