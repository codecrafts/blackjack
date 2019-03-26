package ru.codecrafts.blackjackengine;

import java.util.*;

// Event-Consuming state machine that represent blackjack game logic
public class Round {

    private State state_;
    private Position dealerPosition_;
    private HashMap<Player, PlayerPosition> players_;

    private final Deck deck_;
    private final ArrayList<Event> eventHistory_;

    final static int maxHandScore = 21;
    final static int dealerThreshold = 17;
    final static int aceDiscount = 10;

    private Round(Deck deck) {
        deck_ = deck;
        state_ = State.NOT_STARTED;
        eventHistory_ = new ArrayList<Event>();
        dealerPosition_ = new Position();
        players_ = new HashMap<>();
    }

    static interface EventApplier {
        void applyEvent(Event event, Round round);
    }

    public enum State {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED
    }

    public State state() { return state_; }
    public boolean finished() { return state_ == State.FINISHED; }

    public List<Card> dealerHand() { return Collections.unmodifiableList(dealerPosition_.hand()); }
    public List<Card> playerHand(Player player) { return Collections.unmodifiableList(players_.get(player).hand()); }

    public PlayerPosition playerPosition(Player player) { return players_.get(player); }
    public Position dealerPosition() { return  dealerPosition_; }

    static Round create(Deck deck) {
        return new Round(deck);
    }

    void clear() {
        state_ = State.NOT_STARTED;
        eventHistory_.clear();
        players_.clear();
        dealerPosition_.clear();
    }

    void applyEvent(Event event) {
        eventHistory_.add(event);
        event.type().applyEvent(event, this);
    }

    void calculatePlayerResult(PlayerPosition entry) {
        if (entry.score() > dealerPosition_.score() && entry.score() <= maxHandScore)
            entry.state_ = PlayerState.WIN;
        else if (entry.hasSplit()) {
            entry.state_ = PlayerState.PARTIAL_WIN;
        }
        else
            entry.state_ = PlayerState.LOST;

    }

    void recalculateOverallResult() {
        // Waiting for all players to be ginished
        for ( var playerInfo : players_.entrySet() ) {
            if (!playerInfo.getValue().finished())
                return;
        }

        state_ = State.FINISHED;

        //  The dealer then reveals the hidden card and must hit until the cards total 17 or more points
        for (Card c : dealerPosition_.mainHand_) {
            c.open();
        }

        // Dealer takes all other cards
        while (dealerPosition_.score() < dealerThreshold ) {
            dealerPosition_.mainHand_.add(deck_.nextCard(true));
        }

        // Now player final status can be calculated
        for ( var playerInfo : players_.entrySet() ) {
            // Filter out players that already surrended
            if (playerInfo.getValue().needsCalculation())
                calculatePlayerResult(playerInfo.getValue());
        }
    }

    // Position of the participant during the round
    // Participant can be either dealer or player
    public static class Position {
        protected ArrayList<Card> mainHand_;

        Position() {
            clear();
        }

        void clear() {
            mainHand_ = new ArrayList<>();
        }

        int score() { return score(mainHand_); }

        protected static int score(ArrayList<Card> hand) {
            int score = 0;
            boolean aceWasBefore = false;

            for (Card c : hand) {
                score += c.score();

                // Ace can become 1 point if score exceeds limit
                if (score > maxHandScore && aceWasBefore ) {
                    score -= aceDiscount;
                }

                if (c.isAce())
                    aceWasBefore = true;
            }

            return score;
        }

        ArrayList<Card> hand() { return mainHand_; }

        @Override
        public String toString() {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append('{');
            for (Card c : mainHand_) {
                if (c.visible()) {
                    strBuild.append(c.toString());
                    strBuild.append(' ');
                }
            }
            strBuild.append("}, ");

            return  strBuild.toString();
        }
    }

    public static enum PlayerState {
        NOT_READY,
        IN_GAME,
        DONE, // Player should be done in order to calculate WIN / LOST / PARTIAL_WIN. All players done -> game finished
        LOST,
        WIN,        // Player can win only if dealer hits
        PARTIAL_WIN // Player can win only if dealer hits
    }

    public class PlayerPosition extends Round.Position {
        private ArrayList<Card> splitHand_;
        private int bet_;
        private PlayerState state_;

        PlayerPosition() {
            state_ = PlayerState.NOT_READY;
            bet_ = 0;
            splitHand_ = new ArrayList<Card>();
        }

        ArrayList<Card> splitHand() { return splitHand_; }
        int splitScore() { return score(splitHand_); }

        boolean canSplit() {
            return splitHand_.size() == 0 &&
                    mainHand_.get(0).value() == mainHand_.get(0).value();  }

