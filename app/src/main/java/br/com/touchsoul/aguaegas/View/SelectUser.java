package br.com.touchsoul.aguaegas.View;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import br.com.touchsoul.aguaegas.R;

public class SelectUser extends AppCompatActivity {
    private ImageView backBtn, userBtn, providerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_user_type);

        buttonConfig();
    }

    public void buttonConfig(){
        backBtn = findViewById(R.id.btn_back);
        userBtn = findViewById(R.id.iv_choose_user);
        providerBtn = findViewById(R.id.iv_choose_provider);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                startActivity(new Intent(getApplicationContext(), ChooseService.class));
            }
        });

        providerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProviderMenu.class));
            }
        });
    }
}
