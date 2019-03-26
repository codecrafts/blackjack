package ru.codecrafts.blackjackengine.tests;

import org.junit.jupiter.api.*;
import ru.codecrafts.blackjackengine.*;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests all possible action scenarios for one player
class PlayerUnitTests {

    private static GameSession session;
    private static Player player;
    private static final int betValue = 10;
    private static final int originalCardCount = 2;
    private static final int maxCardCount = 3;

    @BeforeEach
    public void setUp() {
        session = GameEngine.instance().createSession(GameSettings.createAutotestDefault());
        player = session.createPlayer("Oleg Ryskov");
    }

    @AfterEach
    public void tearDown() {
        session.unregisterPlayer(player);
        GameEngine.instance().unregister(session);
    }

    @Test
    public void testPlayerHit() {
        assertTrue(player.canMakeNewBet());
        player.makeNewBet(betValue);
        assertTrue( player.hand().size() == originalCardCount &&
                            player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.hit();
        assertTrue( player.hand().size() == maxCardCount &&
                player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.stand();

        assertTrue(player.canMakeNewBet());
        assertTrue(Math.abs(player.roundDelta()) == betValue);
    }

    @Test
    public void testPlayerStand() {
        assertTrue(player.canMakeNewBet());
        player.makeNewBet(betValue);
        assertTrue( player.hand().size() == originalCardCount &&
                            player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.stand();

        assertTrue(player.canMakeNewBet());
        assertTrue(Math.abs(player.roundDelta()) == betValue);
    }

    @Test
    public void testPlayerDoubleDown() {
        assertTrue(player.canMakeNewBet());
        player.makeNewBet(betValue);
        assertTrue( player.hand().size() == originalCardCount &&
                player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.doubleDown();
        assertTrue( player.hand().size() == maxCardCount &&
                player.currentBet() == 2 * betValue);

        assertTrue(player.canMakeNewBet() &&
                player.roundDelta() != betValue);
    }

    @Test
    public void testPlayerSplit() {
        assertTrue(player.canMakeNewBet());
        player.makeNewBet(betValue);
        assertTrue( player.hand().size() == originalCardCount &&
                player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.split();
        assertTrue( player.hand().size() == originalCardCount &&
                player.currentBet() == 2 * betValue &&
                player.splitHand().size() == originalCardCount
        );

        assertTrue( player.canMakeMove());
        player.stand();

        assertTrue(player.canMakeNewBet());
        assertTrue(Math.abs(player.roundDelta()) == betValue );
    }

    @Test
    public void testPlayerSurrender() {
        assertTrue(player.canMakeNewBet());
        player.makeNewBet(betValue);
        assertTrue( player.hand().size() == originalCardCount &&
                player.currentBet() == betValue);

        assertTrue( player.canMakeMove());
        player.surrender();
        assertTrue(player.hand().size() == originalCardCount);
        assertTrue(player.currentBet() == betValue / 2);
        assertTrue(player.state() == Round.PlayerState.LOST);

        assertTrue(player.canMakeNewBet());
        assertTrue(player.roundDelta() != betValue);
    }
}