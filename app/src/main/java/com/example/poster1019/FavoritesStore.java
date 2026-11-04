package com.example.poster1019;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Saves favorite poster IDs in SharedPreferences so they survive app restarts. */
public class FavoritesStore {
    private static final String PREFS_NAME = "favorites";
    private static final String KEY_IDS = "ids";
    private static final String SEPARATOR = ",";

    private final SharedPreferences preferences;

    // In-memory copy keeps reads fast while SharedPreferences provides persistence.
    private final List<String> ids;


    public FavoritesStore(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        ids = decode(preferences.getString(KEY_IDS,""));
    }

    /** Returns favorite IDs in insertion order without allowing callers to modify them. */
    public List<String> getIds(){
        return Collections.unmodifiableList(ids);
    }

    public int addAll(List<String> newIds){
        int added = 0;
        for (String id:newIds){
            if(!ids.contains(id)){
                ids.add(id);
                added++;
            }
        }
        if(added>0){
            save();
        }
        return added;
    }

    /** Removes one saved favorite and reports whether it was present. */
    public boolean remove(String id){
        boolean removed = ids.remove(id);
        if(removed){
            save();
        }
        return removed;
    }

    /** Removes stale IDs when the app's available poster catalog changes. */
    public void retainOnly(Collection<String> validIds){
        if(ids.retainAll(validIds)){
            save();
        }
    }

    private void save(){
        // apply() persists the change asynchronously without blocking the UI thread.
        preferences.edit().putString(KEY_IDS,encode(ids)).apply();
    }

    static String encode(List<String> ids){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<ids.size();i++){
            if (i > 0) {
                builder.append(SEPARATOR);
            }
            builder.append(ids.get(i));
        }
        return builder.toString();
    }

    static List<String> decode(String saved){
        // Ignore empty and duplicate values from older or malformed saved data.
        List<String> result = new ArrayList<>();
        if (saved == null || saved.isEmpty()){
            return result;
        }
        for(String id: saved.split(SEPARATOR)){
            if(!id.isEmpty() && !result.contains(id)){
                result.add(id);
            }
        }
        return result;
    }
}
