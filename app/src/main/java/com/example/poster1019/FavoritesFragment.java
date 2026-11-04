package com.example.poster1019;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesFragment extends Fragment implements PosterRepository.FavoritesListener, PostersAdapter.OnRemoveFavoriteListener {

    private PosterRepository posterRepository;
    private PostersAdapter favoritesAdapter;
    private TextView textFavoritesCount;
    private RecyclerView favoritesRecyclerView;
    private View layoutFavoritesEmpty;

    /** Creates the Favorite List page. */
    public FavoritesFragment(){
        super(R.layout.fragment_favorites);
    }

    /** Sets up the Favorite List and subscribes it to saved-favorite changes. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textFavoritesCount = view.findViewById(R.id.textFavoritesCount);
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        // This vertical layout manager makes a long Favorite List scroll up and down.
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        layoutFavoritesEmpty = view.findViewById(R.id.layoutFavoritesEmpty);

        posterRepository = PosterRepository.getInstance(requireContext());

        // "this" handles the remove button (onRemoveFavorite below).
        favoritesAdapter = new PostersAdapter(posterRepository.getFavorites(), this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Listen for changes. MainActivity only hides this page, so it stays up to date
        // even while another page is on screen.
        posterRepository.addFavoritesListener(this);

        refresh();

    }

    @Override
    public void onDestroyView() {
        // Stop receiving updates after the fragment's views have been released.
        posterRepository.removeFavoritesListener(this);
        super.onDestroyView();
    }

    /** Reloads saved favorites and switches between the list and empty-state views. */
    private void refresh() {
        List<Posters> favorites = posterRepository.getFavorites();
        favoritesAdapter.setPosters(favorites);

        int count = favorites.size();
        textFavoritesCount.setText(getResources().getQuantityString(R.plurals.favorites_count, count, count));

        boolean isEmpty = favorites.isEmpty();
        textFavoritesCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        favoritesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutFavoritesEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFavoritesChanged() {
        // A selection on the Main Page or removal here can change this list.
        refresh();

    }

    @Override
    public void onRemoveFavorite(Posters poster) {
        // The repository updates storage and notifies every interested screen.
        posterRepository.removeFavorite(poster.id);
    }
}
