package pt.ulisboa.tecnico.basa.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import pt.ulisboa.tecnico.basa.R;

public class TimeFragment extends Fragment
{
    /**
     * Used to communicate back to the parent fragment as the user
     * is changing the time spinners so we can dynamically update
     * the tab text.
     */
    public interface TimeChangedListener
    {
        void onTimeChanged(int hour, int minute);
    }

    private TimeChangedListener mCallback;
    private TimePicker mTimePicker;

    public TimeFragment()
    {
        // Required empty public constructor for fragment.
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }


    public static final TimeFragment newInstance()
    {
        TimeFragment f = new TimeFragment();


        return f;
    }

    /**
     * Create and return the user interface view for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        Context contextThemeWrapper = new ContextThemeWrapper(
                getActivity(), android.R.style.Theme_Holo_Light);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.layout_time, container, false);

        mTimePicker = (TimePicker) v.findViewById(R.id.timePicker);
        // block keyboard popping up on touch
        mTimePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
//        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//
//            @Override
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
//            {
//                mCallback.onTimeChanged(hourOfDay, minute);
//            }
//        });

        // If the client specifies a 24-hour time format, set it on
        // the TimePicker.

            mTimePicker.setIs24HourView(true);



//        mTimePicker.setCurrentHour(initialHour);
//        mTimePicker.setCurrentMinute(initialMinute);


        return v;
    }


    public int getHour(){
        return mTimePicker.getCurrentHour();
    }

    public int getMinute(){
        return mTimePicker.getCurrentMinute();
    }

}