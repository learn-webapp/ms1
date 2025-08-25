package com.webapp.ms1.mongodb.repository;

import java.util.List;

import com.webapp.ms1.mongodb.entity.EmbeddedMovies;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesRepository extends MongoRepository<EmbeddedMovies, String> {

    List<EmbeddedMovies> findByTitle(String title);

    List<EmbeddedMovies> findByTitleRegexIgnoreCase(String titleRegex, Pageable pageable);

    List<EmbeddedMovies> findByYearBetween(Integer startYear, Integer endYear);

    List<EmbeddedMovies> findByGenresContaining(String genre, Pageable pageable);

    List<EmbeddedMovies> findByImdb_RatingGreaterThanEqual(Double minRating, Pageable pageable);

    List<EmbeddedMovies> findByCountriesContaining(String country, Pageable pageable);

    @Query(value = "{ 'cast': ?0 }")
    List<EmbeddedMovies> findByCastMember(String actor, Pageable pageable);

    @Query(value = "{ 'type': 'movie', 'imdb.rating': { $gte: ?0 } }",
            fields = "{ 'title': 1, 'year': 1, 'imdb': 1 }")
    List<EmbeddedMovies> findTopMoviesProjected(Double minRating, Pageable pageable);
}
