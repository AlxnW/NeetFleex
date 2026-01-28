package com.example.neetflex.controller;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.dto.response.RequestDetails;

import com.example.neetflex.model.contents.Movie;
import com.example.neetflex.patterns.command.*;
import com.example.neetflex.service.impl.ContentService;
import com.example.neetflex.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ContentController {

    ContentService contentService;
    UserService userService;


    @Autowired
    public ContentController(ContentService contentService, UserService userService) {
        this.contentService = contentService;

        this.userService = userService;


    }

    @GetMapping("/get/movies")
    public List<Movie>getAllMovies() {
        return contentService.getMovies();
    }

    @GetMapping("/get/Movies")
    public List<ContentResponseDTO> getAllMoviesFromBd(){
        return contentService.getAllPopularMovies();
    }

    @GetMapping("/get/popularSeries")
    public List<ContentResponseDTO> getAllSeriesFromBd(){
        return  contentService.getAllPopularSeries();
    }

   @GetMapping("/get/myList/{userName}")
   public List<ContentResponseDTO> getAllMyListFromBd(@PathVariable String userName){
        return contentService.getAllContentFromUserList(userName);
   }

    @GetMapping("/get/playback/{userName}")
    public List<ContentResponseDTO> getPlaybackFromBd(@PathVariable String userName) {
        return contentService.getAllContentFromPlayback(userName);
    }


    @PostMapping("/addToWatchlist")
    public void addContentToWatchlist(@RequestBody RequestDetails request) {
        Command addCommand = new AddToWatchlistCommand(
                request.getContentName(),
                request.getUserName(),
                request.getType(),
                contentService
        );
        addCommand.execute();
    }


    @PostMapping("/addToPlayback")
    public void addContentToPlaylist(@RequestBody RequestDetails request) {
        Command addCommand = new AddToPlaybackCommand(
                request.getContentName(),
                request.getUserName(),
                request.getType(),
                contentService
        );
        addCommand.execute();
    }


    @PostMapping("/remove/from/Playback")
    public void removeContentFromPlaylist(@RequestBody RequestDetails request) {
        Command addCommand = new RemoveFromPlaylistCommand(
                request.getContentName(),
                request.getUserName(),
                request.getType(),
                contentService
        );
        addCommand.execute();
    }

    @PostMapping("/remove/from/Watchlist")
    public void removeContentFromWatchlist(@RequestBody RequestDetails request) {
        Command addCommand = new RemoveFromWatchlistCommand(
                request.getContentName(),
                request.getUserName(),
                request.getType(),
                contentService
        );
        addCommand.execute();
    }




    @GetMapping("/get/status/liked-button")
    public Boolean getStatusOfLikedButtonFromBd(@RequestBody RequestDetails request) {
        return contentService.checkIfContentExistsInWatchlist(request);
    }

    @GetMapping("/get/status/my-list-button")
    public Boolean getStatusOfMyListButtonFromBd(@RequestBody RequestDetails request) {
        return contentService.checkIfContentExistsInPlaylist(request);
    }



}
