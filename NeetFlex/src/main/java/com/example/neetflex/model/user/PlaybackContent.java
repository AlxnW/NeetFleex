package com.example.neetflex.model.user;

import com.example.neetflex.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = "playback")
public class PlaybackContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playback_content_seq")
    @SequenceGenerator(name = "playback_content_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playback_id")
    @JsonIgnore
    private Playback playback;

    private Long contentId; // This ID will come from either Movie or Series

    @Enumerated(EnumType.STRING)
    private ContentType contentType;


}
