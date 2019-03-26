package ru.codecrafts.blackjackengine;

public class Card implements Comparable<Card> {
    private final Nominal value_;
    private final Suit suit_;
    private boolean visible_;

    public enum Suit {
        Spade('\u2660'),
        Club('\u2663'),
        Heart('\u2665'),
        Diamond('\u2666');

        private char value_;

        private Suit(char value) {
            this.value_ = value;
        }

        @Override
        public String toString() { return Character.toString(value_); }
    }

    public enum Nominal {
        Two("2", 2),
        Three("3", 3),
        Four("4", 4),
        Five("5", 5),
        Six("6", 6),
        Seven("7", 7),
        Eight("8", 8),
        Nine("9", 9),
        Ten("10", 10),
        Jack("J", 10),
        Queen("Q", 10),
        King("K", 10),
        Ace("A", 11);

        private String symbol_;
        private int value_;

        private Nominal(String symbol, int value) {
            this.symbol_ = symbol;
            this.value_ = value;
        }

        @Override
        public String toString() { return symbol_; }
    }

    public Card(Nominal value, Suit suit) {
        this.value_ = value;
        this.suit_ = suit;
        this.visible_ = false;
    }

    public Nominal value() { return value_; }
    public Suit suit() { return suit_; }
    public boolean visible() { return visible_; }
    public int score() { return value_.value_; }

    // Blackjack has special Ace rule: Ace can be either 1 or 11 depending in the position
    public boolean isAce() { return value_ == Nominal.Ace; }

    public void open() { visible_ = true; }

    @Override
    public String toString() {
        return value_.toString() + suit_.toString();
    }

    @Override
    public boolean equals(Object p) {
        if (p == this)
            return true;

        // No need to check for null explicitly as instanceof will return false if p is null
        if (p instanceof Card == false)
            return false;

        Card other = (Card) p;
        return this.value_ == other.value_ && this.suit_ == other.suit_;
    }

    @Override
    public int hashCode() {
        return suit_.hashCode() ^ value_.hashCode();
    }

    @Override
    public int compareTo(Card p) {
        if (p == this)
            return 0;

        if (p == null)
            return 0;

        return this.value_.compareTo(p.value_);
    }
}
