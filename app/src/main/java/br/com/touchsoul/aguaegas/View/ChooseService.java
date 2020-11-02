package br.com.touchsoul.aguaegas.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import br.com.touchsoul.aguaegas.R;

public class ChooseService extends AppCompatActivity {
    private ImageView btn_logout;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_choose_service);

        buttonConfig();
    }

    public void buttonConfig(){
        btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
