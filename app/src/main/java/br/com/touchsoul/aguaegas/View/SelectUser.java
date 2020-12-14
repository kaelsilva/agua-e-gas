package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.touchsoul.aguaegas.R;

public class SelectUser extends AppCompatActivity {
    private ImageView backBtn, userBtn, providerBtn;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_user_type);

        buttonConfig();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SelectUser.this, "Deslogado com sucesso!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void buttonConfig(){
        backBtn = findViewById(R.id.btn_back);
        userBtn = findViewById(R.id.iv_choose_user);
        providerBtn = findViewById(R.id.iv_choose_provider);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // ...
                    case R.id.btn_back:
                        signOut();
                        break;
                    // ...
                }
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(SelectUser.this, "Cliente criado.",
                            Toast.LENGTH_SHORT).show();
                    register(new JSONObject("{\"googleid\": \""+acct.getId()+"\"," +
                                            "\"name\": \""+acct.getDisplayName()+"\"," +
                                            "\"email\": \""+acct.getEmail()+"\"," +
                                            "\"usertype\": \"1\"," +
                                            "\"latitude\": \"0\"," +
                                            "\"longitude\": \"0\"" +
                                            "}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectUser.this, "Erro no POST: "+e.toString(),
                            Toast.LENGTH_LONG).show();
                }
                finish();
                startActivity(new Intent(getApplicationContext(), ChooseService.class));
            }
        });

        providerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(SelectUser.this, "Fornecedor criado.",
                            Toast.LENGTH_SHORT).show();
                    register(new JSONObject("{\"googleid\": \""+acct.getId()+"\"," +
                            "\"name\": \""+acct.getDisplayName()+"\"," +
                            "\"email\": \""+acct.getEmail()+"\"," +
                            "\"usertype\": \"2\"," +
                            "\"latitude\": \"0\"," +
                            "\"longitude\": \"0\"" +
                            "}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SelectUser.this, "Erro no POST: "+e.toString(),
                            Toast.LENGTH_LONG).show();
                }
                finish();
                startActivity(new Intent(getApplicationContext(), ProviderMenu.class));
            }
        });
    }

    public void register(final JSONObject jsonObj) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.129:3000/users";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("googleid", jsonObj.getString("googleid"));
        params.put("name", jsonObj.getString("name"));
        params.put("email", jsonObj.getString("email"));
        params.put("usertype", jsonObj.getString("usertype"));
        params.put("latitude", jsonObj.getString("latitude"));
        params.put("longitude", jsonObj.getString("longitude"));

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // your response
                        Toast.makeText(SelectUser.this, "Usuário cadastrado com sucesso.",
                                Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //Toast.makeText(SelectUser.this, "Erro em register(): "+error.toString(),
                //        Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
        //requestQueue.start();
    }
}
