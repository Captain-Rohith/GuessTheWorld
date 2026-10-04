package com.guesstheworld.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    private Long id;
    private String word;
    private boolean active;
    private LocalDateTime createdAt;
}
