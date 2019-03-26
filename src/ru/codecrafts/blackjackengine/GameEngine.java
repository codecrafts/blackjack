package ru.codecrafts.blackjackengine;

import java.util.ArrayList;

// Possible bug - calculate playerPosition before the game is finished

// TODO: Split logic: Allow split several times if the chance arises, player can take action on each hand, more unit tests
// TODO: Border conditions - State control, When there are no cards, Maximum players, etc.
// TODO: Generate random session id on creation
// TODO: Use Maven or Graddle for project build - currently the default scheme of IntelliJ IDEA is used
// TODO: IllegalStateException is unchecked exception (extends from RuntimeException)

// TODO: GameEngine - ArrayList -> Set
// TODO: New feature - Game AI
// TODO: New feature - Implement Blackjack telegram bot in Java

// GameEngine is a root object of hierarchy, implements singleton pattern, factory of game sessions
public class GameEngine {
    private final ArrayList<GameSession> sessions_;
    private static GameEngine instance_;

    private GameEngine() {
        sessions_ = new ArrayList<GameSession>();
    }

    public static GameEngine instance() {
        if (instance_ == null)
            instance_ = new GameEngine();
        return instance_;
    }

    public GameSession createSession(GameSettings settings) {
        GameSession newSession = GameSession.create(settings);
        sessions_.add(newSession);
        return newSession;
    }

    public boolean unregister(GameSession session) {
        return sessions_.remove(session);
    }
}
