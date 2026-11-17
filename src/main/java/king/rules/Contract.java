package king.rules;

import java.util.List;
import king.model.Card;
import king.model.Suit;
import king.model.Trick;

/**
 * Кон — набор правил одной раздачи. Движок ничего не знает о конкретных конах:
 * все различия (очки, козырь, ограничения хода) спрятаны за этим интерфейсом.
 */
public interface Contract {

    /** Число взяток в раздаче (4 игрока по 8 карт). */
    int TRICKS_PER_DEAL = 8;

    /**
     * Название кона для журнала.
     *
     * @return название кона для журнала
     */
    String title();

    /**
     * {@code true}, если в коне разыгрывается козырь (определяется по последней сданной карте).
     *
     * @return {@code true}, если в коне разыгрывается козырь (определяется по последней сданной карте)
     */
    default boolean usesTrump() {
        return false;
    }

    /**
     * Очки, которые получает игрок, взявший взятку. Штрафы — отрицательные числа, награды — положительные.
     *
     * @param trick      завершённая взятка
     * @param trickIndex номер взятки в раздаче, с нуля
     * @return очки победителю взятки
     */
    int trickPoints(Trick trick, int trickIndex);

    /**
     * Карты руки, которыми разрешено зайти (начать взятку).
     *
     * @param hand рука игрока
     * @return допустимые карты, не пустой список для непустой руки
     */
    default List<Card> legalLeads(List<Card> hand) {
        return hand;
    }

    /**
     * Карты, которыми можно ответить, если нет масти захода (отбой/сброс).
     *
     * @param hand рука игрока, в которой нет масти захода
     * @return допустимые карты
     */
    default List<Card> legalDiscards(List<Card> hand) {
        return hand;
    }

    /**
     * Вспомогательный метод: {@code true}, если в руке есть карты, не являющиеся червями.
     *
     * @param hand рука
     * @return есть ли карты не червовой масти
     */
    static boolean hasNonHearts(List<Card> hand) {
        return hand.stream().anyMatch(c -> c.suit() != Suit.HEARTS);
    }
}
