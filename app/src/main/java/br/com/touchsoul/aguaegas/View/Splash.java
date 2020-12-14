package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.touchsoul.aguaegas.Model.User;
import br.com.touchsoul.aguaegas.Model.VolleyCallBack;
import br.com.touchsoul.aguaegas.R;

public class Splash extends AppCompatActivity implements Runnable {

    private GoogleSignInAccount account;
    private JSONObject obj;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler h = new Handler();
        h.postDelayed(this, 3000);



    }

    @Override
    public void run() {
        account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null){
            if (isAlreadyRegistered(account, new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    if (user.getUsertype() == 1){
                        startActivity(new Intent(getApplicationContext(), ChooseService.class));
                        finish();
                    } else if (user.getUsertype() == 2){
                        startActivity(new Intent(getApplicationContext(), ProviderMenu.class));
                        finish();
                    }
                }
            }));
            startActivity(new Intent(this, Login.class));
            finish();
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
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
                                Toast.makeText(Splash.this, "Erro: "+e,
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Splash.this, "Erro: "+error.toString(),
                        Toast.LENGTH_LONG).show();
                result[0] = false;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result[0];
    }
}
