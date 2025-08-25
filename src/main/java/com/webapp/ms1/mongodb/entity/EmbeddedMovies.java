package com.webapp.ms1.mongodb.entity;

//public class EmbeddedMovies {
//}
//
//package com.example.movieservice.model;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddedMovies {

    @Id
    private String id;

    private String plot;

    private List<String> genres;

    private Integer runtime;

    private String rated;

    private List<String> cast;

    @Field("num_mflix_comments")
    private Integer numMflixComments;

    private String poster;

//    @Indexed
    private String title;

    private String fullplot;

    private List<String> languages;

    /**
     * The sample shows released as epoch millis (including negative, pre-1970).
     * Instant maps well for that.
     */
    private Instant released;

    private List<String> directors;

    private List<String> writers;

    private Awards awards;

    /**
     * In the sample, "lastupdated" is a string with fractional seconds.
     * Keeping it as String avoids parsing quirks; you can change to Instant if you normalize data.
     */
    @Field("lastupdated")
    private String lastUpdatedRaw;

    private Integer year;

    private Imdb imdb;

    private List<String> countries;

    private String type;

    private Tomatoes tomatoes;

    /* ---------- Nested Value Objects ---------- */

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Awards {
        private Integer wins;
        private Integer nominations;
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Imdb {
        /**
         * Use Double for rating to cover decimal ratings across documents.
         */
        @Indexed
        private Double rating;
        private Integer votes;
        private Integer id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tomatoes {
        private Viewer viewer;

        /**
         * The sample shows ISO timestamps for these; map as Instant.
         */
        private Instant dvd;

        private String production;

        @Field("lastUpdated")
        private Instant lastUpdated;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Viewer {
            private Double rating;
            private Integer numReviews;
            private Integer meter;
        }
    }
}
