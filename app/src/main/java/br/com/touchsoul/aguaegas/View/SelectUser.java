package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import br.com.touchsoul.aguaegas.R;

public class SelectUser extends AppCompatActivity {
    private ImageView backBtn, userBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_user_type);

        buttonConfig();
    }

    public void buttonConfig(){
        backBtn = findViewById(R.id.btn_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userBtn = findViewById(R.id.iv_choose_user);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                startActivity(new Intent(getApplicationContext(), ChooseService.class));
            }
        });
    }
}