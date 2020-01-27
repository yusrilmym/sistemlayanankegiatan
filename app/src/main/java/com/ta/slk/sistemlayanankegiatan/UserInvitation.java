package com.ta.slk.sistemlayanankegiatan;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.ta.slk.sistemlayanankegiatan.Fragments.UserInvitationAccept;
import com.ta.slk.sistemlayanankegiatan.Fragments.UserInvitationRejected;

import java.util.zip.Inflater;

public class UserInvitation extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private AppBarLayout appBarLayout;
     Toolbar toolbar;
    private Drawable oldColor;
    private int currentColor;
    private PagerSlidingTabStrip strip;
    SystemBarTintManager systemBarTintManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_invitation);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        systemBarTintManager = new SystemBarTintManager(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentColor = getResources().getColor(R.color.colorPrimary);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        strip = findViewById(R.id.tabs);
        strip.setViewPager(mViewPager);
        strip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        changeColor(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorLightBlue));
                        break;
                    case 1:
                        changeColor(getResources().getColor(R.color.mateGreen),getResources().getColor(R.color.colorLightGreen));
                        break;
                    case 2:
                        changeColor(getResources().getColor(R.color.mateRed),getResources().getColor(R.color.colorLightRed));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    private void changeColor(int newColor, int newColorTint){
        strip.setBackgroundColor(newColor);
        systemBarTintManager.setStatusBarTintColor(newColor);
        Drawable colorDrawable = new ColorDrawable(newColorTint);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable,bottomDrawable});
        if(oldColor == null){
            mViewPager.setBackgroundColor(newColorTint);
            toolbar.setBackgroundColor(newColor);
        }else{
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldColor, layerDrawable});
            mViewPager.setBackground(transitionDrawable);
            toolbar.setBackgroundColor(newColor);
            transitionDrawable.startTransition(500);
        }

        oldColor = layerDrawable;
        currentColor = newColor;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "MENUNGGU KONFIRMASI", "DITERIMA", "DITOLAK" };
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    com.ta.slk.sistemlayanankegiatan.Fragments.UserInvitation invitation = new com.ta.slk.sistemlayanankegiatan.Fragments.UserInvitation();
                    return invitation;
                case 1:
                    UserInvitationAccept userInvitationAccept = new UserInvitationAccept();
                    return userInvitationAccept;
                case 2:
                    UserInvitationRejected userInvitationRejected = new UserInvitationRejected();
                    return userInvitationRejected;
                default:
                    return null;

            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
