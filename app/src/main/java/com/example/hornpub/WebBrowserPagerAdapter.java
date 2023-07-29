package com.example.hornpub;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WebBrowserPagerAdapter extends FragmentStateAdapter {

    public WebBrowserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BrowserFragment();
            case 1:
                return new ReaderModeFragment();
            default:
                return new BrowserFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
