package ru.codecrafts.blackjackengine;

import java.util.Collections;
import java.util.List;

// Player can only participate actively in one GameSession
public class Player {
    protected GameSession session_;
    protected final String playerID_;
    protected Round currentRound_;
    protected Deck deck_;

    private Player(GameSession session, String playerID) {
        this.session_ = session;
        deck_ = session_.deck();
        playerID_ = playerID;
        currentRound_ = session_.currentRound();
}
    public static Player create(GameSession session, String playerName) {
        return new Player(session, playerName);
    }

    // Method to test round state
    // The previous round can still be in progress
    public boolean canMakeNewBet() {
        return currentRound_.playerPosition(this) == null || currentRound_.playerPosition(this).finished();
    }

    // The player is the creator of the round
    public void makeNewBet(int betValue) throws IllegalStateException {
        if (!canMakeNewBet())
            throw new IllegalStateException("You can't make new bet. Current round state: " + currentRound_.state());

        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.newBet, betValue) );
    }

    // Method to test round state
    // Other players can be not ready
    public boolean canMakeMove() {
        return currentRound_.playerPosition(this) != null || currentRound_.playerPosition(this).ready();
    }

    public Round.PlayerPosition position() { return currentRound_.playerPosition(this); }

    public List<Card> hand() { return Collections.unmodifiableList(position().hand()); }
    public List<Card> splitHand() { return Collections.unmodifiableList(position().splitHand()); }
    public int currentBet() { return this.currentRound_.playerPosition(this).bet(); }

    // Take another card from the dealer
    public void hit() throws IllegalStateException {
        if ( !canMakeMove() )
            throw new IllegalStateException("You can't make move. Current round state: " + currentRound_.state());
        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.hitAction) );
    }

    // Take no more cards
    public void stand() throws IllegalStateException {
        if ( !canMakeMove() )
            throw new IllegalStateException("You can't make move. Current round state: " + currentRound_.state());
        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.standAction) );
    }

    // The player is allowed to increase the initial bet by up to 100% in exchange for committing to stand after receiving exactly one more card.
    public void doubleDown() throws IllegalStateException {
        if ( !canMakeMove() )
            throw new IllegalStateException("You can't make move. Current round state: " + currentRound_.state());
        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.doubleDownAction) );
    }

    public boolean canSplit() { return session_.gameSettings().canAlwaysSplit() || currentRound_.playerPosition(this).hasSplit(); }

    // If the first two cards of a hand have the same value, the player can split them into two hands, by moving a second bet equal to the first into an area outside the betting box.
    public void split() throws IllegalStateException {
        if ( !canMakeMove() || !canSplit() )
            throw new IllegalStateException("You can't make move. Current round state: " + currentRound_.state());
        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.splitAction) );
    }

    // TODO: Player can not surrender if he doubled his bet
    public boolean canSurrender() { return  true; }

    // Some games offer the option to "surrender" directly after the dealer has checked for blackjack (see below for variations).
    public void surrender() throws IllegalStateException {
        if ( !canMakeMove() )
            throw new IllegalStateException("You can't make move. Current round state: " + currentRound_.state());
        currentRound_.applyEvent( Round.Event.create(this, Round.EventType.surrenderAction) );
    }

    public Round.State roundState() { return  this.currentRound_.state(); }
    public Round.PlayerState state() {  return this.currentRound_.playerPosition(this).state(); }
    public int roundDelta() { return this.currentRound_.playerPosition(this).delta(); }

    public String name() { return playerID_; }

    // Object methods implementation

    @Override
    public String toString() { return playerID_; }

    @Override
    public int hashCode() { return  playerID_.hashCode(); }

    @Override
    public boolean equals(Object p) {
        if (p == this)
            return true;

        // No need to check for null explicitly as instanceof will return false if p is null
        if (p instanceof Player == false)
            return false;

        Player other = (Player) p;
        return this.playerID_ == other.playerID_;
    }


}
