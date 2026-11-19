package king.game;

import java.util.List;
import king.ai.PlayerStrategy;

/**
 * Прогоняет партию до конца, спрашивая стратегии игроков. Ничего не выводит.
 */
public final class GameRunner {
    private GameRunner() {
    }

    /**
     * Доигрывает партию.
     *
     * @param game       партия
     * @param strategies стратегии игроков по номерам мест (ровно {@link KingGame#PLAYERS})
     * @throws IllegalArgumentException если стратегий не четыре
     * @throws IllegalMoveException     если стратегия выбрала недопустимую карту
     */
    public static void runToEnd(KingGame game, List<PlayerStrategy> strategies) {
        if (strategies.size() != KingGame.PLAYERS) {
            throw new IllegalArgumentException("Нужно ровно " + KingGame.PLAYERS + " стратегии");
        }
        while (!game.isFinished()) {
            int seat = game.currentSeat();
            game.play(seat, strategies.get(seat).chooseCard(game.viewFor(seat)));
        }
    }
}
