package ru.codecrafts.blackjackengine;

public class GameSettings {
    private final Deck.Type type_;
    private final int maxPlayers_;
    private final boolean canPlaytestSplit_;

    private GameSettings() {
        this(Deck.Type.DoubleFiftyTwo, 2, false);
    }

    private GameSettings(Deck.Type deckType, int maxPlayers, boolean canPlaytestSplit) {
        this.type_ = deckType;
        this.maxPlayers_ = maxPlayers;
        this.canPlaytestSplit_ = canPlaytestSplit;
    }

    public static GameSettings createDefault() { return new GameSettings(); }
    public static GameSettings createAutotestDefault() {
        GameSettings gameSettings = new GameSettings(Deck.Type.DoubleFiftyTwo, 2, true);
        return gameSettings;
    }

    public Deck.Type type() { return type_; }
    public int maxPlayers() { return maxPlayers_; }
    public boolean canAlwaysSplit() { return canPlaytestSplit_; }
}
