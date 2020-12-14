package br.com.touchsoul.aguaegas.View;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.touchsoul.aguaegas.Model.User;
import br.com.touchsoul.aguaegas.Model.VolleyCallBack;
import br.com.touchsoul.aguaegas.R;

public class Login extends AppCompatActivity {
    private SignInButton btn_enterWithGoogle;
    private Button btn_closeApp;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0;

    private GoogleSignInAccount account;

    private JSONObject obj;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConfig();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }

    public void buttonConfig(){
        btn_enterWithGoogle = findViewById(R.id.sign_in_button);
        btn_closeApp = findViewById(R.id.main_btn_sairdoapp);

        btn_enterWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        btn_closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null){
                // Signed in successfully, show authenticated UI.
                if (isAlreadyRegistered(account, new VolleyCallBack() {

                            @Override
                            public void onSuccess() {
                                // this is where you will call the geofire, here you have the response from the volley.final VolleyCallBack callBack
                                if (user.getUsertype() == 1) {
                                    startActivity(new Intent(getApplicationContext(), ChooseService.class));
                                    finish();
                                } else if (user.getUsertype() == 2) {
                                    startActivity(new Intent(getApplicationContext(), ProviderMenu.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), SelectUser.class));
                                    finish();
                                }
                            }
                })){} else {
                    startActivity(new Intent(this, SelectUser.class));
                };
            }

            //startActivity(new Intent(getApplicationContext(), SelectUser.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Erro", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(Login.this, "Erro: "+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public boolean isAlreadyRegistered(GoogleSignInAccount account, final VolleyCallBack callBack){
        final boolean[] result = new boolean[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.129:3000/users?googleid=eq."+account.getId();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("[]")){
                            result[0] = false;
                        } else {
                            try {
                                obj = new JSONObject(response.substring(1, response.length()-1));
                                user = new User(
                                            obj.getString("googleid"),
                                            obj.getString("name"),
                                            obj.getString("email"
                                        ));
                                user.setUsertype(Integer.parseInt(obj.getString("usertype")));
                                result[0] = true;
                                callBack.onSuccess();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Login.this, "Erro: "+e,
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Erro: "+error.toString(),
                        Toast.LENGTH_LONG).show();
                result[0] = false;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result[0];
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Login.this, "Deslogado com sucesso!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}