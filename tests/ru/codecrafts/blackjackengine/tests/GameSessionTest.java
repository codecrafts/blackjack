package ru.codecrafts.blackjackengine.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.codecrafts.blackjackengine.GameEngine;
import ru.codecrafts.blackjackengine.GameSession;
import ru.codecrafts.blackjackengine.GameSettings;
import ru.codecrafts.blackjackengine.Player;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTests {
    private static GameSession session;
    private static ArrayList<Player> players = new ArrayList<>();
    private static final String[] playerNames = { "Alexey", "Alexander", "Mikhail" };
    private static final int[] betValues = { 10, 20, 30 };

    private static final int originalCardCount = 2;
    private static final int maxCardCount = 3;

    @BeforeEach
    public void setUp() {
        session = GameEngine.instance().createSession(GameSettings.createAutotestDefault());
        for (String name : playerNames) {
            players.add(session.createPlayer(name));
        }

    }

    @AfterEach
    public void tearDown() {
        for (Player player : players) {
            session.unregisterPlayer(player);
        }

        GameEngine.instance().unregister(session);
        players.clear();
    }

    @Test
    public void testMultiplePlayersHit() {

        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];
            assertTrue(player.canMakeNewBet());
            player.makeNewBet(betValue);
            assertTrue( player.hand().size() == originalCardCount &&
                    player.currentBet() == betValue);
        }

        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];
            assertTrue( player.canMakeMove());
            player.hit();
            assertTrue( player.hand().size() == maxCardCount &&
                    player.currentBet() == betValue);

            assertTrue( player.canMakeMove());
            player.stand();
        }


        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];

            assertTrue(player.canMakeNewBet());
            assertTrue(Math.abs(player.roundDelta()) == betValue);
        }
    }

    @Test
    public void testMultiplePlayersDoDifferent() {

        final int variantCount = 3;

        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];
            assertTrue(player.canMakeNewBet());
            player.makeNewBet(betValue);
            assertTrue( player.hand().size() == originalCardCount &&
                    player.currentBet() == betValue);
        }

        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];

            assertTrue( player.canMakeMove());

            if (i % variantCount == 0)
                player.stand();
            else if (i % variantCount == 1)
                player.surrender();
            else if (i % variantCount == 2)
                player.doubleDown();
        }


        for (int i = 0; i < players.size(); i++ ) {
            Player player = players.get(i);
            int betValue = betValues[i];

            assertTrue(player.canMakeNewBet());
            if (i % variantCount == 0)
                assertTrue(Math.abs(player.roundDelta()) == betValue);
            else
                assertFalse(Math.abs(player.roundDelta()) == betValue);
        }
    }

}