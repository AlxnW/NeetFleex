package com.example.neetflex.repositories;

import com.example.neetflex.model.user.WatchlistContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WatchlistContentRepository extends JpaRepository<WatchlistContent, Long> {






    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM User u " +
            "JOIN u.watchlist w " +
            "JOIN w.contents c " +
            "WHERE u.username = :userName AND c.contentId = :contentId")
    boolean isContentInUserWatchlist(@Param("userName") String userName,
                                     @Param("contentId") Long contentId);

    boolean existsByWatchlistIdAndContentId(Long playbackId, Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM WatchlistContent wc " +
            "WHERE wc.watchlist.id = :watchlistId " +
            "  AND wc.contentId  = :contentId")
    int deleteByWatchlistIdAndContentId(
            @Param("watchlistId") Long watchlistId,
            @Param("contentId")   Long contentId
    );
}