package com.webapp.ms1.mongodb.service;

import com.webapp.ms1.mongodb.entity.EmbeddedMovies;
import com.webapp.ms1.mongodb.repository.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoviesService {

    // write methods here to test the methods of MoviesRepository class
    @Autowired
    private MoviesRepository moviesRepository;

    public EmbeddedMovies saveMovie(EmbeddedMovies movie) {
        return moviesRepository.save(movie);
    }

    public List<EmbeddedMovies> getAllMovies() {
        return moviesRepository.findAll();
    }

    public Optional<EmbeddedMovies> getMovieById(String id) {
        return moviesRepository.findById(id);
    }

    public List<EmbeddedMovies> getByTitle(String title) {
        long millis_start = System.currentTimeMillis();
        List<EmbeddedMovies> movies = moviesRepository.findByTitle(title);
        long millis_end = System.currentTimeMillis();
        System.out.println("Time taken to fetch by title: " + (millis_end - millis_start) + " ms");
        return movies;
    }

}
