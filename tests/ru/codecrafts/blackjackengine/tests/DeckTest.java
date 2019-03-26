package ru.codecrafts.blackjackengine.tests;

import org.junit.jupiter.api.Test;
import ru.codecrafts.blackjackengine.Deck;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Created by Ctrl + Shift + T
// The other option is to download from central maven repository
class DeckUnitTest {
    @Test
    public void testDeckShuffle() {
        Deck newDeck = Deck.create(Deck.Type.DoubleFiftyTwo);
        String oldDeckState = newDeck.toString();
        int  oldDeckCount = newDeck.cardCount();

        newDeck.shuffle();
        String newDeckState = newDeck.toString();
        int newDeckCount = newDeck.cardCount();

        assertTrue(oldDeckCount == newDeckCount);
        assertTrue(!oldDeckState.equals(newDeckState));
    }
}