package tk.kadaradam.ws2812bcontroller.Fragments;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import tk.kadaradam.ws2812bcontroller.Acitvities.MainActivity;
import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.ColorSequences;
import tk.kadaradam.ws2812bcontroller.Databases.ColorSeqDatabase;
import tk.kadaradam.ws2812bcontroller.Utils.Settings;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by Adam on 8/25/2017.
 */

public class MainFragment extends Fragment {

    View view;

    static int color_selected = Settings.DEFAULT_COLOR;
    private int min_led = Settings.LOWEST_LED_NUM;

    private int NUM_LEDS = 0;
    private int SrvPort;
    private String SrvAdr;

    private RangeSeekBar rsb;

    CheckBox checkBoxColorSeq;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        MainActivity x = (MainActivity)getActivity();
        NUM_LEDS = x.LedCount;
        SrvPort = x.SrvPort;
        SrvAdr = x.SrvAdr;

        checkBoxColorSeq    = (CheckBox) view.findViewById(R.id.main_checkbox_colorseq);
        rsb = (RangeSeekBar) view.findViewById(R.id.rangeSeekBar);
        rsb.setRangeValues(min_led, NUM_LEDS);

        handleColorSelector();
        handleLedChangeButton();
        updateTextBox(false);
        checkBoxChecker();

        //showLightVisualization();

        Log.d("myapp", "fuu");
        return view;
    }

    /*private void showLightVisualization() {
        ColorSeqDatabase db      = new ColorSeqDatabase(getContext());
        List<ColorSequences>    colSeq  = db.getColorSequences();

        if(colSeq.isEmpty()) { // if empty do something...
        }

        LedColorDiagram diagram = new LedColorDiagram(1000, 50, 3, -1);

        int pixer_per_led = 1000 / NUM_LEDS;
        int x, y;

        for (ColorSequences value : colSeq) {
            x = ((value.getLedFrom() - 1) * pixer_per_led);
            y = (value.getLedTo() * pixer_per_led);
            diagram.addItem(value.getLedColor(), x, y);
        }

        ImageView mImageView = (ImageView) view.findViewById(R.id.iv);
        diagram.showOnImage(mImageView);

        db.close();
    }*/


    private void checkBoxChecker() {
        checkBoxColorSeq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                final LinearLayout ColorChangeLayout = (LinearLayout) view.findViewById(R.id.full_color_layout);
                ColorChangeLayout.setVisibility(isChecked ? (View.GONE): (View.VISIBLE));
            }
        });
    }

    private void handleColorSelector() {
        // Set the color circle's color
        ImageView iv = (ImageView)view.findViewById(R.id.myRectangleView);
        final GradientDrawable bgShape = (GradientDrawable)iv.getDrawable();
        bgShape.setColor(color_selected);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), color_selected, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        //String strColor = String.format("%06X", 0xFFFFFF & color);
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

    private void handleLedChangeButton() {
        Button changeColorBtn = (Button) view.findViewById(R.id.led_change_button);
        changeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLedColor(color_selected);
            }
        });
    }

    private void updateLedColor(final int color) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color) & 0xFF;

                int min = (int) rsb.getSelectedMinValue();
                int max = (int) rsb.getSelectedMaxValue();

                try {
                    DatagramSocket datagramSocket = new DatagramSocket();
                    InetAddress host = InetAddress.getByName(SrvAdr);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );


                    if(!checkBoxColorSeq.isChecked()) {
                        for(int i = min_led; i < NUM_LEDS; i++) {

                            outputStream.write((byte) i);
                            if(i >= min && i <= max) {
                                outputStream.write((byte) r);
                                outputStream.write((byte) g);
                                outputStream.write((byte) b);
                            } else {
                                outputStream.write((byte) 0);
                                outputStream.write((byte) 0);
                                outputStream.write((byte) 0);
                            }
                        }
                    } else {
                        ColorSeqDatabase        db      = new ColorSeqDatabase(getContext());
                        List<ColorSequences>    colSeq  = db.getColorSequences();
                        List<Integer>           ledsOn  = new ArrayList<Integer>();;

                        if(colSeq.isEmpty()) {
                            Toast.makeText(getContext(), "No color sequences have been added yet.", Toast.LENGTH_SHORT).show();
                            datagramSocket.close();
                            db.close();
                            return;
                        }

                        for (ColorSequences value : colSeq) {

                            if(value.getLedFrom() >= NUM_LEDS)
                                break;

                            r = (value.getLedColor() >> 16) & 0xFF;
                            g = (value.getLedColor() >> 8) & 0xFF;
                            b = (value.getLedColor()) & 0xFF;

                            for (int i = value.getLedFrom(); i <= value.getLedTo(); i++) {

                                if(i >= NUM_LEDS)
                                    break;

                                ledsOn.add(i);
                                
                                outputStream.write((byte) i);
                                outputStream.write((byte) r);
                                outputStream.write((byte) g);
                                outputStream.write((byte) b);
                            }
                        }

                        // Update the non lighted leds to blank
                        for(int i = min_led; i < NUM_LEDS; i++) {
                            if(!ledsOn.contains(i)) {
                                outputStream.write((byte) i);
                                outputStream.write((byte) 0);
                                outputStream.write((byte) 0);
                                outputStream.write((byte) 0);
                            }
                        }
                        db.close();
                    }

                    byte c[] = outputStream.toByteArray( );
                    DatagramPacket datagramPacket = new DatagramPacket(c, c.length, host, SrvPort);
                    datagramSocket.send(datagramPacket);
                    datagramSocket.close();
                } catch (Exception e) {

                }
            }
        });
    }

   private void updateTextBox(boolean update_variables) {
        TextView AdrTextView        = (TextView) view.findViewById(R.id.address_textview);
        TextView PortTextView       = (TextView) view.findViewById(R.id.port_textview);
        TextView MaxLedTextView     = (TextView) view.findViewById(R.id.max_led_textview);

        if(update_variables) {
            MainActivity x = (MainActivity)getActivity();
            NUM_LEDS = x.LedCount;
            SrvPort = x.SrvPort;
            SrvAdr = x.SrvAdr;
        }

        AdrTextView.setText("Address: " + SrvAdr);
        PortTextView.setText("Port: " + SrvPort);
        MaxLedTextView.setText("Max Leds: " + NUM_LEDS);
    }

    public void updateLeds()
    {
        updateTextBox(true);
        rsb.setRangeValues(min_led, NUM_LEDS);
    }
}
