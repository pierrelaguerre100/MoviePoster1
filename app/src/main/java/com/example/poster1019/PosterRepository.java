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

    /** Clears Main Page selections after they have been added to the Favorite List. */
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    /** Resolves saved favorite IDs into poster cards, preserving the order they were added. */
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
        // Copy first so listeners can safely unregister while receiving the update.
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    /** Creates the five movie cards displayed on the Main Page. */
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        posters.add(createPoster("inception", R.drawable.image_01_inception,
                "Inception", 4.5f, "Christopher Nolan",
                "A skilled extractor enters dreams to steal secrets and is offered one final job: plant an idea instead."));

        posters.add(createPoster("titanic", R.drawable.image_02_titanic,
                "Titanic", 4.5f, "James Cameron",
                "A young aristocrat and a free-spirited artist fall in love aboard the doomed RMS Titanic."));

        posters.add(createPoster("the_avengers", R.drawable.image_03_the_avengers,
                "The Avengers", 4.5f, "Joss Whedon",
                "Earth's mightiest heroes join forces to stop Loki and an alien invasion from conquering the world."));

        posters.add(createPoster("interstellar", R.drawable.image_04_interstellar,
                "Interstellar", 5f, "Christopher Nolan",
                "Explorers travel through a wormhole in search of a new home for humanity as Earth becomes uninhabitable."));

        posters.add(createPoster("the_return_of_the_king", R.drawable.image_05_return_of_the_king,
                "The Lord of the Rings: The Return of the King", 5f, "Peter Jackson",
                "Frodo and Sam make their final journey to Mount Doom while Aragorn leads the free peoples against Sauron."));

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
