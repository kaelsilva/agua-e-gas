package br.com.touchsoul.aguaegas.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.touchsoul.aguaegas.Model.User;
import br.com.touchsoul.aguaegas.Model.VolleyCallBack;
import br.com.touchsoul.aguaegas.R;

public class ProviderSettings extends AppCompatActivity {
    private ImageView btnBack;
    private Button btnSave, btnUpdateLocation;
    private GoogleSignInAccount account;
    private User provider;
    private JSONObject obj;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location lastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_settings);

        account = GoogleSignIn.getLastSignedInAccount(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buttonConfig();

        if (isProviderAvailable(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                UIconfig();
            }
        })) ;
    }

    public void UIconfig() {
        EditText et_provider_name = findViewById(R.id.et_provider_name);
        et_provider_name.setText(provider.getName());

        setUsertypeChecks();
    }

    public void setUsertypeChecks() {
        CheckBox cb_water, cb_gas;
        cb_water = findViewById(R.id.cb_water);
        cb_gas = findViewById(R.id.cb_gas);

        if (provider.getUsertype() == 2) {
            cb_water.setChecked(true);
        } else if (provider.getUsertype() == 3) {
            cb_gas.setChecked(true);
        } else {
            cb_water.setChecked(true);
            cb_gas.setChecked(true);
        }
    }

    public void buttonConfig() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        btnUpdateLocation = findViewById(R.id.btn_update_location);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_provider_name = findViewById(R.id.et_provider_name);
                CheckBox cb_water, cb_gas;
                cb_water = findViewById(R.id.cb_water);
                cb_gas = findViewById(R.id.cb_gas);

                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                provider.setName(et_provider_name.getText().toString());
                provider.setLatitude(location.getLatitude());
                provider.setLongitude(location.getLongitude());

                try {
                    @SuppressLint("ResourceType") JSONObject jsonUpdated = new JSONObject("{" +
                            "\"id\": \""+provider.getId()+"\"," +
                            "\"name\": \""+provider.getName()+"\"," +
                            "\"latitude\": \""+provider.getLatitude()+"\"," +
                            "\"longitude\": \""+provider.getLongitude()+"\"" +
                            "}");
                    updateProvider(jsonUpdated);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProviderSettings.this, "Localização atualizada.", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProviderSettings.this, "Latitude: "+provider.getLatitude(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ProviderSettings.this, "Longitude: "+provider.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void updateProvider(final JSONObject jsonObj) throws JSONException {
        //Toast.makeText(this, "jsonObj: "+jsonObj.toString(), Toast.LENGTH_SHORT).show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.server_ip)+"/users?id=eq."+provider.getId();

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // your response
                        Toast.makeText(ProviderSettings.this, "Usuário atualizado com sucesso.",
                                Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //Toast.makeText(ProviderSettings.this, "Erro em register(): "+error.toString(),
                //        Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
        //requestQueue.start();
    }

    public boolean isProviderAvailable(final VolleyCallBack callBack){
        final boolean[] result = new boolean[1];
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.server_ip)+"/users?email=eq."+account.getEmail();

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
                                //User provider;
                                provider = new User(
                                    obj.getString("googleid"),
                                    obj.getString("name"),
                                    obj.getString("email")
                                );

                                provider.setId(obj.getInt("id"));
                                provider.setUsertype(obj.getInt("usertype"));
                                provider.setLatitude(obj.getDouble("latitude"));
                                provider.setLongitude(obj.getDouble("longitude"));

                                result[0] = true;
                                callBack.onSuccess();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProviderSettings.this, "Erro: "+e,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProviderSettings.this, "Erro: "+error.toString(),
                        Toast.LENGTH_LONG).show();
                result[0] = false;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return result[0];
    }
}
