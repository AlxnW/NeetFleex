package com.example.neetflex.service.impl;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.dto.ContentUpdateDTO;
import com.example.neetflex.dto.response.RequestDetails;
import com.example.neetflex.model.contents.Movie;
import com.example.neetflex.model.user.*;
import com.example.neetflex.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.neetflex.enums.ContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




@Data
@Service
@Transactional
public class ContentService {

    private final SeriesRepository seriesRepository;
    private final MovieRepository movieRepository;
    private ObjectMapperDto objectMapperDto;
    private PlaybackRepository playbackRepository;
    private UserService userService;
    private WatchlistContentRepository watchlistContentRepository;
    private PlaybackContentRepository playbackContentRepository;


    @Autowired
    public ContentService(SeriesRepository seriesRepository, MovieRepository movieRepository, ObjectMapperDto objectMapperDto, UserService userService, WatchlistContentRepository watchlistRepository, PlaybackRepository playbackRepository, PlaybackContentRepository playbackContentRepository ) {
        this.seriesRepository = seriesRepository;
        this.movieRepository = movieRepository;
        this.objectMapperDto = objectMapperDto;
        this.userService = userService;
        this.watchlistContentRepository = watchlistRepository;
        this.playbackRepository = playbackRepository;
        this.playbackContentRepository = playbackContentRepository;

    }




    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public List<ContentResponseDTO> getAllPopularMovies() {


        return movieRepository.findPopularMovies().stream()
                .map(ObjectMapperDto::toResponseDTO)
                .collect(Collectors.toList());
    }


    public List<ContentResponseDTO> getAllPopularSeries() {
        return seriesRepository.findPopularSeries().stream()
                .map(ObjectMapperDto::toResponseDTO)
                .collect(Collectors.toList());
    }


    public List<ContentResponseDTO> getAllContentFromPlayback(String userName) {
        Playback playback = userService.getPlaybackByName(userName);
        List<ContentResponseDTO> contentDTOList = new ArrayList<>();

        for (PlaybackContent content : playback.getContents()) {
            if (content.getContentType() == ContentType.MOVIE) {
                movieRepository.findById(content.getContentId())
                        .ifPresent(movie -> contentDTOList.add(ObjectMapperDto.toResponseDTO(movie)));
            } else if (content.getContentType() == ContentType.SERIES) {
                seriesRepository.findById(content.getContentId())
                        .ifPresent(series -> contentDTOList.add(ObjectMapperDto.toResponseDTO(series)));
            }
        }

        return contentDTOList;
    }


    public List<ContentResponseDTO> getAllContentFromUserList(String userName) {
        Watchlist watchlist = userService.getWatchListByName(userName);
        List<ContentResponseDTO> contentDTOList = new ArrayList<>();

        for (WatchlistContent content : watchlist.getContents()) {
            if (content.getContentType() == ContentType.MOVIE) {
                movieRepository.findById(content.getContentId())
                        .ifPresent(movie -> contentDTOList.add(ObjectMapperDto.toResponseDTO(movie)));
            } else if (content.getContentType() == ContentType.SERIES) {
                seriesRepository.findById(content.getContentId())
                        .ifPresent(series -> contentDTOList.add(ObjectMapperDto.toResponseDTO(series)));
            }
        }

        return contentDTOList;
    }


