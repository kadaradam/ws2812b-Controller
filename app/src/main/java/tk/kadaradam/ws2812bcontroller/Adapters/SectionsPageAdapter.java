package tk.kadaradam.ws2812bcontroller.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 8/25/2017.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFramgentList = new ArrayList<>();
    private final List<String> mFramgentTitleList = new ArrayList<>();

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFramgentList.add(fragment);
        mFramgentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFramgentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFramgentList.get(position);
    }

    @Override
    public int getCount() {
        return mFramgentList.size();
    }

}
