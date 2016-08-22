package pt.ulisboa.tecnico.basa.ui.secondary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;


public class DayFragment extends Fragment
{

    private View rootView;
    private RecyclerView mRecyclerView;
    private DaysAdapter mAdapter;
    private String[] data;

    public DayFragment()
    {
        // Required empty public constructor for fragment.
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static final DayFragment newInstance()
    {
        DayFragment f = new DayFragment();

        return f;
    }

    /**
     * Create and return the user interface view for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        data = getResources().getStringArray(R.array.days_array);

        Context contextThemeWrapper = new ContextThemeWrapper(
                getActivity(),
                android.R.style.Theme_Holo_Light);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View  rootView = localInflater.inflate(R.layout.layout_day, container, false);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.listDays);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DaysAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }


    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ItemHolder>{

        private Context context;
        private String[] data;
        private List<String> selectedDays;

        public DaysAdapter(Context context, String[] data) {
            this.context = context;
            this.data = data;
            selectedDays = new ArrayList<>();
        }


        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder viewHolder;
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_checkbox, parent, false);
            viewHolder = new ItemHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {

            String text = data[position];
            holder.checkBox.setText(text);
            holder.value = text;

        }


        @Override
        public int getItemCount() {
            return data.length;
        }

        public class ItemHolder extends RecyclerView.ViewHolder{

            CheckBox checkBox;
            String value;

            public ItemHolder(View itemView) {
                super(itemView);
                checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(isChecked)
                            selectedDays.add(value);
                        else
                            selectedDays.remove(value);

                    }
                });
            }
        }
    }


    public List<String> getSelected() {
        return mAdapter.selectedDays;
    }




}