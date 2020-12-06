package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.touchsoul.aguaegas.R;

public class Login extends AppCompatActivity {
    private SignInButton btn_enterWithGoogle;
    private Button btn_closeApp;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0;

    private GoogleSignInAccount account;

    private JSONObject obj;
    private int usertype;

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
                if (isAlreadyRegistered(account)){
                    if (usertype == 1){
                        startActivity(new Intent(getApplicationContext(), ChooseService.class));
                    } else if (usertype == 2){
                        startActivity(new Intent(getApplicationContext(), ProviderMenu.class));
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), SelectUser.class));
                }
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

    public boolean isAlreadyRegistered(GoogleSignInAccount account){
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
                                obj = new JSONObject(response);
                                usertype = obj.getInt("usertype");
                                result[0] = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
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
}