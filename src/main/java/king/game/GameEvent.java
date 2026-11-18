package king.game;

import java.util.List;
import king.model.Card;
import king.model.Play;
import king.model.Suit;

/**
 * События партии. Механика только создаёт их и отдаёт подписчикам; вывод в консоль — отдельный слой.
 */
public sealed interface GameEvent {

    /**
     * Началась новая раздача.
     *
     * @param dealNumber    номер раздачи, с единицы
     * @param contractTitle название кона
     * @param dealer        сдающий
     * @param trump         козырь или {@code null}
     */
    record DealStarted(int dealNumber, String contractTitle, int dealer, Suit trump) implements GameEvent {
    }

    /**
     * Игрок сыграл карту.
     *
     * @param seat игрок
     * @param card карта
     */
    record CardPlayed(int seat, Card card) implements GameEvent {
    }

    /**
     * Взятка завершена.
     *
     * @param trickNumber номер взятки, с единицы
     * @param winner      взявший
     * @param plays       сыгранные карты
     * @param points      очки, начисленные взявшему
     */
    record TrickWon(int trickNumber, int winner, List<Play> plays, int points) implements GameEvent {
    }

    /**
     * Раздача закончена.
     *
     * @param dealNumber    номер раздачи
     * @param contractTitle название кона
     * @param deltas        очки каждого игрока за кон
     * @param totals        суммарные очки после кона
     */
    record DealFinished(int dealNumber, String contractTitle, List<Integer> deltas,
                        List<Integer> totals) implements GameEvent {
    }

    /**
     * Партия закончена.
     *
     * @param totals  итоговые очки
     * @param winners игроки с максимальным счётом (несколько при равенстве)
     */
    record GameFinished(List<Integer> totals, List<Integer> winners) implements GameEvent {
    }
}
