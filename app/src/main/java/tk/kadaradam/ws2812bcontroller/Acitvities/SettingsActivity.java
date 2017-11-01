package tk.kadaradam.ws2812bcontroller.Acitvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.Settings;

public class SettingsActivity extends AppCompatActivity {

    boolean FillRequired = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText AdrView    = (EditText) findViewById(R.id.address_input);
        final EditText PortView   = (EditText) findViewById(R.id.port_input);
        final EditText MaxLedView = (EditText) findViewById(R.id.maxled_input);
        Button SaveButton   = (Button) findViewById(R.id.settings_save);

        // Set EditText default values
        Intent intent = getIntent();
        FillRequired = intent.getBooleanExtra("fill_required", false);

        if(!FillRequired) {
            String tmp_Address = intent.getStringExtra("hostname");
            int tmp_Port = intent.getIntExtra("port", 0);
            int tmp_Max_led = intent.getIntExtra("max_led", 0);

            AdrView.setText(tmp_Address);
            PortView.setText(String.valueOf(tmp_Port));
            MaxLedView.setText(String.valueOf(tmp_Max_led));
        }


        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regEx = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";

                String SrvAdr  = AdrView.getText().toString();
                String SrvPort_str = PortView.getText().toString();
                String MaxLeds_str = MaxLedView.getText().toString();

                if(SrvAdr.isEmpty() && SrvPort_str.isEmpty() && MaxLeds_str.isEmpty())
                    return;

                int SrvPort = Integer.parseInt(SrvPort_str);
                int MaxLeds = Integer.parseInt(MaxLeds_str);

                if(!SrvAdr.matches(regEx)) {
                    Toast.makeText(getApplicationContext(), "Invalid hostname", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("hostname", SrvAdr);
                editor.putInt("port", SrvPort);
                editor.putInt("max_led", MaxLeds);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();

                FillRequired = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!FillRequired) {
            setResult(RESULT_OK, null);
            finish();
        }
    }
}
