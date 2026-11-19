package king.cli;

import java.io.PrintStream;
import java.util.stream.Collectors;
import king.game.GameEvent;
import king.game.GameListener;
import king.model.Play;

/**
 * Слой вывода: превращает события партии в строки журнала. Единственное место, где печатается игра.
 */
public final class ConsoleRenderer implements GameListener {
    private final PrintStream out;
    private final boolean showCards;

    /**
     * Создаёт слой вывода.
     *
     * @param out       куда печатать
     * @param showCards печатать ли каждую сыгранную карту (иначе только итоги взяток и конов)
     */
    public ConsoleRenderer(PrintStream out, boolean showCards) {
        this.out = out;
        this.showCards = showCards;
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event) {
            case GameEvent.DealStarted e -> out.printf("%n=== Кон %d: %s (сдаёт игрок %d)%s ===%n",
                    e.dealNumber(), e.contractTitle(), e.dealer(),
                    e.trump() == null ? "" : ", козырь " + e.trump().symbol());
            case GameEvent.CardPlayed e -> {
                if (showCards) {
                    out.printf("  игрок %d: %s%n", e.seat(), e.card());
                }
            }
            case GameEvent.TrickWon e -> out.printf("  взятка %d: %s -> берёт игрок %d (%+d)%n",
                    e.trickNumber(),
                    e.plays().stream().map(Play::card).map(Object::toString).collect(Collectors.joining(" ")),
                    e.winner(), e.points());
            case GameEvent.DealFinished e -> out.printf("  итог кона: %s | всего: %s%n", e.deltas(), e.totals());
            case GameEvent.GameFinished e -> out.printf("%n*** Партия окончена. Очки: %s. Победитель(и): %s ***%n",
                    e.totals(), e.winners());
        }
    }
}
