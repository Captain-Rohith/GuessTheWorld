package com.guesstheworld.service;

import com.guesstheworld.model.Word;
import com.guesstheworld.repository.WordRepository;

import java.util.List;
import java.util.Optional;

public class WordService {

    private final WordRepository wordRepository;
    private final ValidationService validationService;

    public WordService(WordRepository wordRepository, ValidationService validationService) {
        this.wordRepository = wordRepository;
        this.validationService = validationService;
    }

    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    public List<Word> getAllActiveWords() {
        return wordRepository.findAllActive();
    }

    public Word addWord(String wordText) {
        ValidationResult val = validationService.validateWord(wordText);
        if (!val.isValid()) {
            throw new IllegalArgumentException(val.getFirstError());
        }

        String uppercaseWord = wordText.trim().toUpperCase();
        Optional<Word> existing = wordRepository.findByWord(uppercaseWord);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Word '" + uppercaseWord + "' already exists in the database.");
        }

        Word newWord = new Word(null, uppercaseWord, true, null);
        return wordRepository.save(newWord);
    }

    public boolean deleteWord(Long id) {
        return wordRepository.deleteById(id);
    }

    public boolean toggleWordStatus(Long id, boolean active) {
        return wordRepository.updateStatus(id, active);
    }

    public Optional<Word> getRandomWord() {
        return wordRepository.getRandomActiveWord();
    }

    public int getWordCount() {
        return wordRepository.count();
    }
}
