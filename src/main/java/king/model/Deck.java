package king.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Колода из 32 карт (от семёрок до тузов). Перемешивается только переданным генератором,
 * поэтому один и тот же посев даёт одну и ту же раздачу.
 */
public final class Deck {
    /** Число карт в колоде. */
    public static final int SIZE = 32;

    private final List<Card> cards = new ArrayList<>(SIZE);

    private Deck() {
    }

    /**
     * Новая упорядоченная колода из 32 карт.
     *
     * @return новая упорядоченная колода из 32 карт
     */
    public static Deck standard() {
        Deck d = new Deck();
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                d.cards.add(new Card(s, r));
            }
        }
        return d;
    }

    /**
     * Перемешивает колоду.
     *
     * @param rng единственный генератор случайных чисел партии
     * @return эта же колода
     */
    public Deck shuffle(Random rng) {
        Collections.shuffle(cards, rng);
        return this;
    }

    /**
     * Неизменяемый список карт в текущем порядке.
     *
     * @return неизменяемый список карт в текущем порядке
     */
    public List<Card> cards() {
        return List.copyOf(cards);
    }
}
