package pt.ulisboa.tecnico.basa.model.recipe.trigger;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.SelectDayTimeDialog;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

public class TimeTrigger extends TriggerAction {

    public final static int AT_TIME = 0;
    public final static String AT_TIME_STRING = "Day and time is";
    private transient Handler handler;
    private transient Runnable run;
    private transient Launch2Activity activity;

    public TimeTrigger() {
        super(TRIGGER_TIME);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(AT_TIME_STRING, AT_TIME);
        super.setAlternatives(alt);

        super.setDescription("The timer trigger allows the user to run certain actions at a specific time.\n" +
                "For example you can turn on the heating in the office at 7 O'clock so that when you arrive the office is pre-heated.\n" +
                "It is also possible to chain more complex trigger for example if I am still at the office at 20 O'clock then send a mail to my wife telling I will be working late.");

    }

    @Override
    public View.OnClickListener getListener(final Context ctx, final TriggerActionParameterSelected triggerActionParameterSelected) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object choice = v.getTag();
                if(choice != null){
                    final int choiceNum = (choice instanceof Double)? ((Double)choice).intValue() : (int) choice;
                    Log.d("time", "is activity:" + (ctx instanceof Launch2Activity));
                    if(ctx instanceof Launch2Activity){


                        SelectDayTimeDialog newFragment =SelectDayTimeDialog.newInstance();
                        newFragment.setTimeDateSelected(new SelectDayTimeDialog.TimeDateSelected() {
                            @Override
                            public void onTimeDateSelected(List<Integer> days, int hour, int minute) {
                                List<String> param = new ArrayList<>();
                                param.add(choiceNum+"");
                                param.add(new Gson().toJson(days));
                                param.add(hour+"");
                                param.add(minute+"");
                                triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);
                            }
                        });
                        String tag = "SelectDayTimeDialog";

                        if(newFragment != null) {
                            FragmentTransaction ft = ((Launch2Activity)ctx).getSupportFragmentManager().beginTransaction();
                            Fragment prev = ((Launch2Activity)ctx).getSupportFragmentManager().findFragmentByTag(tag);
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);
                            newFragment.show(ft, tag);
                        }

                    }

//                    new DialogEditText(ctx, "Select light level (lx)", "Light level:", new DialogEditText.TextSelected() {
//                        @Override
//                        public void onTextSelected(String text) {
//
//                            List<String> param = new ArrayList<>();
//                            param.add(choiceNum+"");
//                            param.add(text);
//                            triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);
//
//                        }
//                    }).show();
                }
            }
        };
    }


    public void setUpCustomView(ViewGroup parent){
    }

    public void destroyCustomView(){
        if(handler != null){
            handler.removeCallbacks(run);
        }
    }


    @Override
    public int  getColor() {
        return Color.parseColor("#FF00CA9F");
    }

    @Override
    public String getParameterTitle() {


        List<Long> timesToRun = getTimerDate();
        for (Long time : timesToRun){
            Log.d("time", "timesToRun:"+time);


        }


        String msg = AT_TIME_STRING;
        if(getParameters().size() == 4){

            int choice = Integer.parseInt( getParameters().get(0));


            String days = "";

            List<Integer> weekDays = new Gson().fromJson(getParameters().get(1), new TypeToken<List<Integer>>() {
            }.getType());

            LocalDate date = new LocalDate();
            for(Integer d : weekDays){

                days = days + date.withDayOfWeek(d).dayOfWeek().getAsText() + ", ";
            }

            if(days.length() > 0)
                days = days.substring(0, days.length()-2);

            String time = new DateTime().withTime(getParametersInt(2), getParametersInt(3), 0, 0).toString(DateTimeFormat.forPattern("H:m"));
            msg = "day is " + days + " and time is " + time;

        }

        return msg;
    }

    public List<Long> getTimerDate(){

        DateTime date = new DateTime();
        List<Integer> weekDays = new Gson().fromJson(getParameters().get(1), new TypeToken<List<Integer>>() {
        }.getType());
        List<Long> dates = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        int todayDayOfWeek = today.getDayOfWeek();
        Log.d("time", "today.getHourOfDay():"+today.getHourOfDay());
        Log.d("time", "today.getMinuteOfHour():"+today.getMinuteOfHour());

        for(Integer d : weekDays){


            int week = 0;
            if(d < todayDayOfWeek)
                week = 1;
            else if (d == todayDayOfWeek && today.getHourOfDay() > getParametersInt(2)   ) {
                week = 1;
            }else if (d == todayDayOfWeek && today.getHourOfDay() == getParametersInt(2) && today.getMinuteOfHour() > getParametersInt(3)  ) {
                week = 1;
            }
            Log.d("time", "Hour():"+getParametersInt(2));
            Log.d("time", "Min():"+getParametersInt(3));
            Log.d("time", "Add week():"+(d <= todayDayOfWeek && today.getHourOfDay() <= getParametersInt(2) && today.getMinuteOfHour() <= getParametersInt(3)));

            dates.add(date.withDayOfWeek(d)
                    .plusWeeks(week)
                    .withTime(getParametersInt(2), getParametersInt(3), 0, 0).toDateTime().getMillis());
        }
        return dates;
    }

}
