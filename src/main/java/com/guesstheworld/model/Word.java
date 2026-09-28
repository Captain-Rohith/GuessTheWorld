package com.guesstheworld.model;

import java.time.LocalDateTime;

public class Word {
    private Long id;
    private String word;
    private boolean active;
    private LocalDateTime createdAt;

    public Word() {
    }

    public Word(Long id, String word, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.word = word != null ? word.toUpperCase() : null;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word != null ? word.toUpperCase() : null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
