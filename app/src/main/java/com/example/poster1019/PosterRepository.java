package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    /** Creates the five original movie cards displayed on the Main Page. */
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        posters.add(createPoster("starlight_journey", R.drawable.poster_starlight_journey,
                "Starlight Journey", 4.5f, "Maya Carter",
                "A young pilot follows a mysterious signal beyond the last mapped star."));

        posters.add(createPoster("midnight_kitchen", R.drawable.poster_midnight_kitchen,
                "The Midnight Kitchen", 4f, "Jordan Lee",
                "Three friends rediscover their neighborhood through late-night recipes."));

        posters.add(createPoster("rainy_platform", R.drawable.poster_rainy_platform,
                "Rainy Platform", 4.5f, "Elena Brooks",
                "A missed train turns into an unexpected conversation and a new beginning."));

        posters.add(createPoster("paper_airplanes", R.drawable.poster_paper_airplanes,
                "Paper Airplanes", 5f, "Noah Bennett",
                "A teen builds a flying club that brings a divided school together."));
        posters.add(createPoster("orchard_summer", R.drawable.poster_orchard_summer,
                "Orchard Summer", 4f, "Sofia Martinez",
                "A city student spends one unforgettable summer helping at a family orchard."));

        return posters;

    }

    /** Builds one poster object so every movie has the same complete set of details. */
    private static Posters createPoster(String id, int image, String name, float rating,
                                        String director, String story) {
        Posters poster = new Posters();
        poster.id = id;
        poster.image = image;
        poster.name = name;
        poster.rating = rating;
        poster.createdBy = director;
        poster.story = story;
        return poster;
    }


}
