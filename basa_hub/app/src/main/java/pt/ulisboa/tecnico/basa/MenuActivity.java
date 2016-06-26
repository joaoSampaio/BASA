package pt.ulisboa.tecnico.basa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int[] Click = {R.id.action_camera, R.id.action_temperature, R.id.action_web };
    private TextView txtwebserver;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        for (int id:Click) {
            findViewById(id).setOnClickListener(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }




    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.action_camera:
                intent = new Intent(MenuActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.action_temperature:
                intent = new Intent(MenuActivity.this, TemperatureActivity.class);
                startActivity(intent);
                break;
            case R.id.action_web:
                intent = new Intent(MenuActivity.this, WebServerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
