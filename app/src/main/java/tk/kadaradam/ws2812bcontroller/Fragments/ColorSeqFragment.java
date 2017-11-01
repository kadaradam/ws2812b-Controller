package tk.kadaradam.ws2812bcontroller.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import tk.kadaradam.ws2812bcontroller.Acitvities.ColorAddActivity;
import tk.kadaradam.ws2812bcontroller.Adapters.ColorSeqArrayAdapter;
import tk.kadaradam.ws2812bcontroller.Acitvities.MainActivity;
import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.ColorSequences;
import tk.kadaradam.ws2812bcontroller.Databases.ColorSeqDatabase;
import tk.kadaradam.ws2812bcontroller.Utils.Settings;


/**
 * Created by Adam on 8/25/2017.
 */

public class ColorSeqFragment extends Fragment {

    int REQUEST_CODE_COLOR_SEQ = 120;

    View view;
    ColorSeqArrayAdapter adapter;
    int NUM_LEDS;
    private int firstFreeLED = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.colorseq_fragment, container, false);

        SharedPreferences settings = getContext().getSharedPreferences(Settings.PREFS_NAME, 0);
        NUM_LEDS = settings.getInt("max_led", 0);

        handleAddColor();
        showListView();
        return view;
    }



    private void showListView() {
        final ListView listview = (ListView) view.findViewById(R.id.list_color);

        ColorSeqDatabase db = new ColorSeqDatabase(getContext());
        final List<ColorSequences> colSeq = db.getColorSequences();

        adapter = new ColorSeqArrayAdapter(getContext(), colSeq, NUM_LEDS);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                showConfirmDeleteDialog(colSeq.get(pos));
                return true;
            }
        });

        db.close();
    }

    private void showConfirmDeleteDialog(final ColorSequences value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setMessage("Are you sure you want to delete the color? " + value.getLedFrom() + " - " + value.getLedTo());
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                ColorSeqDatabase db = new ColorSeqDatabase(getContext());
                db.removeColorSequence(value.getID());
                db.close();

                adapter.remove(value);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleAddColor() {
        /*if(firstFreeLED == -1 || firstFreeLED > NUM_LEDS) {
            Toast.makeText(getContext(), "All leds are taken", Toast.LENGTH_SHORT).show();
            return;
        }*/

        final MainActivity x = (MainActivity)getActivity();

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab_add_color);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ColorAddActivity.class);
                intent.putExtra("max_led", x.LedCount);
                getActivity().startActivityForResult(intent, REQUEST_CODE_COLOR_SEQ);
            }
        });
    }

    public void addColorItem(Intent data) {
        long id     = data.getLongExtra("id", -1);
        int min     = data.getIntExtra("led_min", 0);
        int max     = data.getIntExtra("led_max", 0);
        int color   = data.getIntExtra("led_color", 0);

        if(id == -1 && min == 0 && max == 0 && color == 0) {
            Log.d("myapp", "Error during adding new item to the listadapter");
            return;
        }
        adapter.add(new ColorSequences(id, min, max, color));
    }

    public void updateSeqListView() {
        MainActivity x = (MainActivity)getActivity();
        NUM_LEDS = x.LedCount;

        adapter.updateLedSize(NUM_LEDS);
        adapter.notifyDataSetChanged();
    }
}
