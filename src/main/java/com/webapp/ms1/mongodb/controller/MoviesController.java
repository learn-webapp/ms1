package com.webapp.ms1.mongodb.controller;

import com.webapp.ms1.mongodb.entity.EmbeddedMovies;
import com.webapp.ms1.mongodb.service.MoviesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MoviesController {

    private final MoviesService moviesService;

    @GetMapping("/{title}")
    public ResponseEntity<List<EmbeddedMovies>> getMovieByTitle(@PathVariable String title) {
        List<EmbeddedMovies> movies = moviesService.getByTitle(title);
        return ResponseEntity.ok(movies);
    }
}
