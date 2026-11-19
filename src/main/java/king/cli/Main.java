package king.cli;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import king.ai.LowestCardStrategy;
import king.ai.PlayerStrategy;
import king.ai.RandomStrategy;
import king.game.GameRunner;
import king.game.KingGame;
import king.rules.Contracts;

/**
 * Консольное приложение. Режимы: {@code demo [зерно]} — четыре бота, {@code human [зерно]} — человек (место 0)
 * против трёх ботов. Без аргументов запускается demo с зерном 42.
 */
public final class Main {
    private Main() {
    }

    /**
     * Точка входа консольного приложения.
     *
     * @param args режим и необязательное зерно генератора
     */
    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String mode = args.length > 0 ? args[0] : "demo";
        long seed = args.length > 1 ? Long.parseLong(args[1]) : 42L;
        Random rng = new Random(seed);
        KingGame game = new KingGame(rng, Contracts.standardTen());

        boolean human = mode.equals("human");
        game.addListener(new ConsoleRenderer(out, !human));

        List<PlayerStrategy> players = List.of(
                human ? new ConsolePlayer(new Scanner(System.in), out) : new RandomStrategy(rng),
                new RandomStrategy(rng),
                new LowestCardStrategy(),
                new RandomStrategy(rng));
        out.println("Кинг, зерно " + seed + (human ? " (вы — игрок 0)" : " (демо: 4 бота)"));
        GameRunner.runToEnd(game, players);
    }
}
