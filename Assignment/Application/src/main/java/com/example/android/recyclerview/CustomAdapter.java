

package com.example.android.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.recyclerview.modals.UserSearch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 * This adapter can inflate different types of views according to different abstractions
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static final int IMAGE = 1;
    private static final long FADE_DURATION = 2000;
    /**
     * providing different abstractions for maintainence
     */
    private ArrayList<UserSearch> mDataSet;

    public CustomAdapter(ArrayList<UserSearch> dataSet) {
        mDataSet = dataSet;
    }

    RecyclerView.ViewHolder viewholder;
    View view;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        switch (viewType) {
            case IMAGE:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_item, viewGroup, false);
//                RecyclerView.LayoutParams lp= (RecyclerView.LayoutParams) view.getLayoutParams();
//                lp.width=view.getResources().getDisplayMetrics().widthPixels;
//                lp.height=view.getResources().getDisplayMetrics().heightPixels/6;
//                view.setLayoutParams(lp);
                viewholder = new UserSearchHolder(view);
                break;

        }

        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                UserSearchHolder viewholder1 = (UserSearchHolder) viewHolder;
                viewholder1.main_label.setText(mDataSet.get(position).getTitle());
                if (mDataSet.get(position).getSource() != null)
                    Picasso.with(view.getContext()).load(mDataSet.get(position).getSource()).into(viewholder1.main);
                else
                    viewholder1.main.setBackgroundResource(R.drawable.ic_launcher);
                setFadeAnimation(viewholder1.main);
                break;
        }

    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        TranslateAnimation animation = new TranslateAnimation(-20, 0, 0, 0);
        anim.setDuration(FADE_DURATION * 2
        );
        animation.setDuration(FADE_DURATION);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(anim);
        animationSet.addAnimation(animation);
        view.startAnimation(animationSet);
//        view.animate().translationX(10).alpha(1).setDuration(FADE_DURATION).start();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getTemplate().equals(UserSearch.TEMPLATE_TYPE_1))
            return IMAGE;
        return super.getItemViewType(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setResult(ArrayList<UserSearch> result) {
        this.mDataSet = result;
    }

    private static class UserSearchHolder extends RecyclerView.ViewHolder {
        ImageView main;
        TextView main_label;

        public UserSearchHolder(View itemView) {
            super(itemView);
            main = (ImageView) itemView.findViewById(R.id.main);
            main_label = (TextView) itemView.findViewById(R.id.main_label);
        }
    }


}
