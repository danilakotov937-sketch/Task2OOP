package king.cli;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import king.ai.PlayerStrategy;
import king.ai.PlayerView;
import king.model.Card;
import king.model.Play;

/**
 * Человек за консолью: показывает руку и допустимые ходы, читает номер карты.
 */
public final class ConsolePlayer implements PlayerStrategy {
    private final Scanner in;
    private final PrintStream out;

    /**
     * Создаёт игрока-человека.
     *
     * @param in  откуда читать ввод
     * @param out куда печатать подсказки
     */
    public ConsolePlayer(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public Card chooseCard(PlayerView view) {
        out.printf("%nВаш ход (игрок %d). Кон: %s%s%n", view.seat(), view.contract().title(),
                view.trump() == null ? "" : ", козырь " + view.trump().symbol());
        out.println("Стол: " + view.trickSoFar().stream()
                .map(Play::card).map(Object::toString).toList());
        out.println("Рука: " + view.hand());
        List<Card> moves = view.legalMoves();
        for (int i = 0; i < moves.size(); i++) {
            out.printf("  %d) %s%n", i + 1, moves.get(i));
        }
        while (true) {
            out.print("Номер карты: ");
            if (!in.hasNextLine()) {
                throw new IllegalStateException("Ввод закончился");
            }
            String line = in.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= moves.size()) {
                    return moves.get(n - 1);
                }
            } catch (NumberFormatException ignored) {
                // повторим запрос
            }
            out.println("Введите число от 1 до " + moves.size());
        }
    }
}
