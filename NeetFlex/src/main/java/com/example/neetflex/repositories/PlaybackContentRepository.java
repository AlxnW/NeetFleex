package com.example.neetflex.repositories;

import com.example.neetflex.model.user.Playback;
import com.example.neetflex.model.user.PlaybackContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PlaybackContentRepository extends JpaRepository<PlaybackContent, Long> {


    Optional<PlaybackContent> findByPlaybackAndContentId(Playback playback, Long contentId);



    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM User u " +
            "JOIN u.watchlist w " +
            "JOIN w.contents c " +
            "WHERE u.username = :userName AND c.contentId = :contentId")
    boolean isContentInUserWatchlist(@Param("userName") String userName,



                                     @Param("contentId") Long contentId);


    boolean existsByPlaybackIdAndContentId(Long playbackId, Long id);
    @Modifying
    @Transactional
    @Query("DELETE FROM PlaybackContent pb " +
            "WHERE pb.playback.id = :playbackId " +
            "  AND pb.contentId  = :contentId")
    int deleteByWatchlistAndContent(
            @Param("playbackId") Long playbackId,
            @Param("contentId")   Long contentId
    );
}