        boolean hasSplit() { return splitHand_.size() != 0; }
        int bet() { return bet_; }
        PlayerState state() { return state_; }

        int delta() {
            int delta = 0;

            if (state_ == PlayerState.WIN) {
                delta = bet_;
            }
            else if (state_ == PlayerState.LOST) {
                delta = -bet_;
            }
            else if (state_ == PlayerState.PARTIAL_WIN) {
                delta = bet_ / 2;
            }

            return delta;
        }

        @Override
        public String toString() {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(super.toString());

            if (hasSplit()) {
                strBuild.append(';');
                strBuild.append('{');
                for (Card c : splitHand_) {
                    strBuild.append(c.toString()).append(' ');
                }
                strBuild.append("}, ");
            }

            strBuild.append(bet_).append("#");
            return  strBuild.toString();
        }

        boolean ready() { return state_ == PlayerState.IN_GAME; }
        boolean finished() { return
                state_ == PlayerState.LOST || // In case of surrender
                state_ == PlayerState.DONE ||
                state_ == PlayerState.PARTIAL_WIN ||
                state_ == PlayerState.WIN;
        }

        boolean needsCalculation() { return state_ == PlayerState.DONE; }
    }

    static class Event {
        private final EventType eventType_;
        private final Player player_;
        private final int value_;

        private Event(Player player, EventType eventType) { this(player, eventType, 0); }

        private Event(Player player, EventType eventType, int value) {
            player_ = player;
            eventType_ = eventType;
            value_ = value;
        }

        static Event create(Player player, EventType eventType) {
            return new Event(player, eventType);
        }

        static Event create(Player player, EventType eventType, int value) {
            return new Event(player, eventType, value);
        }

        EventType type() { return eventType_; }
        Player player() { return  player_; }
    }

    static enum EventType implements EventApplier {
        newBet {
            @Override
            public void applyEvent(Event event, Round round) {
                PlayerPosition newPlayerPosition = round.new PlayerPosition();
                newPlayerPosition.bet_ = event.value_;
                newPlayerPosition.mainHand_.add(round.deck_.nextCard(true));
                newPlayerPosition.mainHand_.add(round.deck_.nextCard(true));
                newPlayerPosition.state_ = PlayerState.IN_GAME;
                round.players_.put(event.player(), newPlayerPosition);

                for ( var playerInfo : round.players_.entrySet() ) {
                    if (!playerInfo.getValue().ready())
                        return;
                }

                round.state_ = State.IN_PROGRESS;
                round.dealerPosition_ = new Position();
                round.dealerPosition_.mainHand_.add(round.deck_.nextCard(true));
                round.dealerPosition_.mainHand_.add(round.deck_.nextCard(false));
            }
        },

        hitAction {
            @Override
            public void applyEvent(Event event, Round round) {
                var playerPosition = round.playerPosition(event.player());
                playerPosition.mainHand_.add(round.deck_.nextCard(true));
                if ( playerPosition.score() > maxHandScore )
                    playerPosition.state_ = PlayerState.LOST;
                round.recalculateOverallResult();
            }
            },

        standAction {
            @Override
            public void applyEvent(Event event, Round round) {
                round.playerPosition(event.player()).state_ = PlayerState.DONE;
                round.recalculateOverallResult();
            }
        },

        doubleDownAction {
            @Override
            public void applyEvent(Event event, Round round) {
                round.playerPosition(event.player()).bet_ *= 2;
                round.playerPosition(event.player()).mainHand_.add(round.deck_.nextCard(true));
                round.playerPosition(event.player()).state_ = PlayerState.DONE;
                round.recalculateOverallResult();
            }
        },

        splitAction {
            @Override
            public void applyEvent(Event event, Round round) {
                var mainHand = round.playerPosition(event.player()).mainHand_;
                Card splitCard = mainHand.remove(mainHand.size() - 1);
                round.playerPosition(event.player()).bet_ *= 2;
                round.playerPosition(event.player()).splitHand_.add(splitCard);
                round.playerPosition(event.player()).splitHand_.add(round.deck_.nextCard(true));
                round.playerPosition(event.player()).mainHand_.add(round.deck_.nextCard(true));
                round.playerPosition(event.player()).state_ = PlayerState.DONE;
                round.recalculateOverallResult();
            }
        },

        surrenderAction {
            @Override
            public void applyEvent(Event event, Round round) {
                round.playerPosition(event.player()).bet_ /= 2;
                round.playerPosition(event.player()).state_ = PlayerState.LOST;
                round.recalculateOverallResult();
            }
        }
    }
}
