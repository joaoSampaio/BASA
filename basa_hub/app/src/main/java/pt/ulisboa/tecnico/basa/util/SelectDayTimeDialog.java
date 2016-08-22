package pt.ulisboa.tecnico.basa.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.ui.secondary.DayFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.TimeFragment;

/**
 * Created by Sampaio on 19/08/2016.
 */
public class SelectDayTimeDialog extends DialogFragment {


    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter mAdapter;
    private Button mOkButton;
    private Button mCancelButton;
    private TimeDateSelected timeDateSelected;

    private List<String> selectedDays;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);



//        mDaysArray = getResources().getStringArray(R.array.days_array);
//
//
//
//        mCalendar = Calendar.getInstance();
//        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
//        mCalendar.set(Calendar.MINUTE, mMinute);


    }


    public static SelectDayTimeDialog newInstance()
    {
        SelectDayTimeDialog f = new SelectDayTimeDialog();

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.layout_slide_day_time_picker, container);

        setupViews(view);
        initButtons();

        return view;
    }

    @Override
    public void onDestroyView()
    {
        // Workaround for a bug in the compatibility library where calling
        // setRetainInstance(true) does not retain the instance across
        // orientation changes.
        if (getDialog() != null && getRetainInstance())
        {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }



    private void setupViews(View v)
    {
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        mOkButton = (Button) v.findViewById(R.id.okButton);
        mCancelButton = (Button) v.findViewById(R.id.cancelButton);
        mOkButton.setText("Next");
        selectedDays = new ArrayList<>();
    }





    private void initButtons()
    {
        mOkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(mViewPager.getCurrentItem() == 0){
                    selectedDays = getDays();

                    if(selectedDays.isEmpty()){
                        Toast.makeText(AppController.getAppContext(), "No days selected", Toast.LENGTH_SHORT).show();
                    }else{
                        mViewPager.setCurrentItem(1);
                        mOkButton.setText("Ok");
                    }
                }else{

                    int hour = ((TimeFragment)mAdapter.getFragments().get(1)).getHour();
                    int minute = ((TimeFragment)mAdapter.getFragments().get(1)).getMinute();

                    selectedDays = getDays();
                    if(selectedDays.isEmpty()){
                        Toast.makeText(AppController.getAppContext(), "No days selected", Toast.LENGTH_SHORT).show();
                        mViewPager.setCurrentItem(0);
                        mOkButton.setText("Next");
                    }

                    if(getTimeDateSelected() != null) {

                        List<Integer> weekDays = new ArrayList<Integer>();
                        for(String day : selectedDays){
                            int id = 0;
                            switch (day){
                                case "Sunday":
                                    id = 7;
                                    break;
                                case "Monday":
                                    id = 1;
                                    break;
                                case "Tuesday":
                                    id = 2;
                                    break;
                                case "Wednesday":
                                    id = 3;
                                    break;
                                case "Thursday":
                                    id = 4;
                                    break;
                                case "Friday":
                                    id = 5;
                                    break;
                                case "Saturday":
                                    id = 6;
                                    break;
                            }
                            weekDays.add(id);
                        }
                        Collections.sort( weekDays );
                        getTimeDateSelected().onTimeDateSelected(weekDays, hour, minute);
                        dismiss();
                    }
                }

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }


    private List<String> getDays(){

        if(mAdapter != null && !mAdapter.getFragments().isEmpty()){
            return ((DayFragment)mAdapter.getFragments().get(0)).getSelected();
        }
        return new ArrayList<>();
    }


    public TimeDateSelected getTimeDateSelected() {
        return timeDateSelected;
    }

    public void setTimeDateSelected(TimeDateSelected timeDateSelected) {
        this.timeDateSelected = timeDateSelected;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
            fragments.add(DayFragment.newInstance());
            fragments.add(TimeFragment.newInstance());

        }

        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "Week days";
            return "Time";
        }

        public List<Fragment> getFragments() {
            return fragments;
        }
    }





    public interface TimeDateSelected{
        void onTimeDateSelected(List<Integer> days, int hour, int minute);
    }






}
