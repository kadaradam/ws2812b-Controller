package tk.kadaradam.ws2812bcontroller.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.ColorSequences;

/**
 * Created by Adam on 8/30/2017.
 */

public class ColorSeqArrayAdapter extends ArrayAdapter<ColorSequences> {

    private final Context context;
    private int maxLeds;

    public ColorSeqArrayAdapter(Context context, List<ColorSequences> sequence, int maxLeds) {
        super(context, R.layout.colorseq_list_item, sequence);
        this.context = context;
        this.maxLeds = maxLeds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ColorSequences sequence = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.colorseq_list_item, parent, false);
        }

        TextView textTittle     = (TextView) convertView.findViewById(R.id.cs_listitem_text);
        TextView textDesc       = (TextView) convertView.findViewById(R.id.cs_listitem_desc);
        ImageView imageView     = (ImageView) convertView.findViewById(R.id.cs_listitem_color);
        GradientDrawable bgShape = (GradientDrawable)imageView.getDrawable();

        bgShape.setColor(sequence.getLedColor());
        textTittle.setText("Leds: " + sequence.getLedFrom() + " - " + sequence.getLedTo());

        if(sequence.getLedTo() > this.maxLeds) {
            textDesc.setError("");
            textDesc.setText("ERROR: Sequence wont be visible");
        }else {
            textDesc.setError(null);
            textDesc.setText("");
        }

        return convertView;
    }
    public void updateLedSize(int new_ledsize) {
        this.maxLeds = new_ledsize;
    }
}
