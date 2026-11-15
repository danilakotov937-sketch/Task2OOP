package king.model;

/**
 * Игральная карта — неизменяемая пара «масть + достоинство».
 *
 * @param suit масть
 * @param rank достоинство
 */
public record Card(Suit suit, Rank rank) {

    /**
     * Разбирает запись вида {@code KH}, {@code 10S}, {@code 7d}: достоинство, затем масть.
     *
     * @param text запись карты
     * @return карта
     * @throws IllegalArgumentException если запись некорректна
     */
    public static Card parse(String text) {
        String t = text.trim();
        if (t.length() < 2) {
            throw new IllegalArgumentException("Некорректная карта: " + text);
        }
        Rank rank = Rank.fromLabel(t.substring(0, t.length() - 1));
        Suit suit = Suit.fromCode(t.charAt(t.length() - 1));
        return new Card(suit, rank);
    }

    /**
     * {@code true}, если это король червей («кинг»).
     *
     * @return {@code true}, если это король червей («кинг»)
     */
    public boolean isKingOfHearts() {
        return suit == Suit.HEARTS && rank == Rank.KING;
    }

    @Override
    public String toString() {
        return rank.label() + suit.symbol();
    }
}
