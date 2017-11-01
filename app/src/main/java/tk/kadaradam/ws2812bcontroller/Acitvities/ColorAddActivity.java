package tk.kadaradam.ws2812bcontroller.Acitvities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.List;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import tk.kadaradam.ws2812bcontroller.Databases.ColorSeqDatabase;
import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.InputFilterMinMax;
import tk.kadaradam.ws2812bcontroller.Utils.Settings;
import tk.kadaradam.ws2812bcontroller.Utils.ColorSequences;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorAddActivity extends AppCompatActivity {

    private int color_selected = Settings.DEFAULT_COLOR;
    private int NUM_LEDS = 0;
    private int min_led = Settings.LOWEST_LED_NUM;

    private EditText ledFromInput;
    private EditText ledToInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_add);

        ledFromInput    = (EditText)findViewById(R.id.seq_led_from_input);
        ledToInput      = (EditText)findViewById(R.id.seq_led_to_input);

        Intent intent = getIntent();
        NUM_LEDS = intent.getIntExtra("max_led", 0);

        final RangeSeekBar rsb = (RangeSeekBar) findViewById(R.id.rangebar_color_add);
        rsb.setRangeValues(min_led, NUM_LEDS);

        rsb.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                ledFromInput.setText("" + minValue);
                ledToInput.setText("" + maxValue);
            }
        });

        ledFromInput.setText("" + min_led);
        ledToInput.setText("" + NUM_LEDS);
        ledFromInput.setFilters(new InputFilter[]{new InputFilterMinMax(min_led, NUM_LEDS)});
        ledToInput.setFilters(new InputFilter[]{new InputFilterMinMax(min_led + 1, NUM_LEDS)});

        handleColorSelector();
        saveColor();

        // Test


        Button sliderButton = (Button)findViewById(R.id.sliderSetButton);
        sliderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int from        = Integer.parseInt(ledFromInput.getText().toString());
                int to          = Integer.parseInt(ledToInput.getText().toString());

                if(from >= to) {
                    Toast.makeText(getApplicationContext(), "The starting led number should be lower than the ending number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                rsb.setSelectedMinValue(from);
                rsb.setSelectedMaxValue(to);
            }
        });

        /*ledFromInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    return;

                int
                        num = Integer.parseInt(ledFromInput.getText().toString()),
                        max = (int) rsb.getSelectedMaxValue(),
                        min = (int) rsb.getSelectedMinValue();

                if(num >= max) {
                    ledFromInput.setText("" + min);
                    return;
                }

                if (num >= min_led && num <= NUM_LEDS) {
                    rsb.setSelectedMinValue(num);
                }

            }
        });

        ledToInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    return;

                int
                        num = Integer.parseInt(ledToInput.getText().toString()),
                        max = (int) rsb.getSelectedMaxValue(),
                        min = (int) rsb.getSelectedMinValue();

                if(num <= min) {
                    ledToInput.setText("" + max);
                    return;
                }

                if (num >= (min_led + 1) && num <= NUM_LEDS)
                    rsb.setSelectedMaxValue(num);
            }
        });

        ledFromInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().matches("")) {
                    int num = Integer.parseInt(s.toString());

                    if(num >= (int) rsb.getSelectedMaxValue()) {
                        ledFromInput.setText("" + min_led);
                        return;
                    }

                    if (num >= min_led && num <= NUM_LEDS) {
                        rsb.setSelectedMinValue(num);
                    }
                }
            }
        });

        ledToInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().matches("")) {
                    int num = Integer.parseInt(s.toString());

                    if(num <= (int) rsb.getSelectedMinValue()) {
                        ledToInput.setText("" + NUM_LEDS);
                        return;
                    }

                    if (num >= (min_led + 1) && num <= NUM_LEDS)
                        rsb.setSelectedMaxValue(num);
                }
            }
        });*/
    }


    private void handleColorSelector() {
        // Set the color circle's color
        ImageView iv = (ImageView)findViewById(R.id.view_color_add);
        final GradientDrawable bgShape = (GradientDrawable)iv.getDrawable();
        bgShape.setColor(color_selected);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(ColorAddActivity.this, color_selected, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {

                        color_selected = color;
                        bgShape.setColor(color);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();


            }
        });
    }

    private void saveColor() {
        final ColorSeqDatabase db = new ColorSeqDatabase(ColorAddActivity.this);
        final List<ColorSequences> colSeq = db.getColorSequences();

        Button saveButton = (Button) findViewById(R.id.button_color_add);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean alreadyInRange = false;

                RangeSeekBar rsb = (RangeSeekBar)findViewById(R.id.rangebar_color_add);
                int min = (int) rsb.getSelectedMinValue();
                int max = (int) rsb.getSelectedMaxValue();

                for (ColorSequences i : colSeq) {
                    if ((min >= i.getLedFrom() && min <= i.getLedTo()) || (max >= i.getLedFrom() && max <= i.getLedTo()))
                        alreadyInRange = true;
                }

                if(alreadyInRange) {
                    Toast.makeText(getApplicationContext(), "Unable to save. Another color touches that range.", Toast.LENGTH_SHORT).show();
                    return;
                }

                long id = db.addColorSequence(new ColorSequences(min, max, color_selected));
                Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();

                Intent output = new Intent();
                output.putExtra("id", id);
                output.putExtra("led_min", min);
                output.putExtra("led_max", max);
                output.putExtra("led_color", color_selected);
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }
}
