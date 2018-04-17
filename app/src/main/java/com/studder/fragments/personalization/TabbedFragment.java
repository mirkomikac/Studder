package com.studder.fragments.personalization;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studder.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedFragment extends Fragment {

    private static final String MPOSITION = "POSITION";
    private Integer position;

    public TabbedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabbedFragment.
     */
    public static TabbedFragment newInstance(int position) {

        TabbedFragment fragment = new TabbedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        args.putInt(MPOSITION, position);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        position = getArguments().getInt(MPOSITION);

        return view;
    }

    private void setupViewPager(ViewPager viewPager){
        // Tim6 -> Get Categories Info From DB -> Server
        SectionsCategoryPagerAdapter adapter = new SectionsCategoryPagerAdapter(getChildFragmentManager());

        List<String> categories = new ArrayList<String>(Arrays.asList("Movies", "Travel", "Music"));

        for(int i = 0;i< categories.size();i++){
            adapter.addFragment(CategoriesFragment.newInstance(1), categories.get(i));
        }

        viewPager.setAdapter(adapter);

    }


    public class SectionsCategoryPagerAdapter extends FragmentPagerAdapter {

        public SectionsCategoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            //return PlaceholderFragment.newInstance(position + 1);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
