package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.touchsoul.aguaegas.R;

public class Login extends AppCompatActivity {
    private Button btn_enterWithGoogle, btn_closeApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConfig();
    }

    public void buttonConfig(){
        btn_enterWithGoogle = findViewById(R.id.main_btn_entrarcomgoogle);
        btn_closeApp = findViewById(R.id.main_btn_sairdoapp);

        btn_enterWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectUser.class));
            }
        });

        btn_closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}