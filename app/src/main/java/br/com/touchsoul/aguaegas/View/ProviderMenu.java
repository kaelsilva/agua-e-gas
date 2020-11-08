package br.com.touchsoul.aguaegas.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import br.com.touchsoul.aguaegas.R;

public class ProviderMenu extends AppCompatActivity {
    private ImageView btnLogout, btnCloseapp;
    private Button btnConfigurations, btnSeeOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_menu);

        buttonConfig();
    }

    public void buttonConfig(){
        btnLogout = findViewById(R.id.btn_logout);
        btnCloseapp = findViewById(R.id.btn_closeapp);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCloseapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
