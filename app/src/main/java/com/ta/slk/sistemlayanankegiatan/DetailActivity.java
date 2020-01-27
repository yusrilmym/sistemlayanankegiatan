package com.ta.slk.sistemlayanankegiatan;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CpuUsageInfo;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.ta.slk.sistemlayanankegiatan.Fragments.ActivitiesFragment;
import com.ta.slk.sistemlayanankegiatan.Fragments.CommentFragment;
import com.ta.slk.sistemlayanankegiatan.Fragments.DetailFragment;
import com.ta.slk.sistemlayanankegiatan.Fragments.DocumentationFragment;
import com.ta.slk.sistemlayanankegiatan.Method.Application;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

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
    private Bundle activity;
    public static DetailActivity detail;
    TextView contact, location, date, clock, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detail = this;
        activity = getIntent().getBundleExtra("activity");
        initComponents();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CircleImageView toolbarImage = findViewById(R.id.toolbar_img);
        Glide.with(getApplicationContext()).load(ApiClient.BASE_URL + "uploads/" + activity.getString("picture")).into(toolbarImage);
    }

    private void initComponents() {
        title = findViewById(R.id.dt_title);
        contact = findViewById(R.id.dt_contact);
        date = findViewById(R.id.dt_date);
        clock = findViewById(R.id.dt_clock);
        location = findViewById(R.id.dt_location);
        contact.setText(activity.getString("admin"));
        ImageButton btnBack = findViewById(R.id.btn_back);
        String[] dateTime = activity.getString("date").split(" ");
        date.setText(dateTime[0]);
        clock.setText(dateTime[1]);
        location.setText(activity.getString("place"));
        title.setText(activity.getString("name"));
        ImageView imageView = findViewById(R.id.img_mini);
        Glide.with(getApplicationContext()).load(ApiClient.BASE_URL + "uploads/" + activity.getString("picture")).into(imageView);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    DetailFragment detail = new DetailFragment();
                    detail.setArguments(activity);
                    return detail;
                case 1:
                    CommentFragment comment = new CommentFragment();
                    comment.setArguments(activity);
                    return comment;
                case 2:
                    DocumentationFragment documentation = new DocumentationFragment();
                    documentation.setArguments(activity);
                    return documentation;
                    default:
                        return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Detail";
                case 1:
                    return "Komentar";
                case 2:
                    return "Dokumentasi";
                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public void onBackPressed() {
//        Intent i=new Intent(this,AdminContent.class);
//        startActivity(i);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void backButton(View view) {
        finish();
    }
}
