package king.ai;

import king.model.Card;

/**
 * Стратегия игрока: по видимому игроку состоянию выбирает карту (паттерн «стратегия»).
 */
@FunctionalInterface
public interface PlayerStrategy {
    /**
     * Выбирает карту для хода.
     *
     * @param view то, что видит игрок
     * @return карта из {@code view.legalMoves()}
     */
    Card chooseCard(PlayerView view);
}
