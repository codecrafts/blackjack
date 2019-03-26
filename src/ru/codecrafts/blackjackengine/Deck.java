package ru.codecrafts.blackjackengine;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// A set of card objects
public class Deck {
    private final Type type_;
    private final ArrayList<Card> content_;

    private Deck(Type type) {
        this.type_ = type;
        this.content_ = new ArrayList<Card>();
    }

    // ThreadLocalRandom can be used by default instead of Random
    // No need to create additional instance of Random
    public void shuffle() {
        int roundCount = ThreadLocalRandom.current().nextInt(content_.size());

        for (int i = 0; i < roundCount; i++) {
            int curIndex = ThreadLocalRandom.current().nextInt(content_.size());
            Card c = content_.get(curIndex);
            content_.remove(curIndex);
            content_.add(c);
        }
    }

    public int cardCount() { return content_.size(); }

    public Card nextCard(boolean visible) {
        Card lastCard =  content_.get(content_.size() - 1);
        if (visible)
            lastCard.open();

        content_.remove(content_.size() - 1);
        return lastCard;
    }

    public static Deck create(Type deckType) {
        Deck newDeck = new Deck(deckType);
        if (deckType == Type.FiftyTwo) {
            newDeck.fillStandardDeck();
            newDeck.shuffle();
        }
        else if (deckType == Type.DoubleFiftyTwo) {
            newDeck.fillStandardDeck();
            newDeck.fillStandardDeck();
            newDeck.shuffle();
        }

        return newDeck;
    }

    void fillStandardDeck() {
        assert (Card.Suit.values().length * Card.Nominal.values().length == Type.FiftyTwo.cardCount() );

        int i = 0;
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Nominal val : Card.Nominal.values() ) {
                content_.add( new Card(val, suit) );
                i++;
            }
        }
    }

    public enum Type {
        Empty(0),
        FiftyTwo(52),
        DoubleFiftyTwo(104);

        private final int cardCount_;

        Type(int count) {
            this.cardCount_ = count;
        }
        public int cardCount() { return cardCount_; }
    }

    @Override
    public String toString() {
        StringBuilder newStrBuild = new StringBuilder();
        for (Card c : content_) {
            newStrBuild.append(c.toString());
            newStrBuild.append(", ");
        }
        return newStrBuild.toString();
    }
}
