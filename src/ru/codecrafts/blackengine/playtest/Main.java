package ru.codecrafts.blackengine.playtest;

import ru.codecrafts.blackjackengine.GameEngine;
import ru.codecrafts.blackjackengine.GameSession;
import ru.codecrafts.blackjackengine.GameSettings;
import ru.codecrafts.blackjackengine.Player;

import java.util.Scanner;

// Формат чат бота офигенно подойдет под блекджек
public class Main {
    private static Player player_;
    private static GameSession session_;
    private static Scanner scanner_ = new Scanner(System.in);

    public static void main(String[] args) {
        initNewGame();
        int playerBet = playerStartRound();
        boolean oneMoreRound = true;
        do {
            boolean roundFinished = playerRoundMenu();
            if (roundFinished) {
                printRoundResult();
                oneMoreRound = oneMoreRoundMenu();
                if (oneMoreRound) playerStartRound();
            }
        }
        while(oneMoreRound);
        System.out.println("Спасибо за игру! До новых встреч!");
    }

    private static void initNewGame() {
        System.out.println("Приветствую вас, добро пожаловать в наше консольное казино!");
        System.out.println("Как вас зовут?");
        String playerName_ = scanner_.nextLine();
        System.out.println(playerName_ + ", так приятно с вами познакомиться!" );

        session_ = GameEngine.instance().createSession(GameSettings.createDefault());
        player_ = session_.createPlayer(playerName_);
    }

    // Returns player's bet
    private static int playerStartRound() {
        System.out.println("Новый раунд вот-вот начнётся. Какую ставку вы хотите сделать?");
        int playerBet = scanner_.nextInt();
        player_.makeNewBet(playerBet);
        System.out.println("Ваша ставка принята. Вы поставили " + playerBet + " долларов");
        printGameState();
        scanner_.nextLine(); // Make input stream empty
        return  playerBet;
    }

    // Returns true if the round is finished, false otherwise
    private static boolean playerRoundMenu() {
        boolean roundFinished = false;
        System.out.println("Что вы хотите сделать дальше?");
        if (player_.canMakeMove())  System.out.println("A) Взять еще одну карту");
        if (player_.canMakeMove())  System.out.println("B) Не брать больше карт");
        if (player_.canMakeMove())  System.out.println("C) Удвоить ставку");
        if (player_.canSplit())     System.out.println("D) Разбить пару");
        if (player_.canSurrender()) System.out.println("E) Сдаться");
        System.out.println("H) Попросить помощи");

        String playerChoice = scanner_.nextLine().toUpperCase();
        switch (playerChoice) {
            case "A": { playerHit(); } break;
            case "B": { playerStand();  } break;
            case "C": { playerDoubleDown(); } break;
            case "D": { playerSplit(); } break;
            case "E": { playerSurrender();  } break;
            case "H": { helpPlayer();  } break;
            default: { System.out.println("Ой, что-то пошло не так"); return roundFinished; }
        }

        return session_.currentRound().finished();
    }

    private static boolean oneMoreRoundMenu() {
        boolean oneMoreRound = false;
        System.out.println("Хотите сыграть ещё один раунд?");
        System.out.println("Y) Да");
        System.out.println("N) Нет");
        String playerChoice = scanner_.nextLine().toUpperCase();
        switch (playerChoice) {
            case "Y": { oneMoreRound = true; } break;
            case "N": { oneMoreRound = false;  } break;
        }
        return oneMoreRound;
    }

    private static void playerHit() {
        player_.hit();
        printGameState();
    }

    private static void playerStand() {
        player_.stand();
        printGameState();
    }

    private static void playerDoubleDown() {
        player_.doubleDown();
        printGameState();
    }

    private static void playerSplit() {
        player_.split();
        printGameState();
    }

    private static void playerSurrender() {
        player_.surrender();
        printGameState();
    }

    private static void helpPlayer() {
        System.out.println("У вас сейчас неплохая позиция");
    }

    private static void printGameState() {
        System.out.println("У дилера следующая позиция: " + session_.currentRound().dealerPosition().toString());
        System.out.println("У вас следующая позиция: " + session_.currentRound().playerPosition(player_).toString());
    }

    private static void printRoundResult() {
        System.out.println("Раунда игры закончен. Результаты игры: ");
        switch (player_.state()) {
            case LOST: { System.out.println("Вы проиграли. Ваш проигрыш: " + player_.roundDelta()); } break;
            case WIN: { System.out.println("Вы выиграли. Ваш выигрыш: " + player_.roundDelta()); } break;
            case PARTIAL_WIN: { System.out.println("Вы частично выиграли. Ваш выигрыш:" + player_.roundDelta()); } break;
            default: { System.out.println("Ой, что-то пошло не так"); }
        }
    }
}
