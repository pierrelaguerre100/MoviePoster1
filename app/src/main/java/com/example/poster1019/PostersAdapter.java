package com.example.poster1019;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.PostersViewHolder> {

    //  The same adapter and card layout are used on two pages.
    // SELECT   = Main Page: tap a card to select it (orange border + checkmark).
    // FAVORITE = Favorite List: cards can't be selected; each has a remove button.
    public enum Mode { SELECT, FAVORITE }

    //  Called when the remove button on a Favorite List card is tapped.
    public interface OnRemoveFavoriteListener {
        void onRemoveFavorite(Posters poster);
    }

    // Which page this adapter is for, and who handles "remove".
    private Mode mode = Mode.SELECT;
    private OnRemoveFavoriteListener removeFavoriteListener;

    public PostersAdapter(List<Posters> posters, OnRemoveFavoriteListener removeFavoriteListener) {
        this.posters = posters;
        this.mode = Mode.FAVORITE;
        this.removeFavoriteListener = removeFavoriteListener;
    }

    /** Replaces Favorite List data after the repository reports a saved-data change. */
    public void setPosters(List<Posters> posters) {
        this.posters = posters;
        notifyDataSetChanged();
    }

    private List<Posters> posters;
    private posterListener posterListener;

    public PostersAdapter(List<Posters>posters,posterListener posterListener){
        this.posters = posters;
        this.posterListener = posterListener;
    }

    @NonNull
    @Override
    public PostersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostersViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_poster,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull PostersAdapter.PostersViewHolder holder, int position) {
        holder.bindPosters(posters.get(position));

    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    /** Returns cards selected on the Main Page so they can be added as favorites. */
    public List<Posters> getSelectedPosters(){
        List<Posters> selectedPosters = new ArrayList<>();
        for (Posters poster : posters){
            if (poster.isSelected){
                selectedPosters.add(poster);
            }
        }
        return selectedPosters;
    }

    class PostersViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layoutPosters;
        View viewbackground;
        RoundedImageView imagePosters;
        TextView textName,textCreatedBy, textStory;
        RatingBar ratingBar;
        ImageView imageSelected;
        ImageView imageRemove;

        public PostersViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPosters = itemView.findViewById(R.id.layoutPosters);
            viewbackground = itemView.findViewById(R.id.viewBackground);
            imagePosters = itemView.findViewById(R.id.imagePosters);
            textName = itemView.findViewById(R.id.textName);
            textCreatedBy = itemView.findViewById(R.id.textCreateBy);
            textStory = itemView.findViewById(R.id.textStory);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageSelected = itemView.findViewById(R.id.imageSelected);
            imageRemove = itemView.findViewById(R.id.imageRemove);
        }

        void bindPosters(final Posters posters){
            imagePosters.setImageResource(posters.image);
            textName.setText(posters.name);
            textStory.setText(posters.story);
            textCreatedBy.setText(posters.createdBy);
            ratingBar.setRating(posters.rating);

            if(mode == Mode.FAVORITE){
                // Favorite List cards have one action: remove this saved favorite.
                bindFavorite(posters);
                return;
            }
            imageRemove.setVisibility(View.GONE);

            // Restore selection styling whenever a Main Page card is rebound.
            if(posters.isSelected){
                viewbackground.setBackgroundResource(R.drawable.poster_selected_background);
                imageSelected.setVisibility(View.VISIBLE);

            }else{
                viewbackground.setBackgroundResource(R.drawable.poster_background);
                imageSelected.setVisibility(View.GONE);
            }

            layoutPosters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(posters.isSelected){
                        viewbackground.setBackgroundResource(R.drawable.poster_background);
                        imageSelected.setVisibility(View.GONE);
                        posters.isSelected =false;
                        if(getSelectedPosters().isEmpty()){
                        posterListener.OnPosterAction(false);}
                    }else{
                        viewbackground.setBackgroundResource(R.drawable.poster_selected_background);
                        imageSelected.setVisibility(View.VISIBLE);
                        posters.isSelected =true;
                        posterListener.OnPosterAction(true);
                    }
                }
            });

        }

        // A Favorite List card: always the plain background and no checkmark (the
        // poster may still be selected on the Main Page, but that doesn't apply here), no
        // tap action, and a remove button that reports which poster to remove.
        private void bindFavorite(final Posters posters){
            viewbackground.setBackgroundResource(R.drawable.poster_background);
            imageSelected.setVisibility(View.GONE);
            layoutPosters.setOnClickListener(null);
            layoutPosters.setClickable(false);

            imageRemove.setVisibility(View.VISIBLE);
            imageRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFavoriteListener.onRemoveFavorite(posters);
                }
            });
        }
    }
}
