package ru.codecrafts.blackjackengine;

import java.util.HashSet;

// GameSession is more like a table in the casino or the virtual room where player reside
public class GameSession {
    private final String sessionID_;
    private final GameSettings settings_;

    private final HashSet<Player> players_;
    private final Round currentRound_;
    private final Deck deck_;

    private GameSession(GameSettings settings) {
        sessionID_ = "testSession";
        players_ = new HashSet<Player>();
        settings_ = settings;
        deck_ = Deck.create(settings.type());
        currentRound_ = Round.create(deck_);
    }

    public static GameSession create(GameSettings settings) {
        return new GameSession(settings);
    }

    public Player createPlayer(String playerName) {
        Player newPlayer = Player.create(this, playerName);
        players_.add(newPlayer);
        return  newPlayer;
    }

    public boolean unregisterPlayer(Player player) {
        return  players_.remove(player);
    }

    public Round currentRound() { return currentRound_; }

    public GameSettings gameSettings() { return settings_; }

    Deck deck() { return deck_; }

    @Override
    public String toString() { return sessionID_; }

    @Override
    public int hashCode() {
        return sessionID_.hashCode();
    }

}
