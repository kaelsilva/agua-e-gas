package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.touchsoul.aguaegas.Model.User;
import br.com.touchsoul.aguaegas.Model.VolleyCallBack;
import br.com.touchsoul.aguaegas.R;

public class ProviderDetail extends AppCompatActivity {
    private JSONObject obj;
    private double preco;
    private User provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_detail);

        getFromBundle();
        buttonConfig();

        final TextView tv = findViewById(R.id.tv_provider_price);
        requestPrice(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                tv.setText(String.valueOf(preco));
            }
        });

    }

    public void buttonConfig(){
        Button btn_request = findViewById(R.id.btn_request);
        ImageView btn_back = findViewById(R.id.btn_back2);

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public Bundle receiveDataFromActivity(Bundle bundle){
        Intent i = getIntent();

        bundle = i.getExtras();

        return bundle;
    }

    public void getFromBundle(){
        Bundle bundle = new Bundle();
        TextView tv_provider_name, tv_provider_email;

        bundle = receiveDataFromActivity(bundle);

        provider = new User(
                                bundle.getString("googleid"),
                                bundle.getString("name"),
                                bundle.getString("email")
                            );
        provider.setId(bundle.getInt("id"));

        tv_provider_name = findViewById(R.id.tv_provider_name);
        tv_provider_email = findViewById(R.id.tv_provider_email);

        tv_provider_name.setText(bundle.getString("name"));
        tv_provider_email.setText(bundle.getString("email"));
    }

    public void register(final JSONObject jsonObj) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.server_ip)+"/users";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("googleid", jsonObj.getString("googleid"));
        params.put("name", jsonObj.getString("name"));
        params.put("email", jsonObj.getString("email"));
        params.put("usertype", jsonObj.getString("usertype"));

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // your response
                        //Toast.makeText(ProviderDetail.this, "Usuário cadastrado com sucesso.",
                                //Toast.LENGTH_SHORT).show();

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

    public String requestPrice(final VolleyCallBack callBack){
        //final boolean[] result = new boolean[1];
        final String[] result = new String[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.server_ip)+"/prices?providerid=eq."+provider.getId();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("[]")){
                            result[0] = "0";
                        } else {
                            try {
                                obj = new JSONObject(response.substring(1, response.length()-1));
                                double price = obj.getDouble("price");
                                result[0] = String.valueOf(price);
                                preco = price;
                                callBack.onSuccess();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProviderDetail.this, "Erro: "+e,
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProviderDetail.this, "Erro: "+error.toString(),
                        Toast.LENGTH_LONG).show();
                result[0] = "0";
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result[0];
    }
}
