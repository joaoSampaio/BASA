package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.StatisticalEvent;


public class StatisticsFragment extends DialogFragment  {

    private View rootView;
    private LineChart mChart;
    private Spinner spinner;
    private List<StatisticalEvent> lightsData;
    private long mappingLargestValue;

    public StatisticsFragment() {
        // Required empty public constructor
    }



    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){

        spinner = (Spinner)rootView.findViewById(R.id.spinner);
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Occupancy");
        spinnerArray.add("Temperature");
        spinnerArray.add("Lights");
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    setDataOccupancy();
                }else if(position == 1){
                    setDataTemperature();
                } else{
                    setDataLights();
                }
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        });

        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        textViewDescription.setText("Statistics");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mChart = (LineChart) rootView.findViewById(R.id.chart1);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);


        XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
//        xAxis.setTextColor(Color.rgb(255, 192, 56));
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setGranularity(1000L); // one minute in millis

        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm:ss");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                if(lightsData != null && !lightsData.isEmpty()){
                    Log.d("stats", " value:"+value);

                    long real = (long)(mappingLargestValue - value)*1000 + lightsData.get(0).getX();
                    return mFormat.format(new Date(real));
                }
                return "nao sei";
//                return mFormat.format(new Date((long) value));
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setYOffset(-9f);
//        leftAxis.setYOffset(39f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setEnabled(false);
        rightAxis.setAxisMinValue(0f);



        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);



        setDataOccupancy();
        mChart.invalidate();
    }


    private void setData(int count, float range) {

        long now = System.currentTimeMillis();
        long hourMillis = 3600000L;
        long minuteMillis = 60000L;
        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;
        float to = now + (count / 2) * hourMillis;

        values.add(new Entry(now, 0));
        values.add(new Entry(now + 1*hourMillis, 1));
        values.add(new Entry(now + 2*hourMillis, 1));
        values.add(new Entry(now + 3*hourMillis, 2));
        values.add(new Entry(now + 4*hourMillis, 0));
        values.add(new Entry(now + 5*hourMillis, 0));
//
//        for (float x = from; x < to; x += hourMillis) {
//
//            float y = getRandom(range, 50);
//            values.add(new Entry(x, y)); // add one entry per hour
//        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.STEPPED);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);




        values = new ArrayList<Entry>();

        values.add(new Entry(now, 27));
        values.add(new Entry(now + 1*hourMillis, 28));
        values.add(new Entry(now + 2*hourMillis, 29));
        values.add(new Entry(now + 2*hourMillis + 5*minuteMillis, 30));
        values.add(new Entry(now + 2*hourMillis + 20*minuteMillis, 31));
        values.add(new Entry(now + 2*hourMillis + 40*minuteMillis, 32));
        values.add(new Entry(now + 3*hourMillis, 33));
        values.add(new Entry(now + 4*hourMillis, 28));
        values.add(new Entry(now + 5*hourMillis, 19));
        LineDataSet setTemperature = new LineDataSet(values, "DataSet Temperature");
        setTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
        setTemperature.setColor(Color.RED);
        setTemperature.setValueTextColor(Color.BLACK);
        setTemperature.setLineWidth(1.5f);
        setTemperature.setDrawCircles(true);
        setTemperature.setDrawValues(true);
        setTemperature.setFillAlpha(65);
        setTemperature.setFillColor(ColorTemplate.getHoloBlue());
        setTemperature.setHighLightColor(Color.rgb(244, 117, 117));
        setTemperature.setDrawCircleHole(false);
        setTemperature.setMode(LineDataSet.Mode.LINEAR);

        // create a data object with the datasets


        data.addDataSet(setTemperature);


        // set data
        mChart.setData(data);
    }

    private void setDataTemperature() {
        mChart.clear();
//        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        long now = System.currentTimeMillis();
        long hourMillis = 3600000L;
        long minuteMillis = 60000L;
        ArrayList<Entry> values = new ArrayList<Entry>();

        values.add(new Entry(now, 27));
        values.add(new Entry(now + 1*hourMillis, 28));
        values.add(new Entry(now + 2*hourMillis, 29));
        values.add(new Entry(now + 2*hourMillis + 5*minuteMillis, 30));
        values.add(new Entry(now + 2*hourMillis + 20*minuteMillis, 31));
        values.add(new Entry(now + 2*hourMillis + 40*minuteMillis, 32));
        values.add(new Entry(now + 3*hourMillis, 33));
        values.add(new Entry(now + 4*hourMillis, 28));
        values.add(new Entry(now + 5*hourMillis, 19));
        LineDataSet setTemperature = new LineDataSet(values, "Temperature");
        setTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
        setTemperature.setColor(Color.RED);
        setTemperature.setValueTextColor(Color.BLACK);
        setTemperature.setLineWidth(1.5f);
        setTemperature.setDrawCircles(true);
        setTemperature.setDrawValues(true);
        setTemperature.setFillAlpha(65);
        setTemperature.setFillColor(ColorTemplate.getHoloBlue());
        setTemperature.setHighLightColor(Color.rgb(244, 117, 117));
        setTemperature.setDrawCircleHole(false);
        setTemperature.setMode(LineDataSet.Mode.LINEAR);

        // create a data object with the datasets


        LineData data = new LineData(setTemperature);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);


        // set data
        mChart.setData(data);
    }


    private void setDataLights() {
        mChart.clear();
        ArrayList<Entry> values = new ArrayList<Entry>();
        lightsData =  AppController.getInstance().getStatisticalData().getLights();

        Log.d("stats", " setDataLights:" + lightsData.size());
        long now = System.currentTimeMillis();
        long hourMillis = 3600000L;

        long xValue, largestValue, shiftValue;

        largestValue = (lightsData.get(lightsData.size()-1).getX()- lightsData.get(0).getX())/1000;
        mappingLargestValue = largestValue;
        for(StatisticalEvent stat : lightsData){
            Log.d("stats", "original x:"+stat.getX() + " y:"+stat.getY());

            xValue = (stat.getX() - lightsData.get(0).getX())/1000;
            shiftValue = largestValue - xValue;
            values.add(0, new Entry(shiftValue , stat.getY()));
        }



        for(Entry stat : values){
            Log.d("stats", " x:"+stat.getX() + " y:"+stat.getY());
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Lights");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLUE);
        set1.setValueTextColor(Color.BLACK);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLUE);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.STEPPED);
//        set1.setDrawFilled(true);
        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }


    private void setDataOccupancy() {
        mChart.clear();
        ArrayList<Entry> values = new ArrayList<Entry>();
        lightsData =  AppController.getInstance().getStatisticalData().getOccupantsBuilding();

        Log.d("stats", " setDataLights:" + lightsData.size());
        long now = System.currentTimeMillis();
        long hourMillis = 3600000L;

        long xValue, largestValue, shiftValue;

        largestValue = (lightsData.get(lightsData.size()-1).getX()- lightsData.get(0).getX())/1000;
        mappingLargestValue = largestValue;
        for(StatisticalEvent stat : lightsData){
            Log.d("stats", "original x:"+stat.getX() + " y:"+stat.getY());

            xValue = (stat.getX() - lightsData.get(0).getX())/1000;
            shiftValue = largestValue - xValue;
            values.add(0, new Entry(shiftValue , stat.getY()));
        }



        for(Entry stat : values){
            Log.d("stats", " x:"+stat.getX() + " y:"+stat.getY());
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Building occupants");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLUE);
        set1.setValueTextColor(Color.BLACK);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLUE);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.STEPPED);
//        set1.setDrawFilled(true);
        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();




    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }
}