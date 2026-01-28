package com.example.neetflex.repositories;

import com.example.neetflex.model.user.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackRepository extends JpaRepository<Playback, Long> {
   Optional<Playback> findById(Long playbackId);

   @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM User u " +
           "JOIN u.playback w " +
           "JOIN w.contents c " +
           "WHERE u.username = :userName AND c.contentId = :contentId")
   boolean isContentInUserPlaylist(@Param("userName") String userName,
                                   @Param("contentId") Long contentId);
}