    public void addContentToWatchlist(String contentName, String userName, ContentType type) {
        try {
            Watchlist watchlist = userService.getWatchListByName(userName);
            if (watchlist == null) {
                Optional<User> currentUser = userService.findByUsername(userName);
                if(currentUser.isPresent()) {
                    watchlist = new Watchlist();
                    User user = currentUser.get();
                    user.setWatchlist(watchlist);
                    userService.save(user);
                    // Refresh watchlist reference after save
                    watchlist = userService.getWatchListByName(userName);
                } else {
                    throw new IllegalArgumentException("User not found: " + userName);
                }
            }
            ContentUpdateDTO contentDTO = getContentByNameAndType(contentName, type);
            if (contentDTO == null) {
                throw new IllegalArgumentException("Content not found: " + contentName + " (" + type + ")");
            }

            boolean exists = watchlistContentRepository.existsByWatchlistIdAndContentId(watchlist.getId(), contentDTO.getId());

            if (exists) {
                System.out.println("Content already exists: " + contentName + " (" + type + ")");
                return;
            }

            WatchlistContent watchlistContent = new WatchlistContent();
            watchlistContent.setContentId(contentDTO.getId());
            watchlistContent.setContentType(contentDTO.getType());
            watchlistContent.setWatchlist(watchlist);

            watchlistContentRepository.save(watchlistContent);

            System.out.println("[Service: Watchlist] Added content '" + contentDTO.getTitle() + "' to watchlist for user " + userName);
        } catch (Exception e) {
            System.err.println("[Service: Watchlist] Failed to add content to watchlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ContentUpdateDTO getContentByNameAndType(String contentName, ContentType type) {
        if (type == ContentType.MOVIE) {
            return movieRepository.findByTitle(contentName)
                    .map(ObjectMapperDto::toUpdateDTO)
                    .orElse(null);
        }

        if (type == ContentType.SERIES) {
            return seriesRepository.findByTitle(contentName)
                    .map(ObjectMapperDto::toUpdateDTO)
                    .orElse(null);
        }

        return null;
    }


    public Boolean checkIfContentExistsInWatchlist(RequestDetails request) {
        ContentUpdateDTO contentUpdateDTO = getContentByNameAndType(request.getContentName(), request.getType());
        if (contentUpdateDTO != null) {
            return watchlistContentRepository.isContentInUserWatchlist(request.getUserName(), contentUpdateDTO.getId());
        }
        else return false;

    }

    public Boolean checkIfContentExistsInPlaylist(RequestDetails request) {
        ContentUpdateDTO contentUpdateDTO = getContentByNameAndType(request.getContentName(), request.getType());
        if (contentUpdateDTO != null) {
            return playbackRepository.isContentInUserPlaylist(request.getUserName(), contentUpdateDTO.getId());
        }
        else return false;
    }

    public void addContentToPlaylist(String contentName, String userName, ContentType contentType) {

        try {
            Playback playback = userService.getPlaybackByName(userName);
            if (playback == null) {
                Optional<User> currentUser = userService.findByUsername(userName);
                if(currentUser.isPresent()) {
                    playback = new Playback();
                    User user = currentUser.get();
                    user.setPlayback(playback);
                    userService.save(user);
                    // Refresh playback reference after save
                    playback = userService.getPlaybackByName(userName);
                } else {
                    throw new IllegalArgumentException("User not found: " + userName);
                }
            }


            ContentUpdateDTO contentDTO = getContentByNameAndType(contentName, contentType);
            if (contentDTO == null) {
                throw new IllegalArgumentException("Content not found: " + contentName + " (" + contentType + ")");
            }

            // Check if this content is already in the playback using IDs
            boolean exists = playbackContentRepository.existsByPlaybackIdAndContentId(playback.getId(), contentDTO.getId());

            if (exists) {
                System.out.println("[Service: Playback] Content '" + contentDTO.getTitle() + "' already in playback for user " + userName);
                return;
            }

            // Create new playback content with direct ID reference
            PlaybackContent newplayback = new PlaybackContent();
            newplayback.setContentId(contentDTO.getId());
            newplayback.setContentType(contentDTO.getType());

            // Use the existing playback entity
            newplayback.setPlayback(playback);

            playbackContentRepository.save(newplayback);

            System.out.println("[Service: Playback] Added content '" + contentDTO.getTitle() + "' to playback for user " + userName);
        } catch (Exception e) {
            System.err.println("[Service: Playback] Failed to add content to playback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeContentFromPlaylist(String contentName, String userName, ContentType contentType) {
        try {
            Playback playback = userService.getPlaybackByName(userName);
            if (playback == null) {
                throw new IllegalArgumentException("Playback not found for user: " + userName);
            }


            ContentUpdateDTO contentDTO = getContentByNameAndType(contentName, contentType);
            if (contentDTO == null) {
                throw new IllegalArgumentException("Content not found: " + contentName + " (" + contentType + ")");
            }


            int result = playbackContentRepository.deleteByWatchlistAndContent( playback.getId(), contentDTO.getId());

            if ( result > 0) {
                System.out.println("Content '" + contentDTO.getTitle() + "' removed from playback for user " + userName);
            }
            else {
                System.out.println("The content '" + contentDTO.getTitle() + "' was not removed from the playback for user " + userName);
            }

            System.out.println("[Service: Playback] Removed content '" + contentDTO.getTitle() + "' to playback for user " + userName);
        } catch (Exception e) {
            System.err.println("[Service: Playback] Failed to remove content to playback: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void removeContentFromWatchlist(String contentName, String userName, ContentType contentType) {
        try {
            Watchlist watchlist = userService.getWatchListByName(userName);
            if (watchlist == null) {
                throw new IllegalArgumentException("Watchlist not found for user: " + userName);
            }

            ContentUpdateDTO contentDTO = getContentByNameAndType(contentName, contentType);
            if (contentDTO == null) {
                throw new IllegalArgumentException("Content not found: " + contentName + " (" + contentType + ")");
            }


            int result = watchlistContentRepository.deleteByWatchlistIdAndContentId( watchlist.getId(),  contentDTO.getId());

            if (result > 0) {
                System.out.println("Content '" + contentDTO.getTitle() + "' removed from watchlist for user " + userName);

            }
            else {
                System.out.println("Content '" + contentDTO.getTitle() + "' was not removed from the watchlist for user " + userName);
            }


            System.out.println("[Service: ] Removed content '" + contentDTO.getTitle() + "' to watchlist for user " + userName);
        } catch (Exception e) {
            System.err.println("[Service: Watchlist] Failed to add remove to watchlist: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
