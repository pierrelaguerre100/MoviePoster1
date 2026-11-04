package com.example.poster1019;

public class Posters {

    // Stable ID links a poster to its persisted favorite entry.
    String id;

    // Card content shown by PostersAdapter on both movie-list pages.
    String name,createdBy,story;
    int image;
    // Main Page-only selection state; Favorite List cards do not use it.
    Boolean isSelected = false;
    float rating;
}
