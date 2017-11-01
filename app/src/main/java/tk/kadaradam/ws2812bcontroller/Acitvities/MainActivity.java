package tk.kadaradam.ws2812bcontroller.Acitvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import tk.kadaradam.ws2812bcontroller.Adapters.SectionsPageAdapter;
import tk.kadaradam.ws2812bcontroller.Fragments.ColorSeqFragment;
import tk.kadaradam.ws2812bcontroller.Fragments.MainFragment;
import tk.kadaradam.ws2812bcontroller.R;
import tk.kadaradam.ws2812bcontroller.Utils.Settings;


public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_COLOR_SEQ = 120;
    private int REQUEST_CODE_SETTINGS = 121;

    public int LedCount;
    public int SrvPort;
    public String SrvAdr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        loadSettings();

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Handler handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkSettings();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_COLOR_SEQ) {
            if(data == null)
                return;

            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            String tag = getFragmentTag(mViewPager.getId(), 1);
            ColorSeqFragment f = (ColorSeqFragment) getSupportFragmentManager().findFragmentByTag(tag);
            f.addColorItem(data);
        }
        if(requestCode == REQUEST_CODE_SETTINGS) {
            loadSettings();

            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            String tag = getFragmentTag(mViewPager.getId(), 0);
            MainFragment f = (MainFragment) getSupportFragmentManager().findFragmentByTag(tag);
            f.updateLeds();

            tag = getFragmentTag(mViewPager.getId(), 1);
            ColorSeqFragment f2 = (ColorSeqFragment) getSupportFragmentManager().findFragmentByTag(tag);
            f2.updateSeqListView();
        }
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition)
    {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME, 0);
        SrvAdr  = settings.getString("hostname", "");
        SrvPort = settings.getInt("port", 0);
        LedCount = settings.getInt("max_led", 0);
    }

    private void checkSettings() {
        if(SrvAdr.equals("") && SrvPort == 0 && LedCount == 0) {
            Log.d("Adam", "No settings detected");

            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("hostname", SrvAdr);
            intent.putExtra("port", SrvPort);
            intent.putExtra("max_led", LedCount);
            intent.putExtra("fill_required", true);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);

            Toast.makeText(this, "Please enter your led info", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_settings, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("hostname", SrvAdr);
            intent.putExtra("port", SrvPort);
            intent.putExtra("max_led", LedCount);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "Controller");
        adapter.addFragment(new ColorSeqFragment(), "Color Sequences");
        viewPager.setAdapter(adapter);
    }
}
