package com.example.neetflex.model.user;

import com.example.neetflex.enums.ContentType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "watchlist_contents")
public class WatchlistContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "watchlist_content_seq")
    @SequenceGenerator(name = "watchlist_content_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "watchlist_id")
    private Watchlist watchlist;
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;


}




