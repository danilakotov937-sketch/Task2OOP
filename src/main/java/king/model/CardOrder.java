package king.model;

import java.util.Comparator;

/**
 * Компараторы карт: порядок в руке и сила карт во взятке с учётом козыря.
 */
public final class CardOrder {

    /** Порядок для отображения руки: по масти, внутри масти — от старшей к младшей. */
    public static final Comparator<Card> HAND =
            Comparator.comparing(Card::suit).thenComparing(Card::rank, Comparator.reverseOrder());

    private CardOrder() {
    }

    /**
     * Сила карты во взятке: козырь бьёт масть захода, масть захода бьёт всё остальное,
     * карты других мастей между собой равны (они взятку не берут).
     *
     * @param led   масть первой карты взятки
     * @param trump козырная масть или {@code null}, если игра без козыря
     * @return компаратор, большая карта — сильнее
     */
    public static Comparator<Card> forTrick(Suit led, Suit trump) {
        return Comparator
                .comparingInt((Card c) -> c.suit() == trump ? 2 : c.suit() == led ? 1 : 0)
                .thenComparing((a, b) -> {
                    boolean comparable = a.suit() == b.suit() && (a.suit() == trump || a.suit() == led);
                    return comparable ? a.rank().compareTo(b.rank()) : 0;
                });
    }
}
