package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import br.com.touchsoul.aguaegas.R;

public class Splash extends AppCompatActivity implements Runnable {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler h = new Handler();
        h.postDelayed(this, 3000);
    }

    @Override
    public void run() {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}