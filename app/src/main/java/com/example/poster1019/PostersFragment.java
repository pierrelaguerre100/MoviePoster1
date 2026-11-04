package com.example.poster1019;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostersFragment extends Fragment implements posterListener {

    private Button buttonAddtoWatchList;

    private PosterRepository posterRepository;

    public PostersFragment(){
        super(R.layout.fragment_posters);
    }

    /** Configures the poster picker and adds selected posters to persistent favorites. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up the scrolling catalog and keep the bulk-action button hidden initially.
        RecyclerView posterRecyclerView = view.findViewById(R.id.posTersRecyclerView);
        // This vertical layout manager makes the Main Page poster cards scroll up and down.
        posterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        buttonAddtoWatchList  = view.findViewById(R.id.buttonAddToWatchlist);

        // The shared repository keeps catalog, selections, and favorites consistent across pages.
        posterRepository = PosterRepository.getInstance(requireContext());
        List<Posters> posters = posterRepository.getAllPosters();

        // This adapter allows cards to be selected for the bulk Add To Watchlist action.
        final PostersAdapter postersAdapter = new PostersAdapter(posters, this);
//        final PostersAdapter postersAdapter = new PostersAdapter(posters, (posterListener) this);
        posterRecyclerView.setAdapter(postersAdapter);

        // Selection state lives in the repository, so restore the button after rotation.
        OnPosterAction(!postersAdapter.getSelectedPosters().isEmpty());

        // Save all selected cards, then reset the picker for the next selection.
        buttonAddtoWatchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Posters> selectPosters = postersAdapter.getSelectedPosters();

                int added = posterRepository.addFavorites(selectPosters);

                posterRepository.clearSelection();
                postersAdapter.notifyDataSetChanged();
                OnPosterAction(false);

                String message;
                if(added>0){
                    message = getResources().getQuantityString(R.plurals.favorites_added,added,added);
                }else {
                    message = getString(R.string.favorites_already_added);
                }
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnPosterAction(Boolean isSelected) {
        if(isSelected){
            buttonAddtoWatchList.setVisibility(View.VISIBLE);
        }else{
            buttonAddtoWatchList.setVisibility(View.GONE);
        }

    }
}
