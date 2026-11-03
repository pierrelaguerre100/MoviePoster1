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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //find the list and the button
        RecyclerView posterRecyclerView = view.findViewById(R.id.posTersRecyclerView);
        posterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        buttonAddtoWatchList  = view.findViewById(R.id.buttonAddToWatchlist);

        //the posters come from the shared repository
        posterRepository = PosterRepository.getInstance(requireContext());
        List<Posters> posters = posterRepository.getAllPosters();

        //connect the list to its adapter
        final PostersAdapter postersAdapter = new PostersAdapter(posters, this);
//        final PostersAdapter postersAdapter = new PostersAdapter(posters, (posterListener) this);
        posterRecyclerView.setAdapter(postersAdapter);

        //selections live in the repository and survive rotation, so button show up again
        OnPosterAction(!postersAdapter.getSelectedPosters().isEmpty());

        //show the selected names
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
