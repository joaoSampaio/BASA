package pt.ulisboa.tecnico.basa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

public class PreMadeRecipeAdapter extends RecyclerView.Adapter<PreMadeRecipeAdapter.RecipeItemHolder>{

    private List<Recipe> data = new ArrayList<>();
    private Context context;
    private Fragment fragmentContext;
    private UpdateRecipeList updateRecipeList;
    private ViewClicked listener;

    public PreMadeRecipeAdapter(Context context, List<Recipe> data, Fragment fragmentContext, UpdateRecipeList updateRecipeList, ViewClicked listener) {
        this.context = context;
        this.data = data;
        this.fragmentContext = fragmentContext;
        this.updateRecipeList = updateRecipeList;
        this.listener = listener;
    }


    @Override
    public RecipeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecipeItemHolder viewHolder;

//        GridView grid = (GridView)parent;
//        int size = grid.getColumnWidth();

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_ifttt_recipe, parent, false);
//        v.setLayoutParams(new GridView.LayoutParams(size, size));
        viewHolder = new RecipeItemHolder(v);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final RecipeItemHolder holder, final int position) {

        final Recipe recipe = data.get(position);
        holder.position = position;
        if(!recipe.getTriggers().isEmpty() && !recipe.getActions().isEmpty()) {
            setIcon(recipe.getTriggers().get(0).getResId(), holder.imageTrigger);
            setIcon(recipe.getActions().get(0).getResId(), holder.imageAction);
            holder.textViewDescription.setText(recipe.getRecipeDescription());

            holder.switchActive.setOnCheckedChangeListener(null);
            holder.switchActive.setChecked(recipe.isActive());


            holder.switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateRecipeList.updateActiveRecipe(position, isChecked);


                }
            });

            if(recipe.isActive()) {

                holder.colorTrigger.setBackgroundColor(recipe.getTriggers().get(0).getColor());
                holder.colorAction.setBackgroundColor(recipe.getActions().get(0).getColor());

            }else{
                int color = Color.parseColor("#bdbdbd");
                holder.colorTrigger.setBackgroundColor(color);
                holder.colorAction.setBackgroundColor(color);
            }

            holder.recipeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(position);
                }
            });


        }

    }

    private void setIcon(int resId, ImageView image){
        if(resId != -1) {
            Glide.with(fragmentContext).load(resId)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(image);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecipeItemHolder extends RecyclerView.ViewHolder{
        ImageView imageTrigger, imageAction;
        TextView textViewDescription;
        View colorTrigger, colorAction, recipeContainer;
        Switch switchActive;
        int position;


        public RecipeItemHolder(View itemView) {
            super(itemView);
            imageTrigger = (ImageView)itemView.findViewById(R.id.imageTrigger);
            imageAction = (ImageView)itemView.findViewById(R.id.imageAction);
            textViewDescription = (TextView)itemView.findViewById(R.id.textViewDescription);
            colorTrigger = itemView.findViewById(R.id.layoutFirst);
            colorAction = itemView.findViewById(R.id.layoutSecond);
            switchActive = (Switch)itemView.findViewById(R.id.switchActive);
            recipeContainer = itemView.findViewById(R.id.card_view);

        }

    }



    public interface UpdateRecipeList{
        void updateActiveRecipe(int position, boolean active);
    }




}
