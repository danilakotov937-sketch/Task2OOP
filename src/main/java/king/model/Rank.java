package king.model;

/**
 * Достоинство карты в колоде из 32 карт. Порядок констант — от младшей к старшей.
 */
public enum Rank {
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    /** Валет. */ JACK("J"),
    /** Дама. */ QUEEN("Q"),
    /** Король. */ KING("K"),
    /** Туз. */ ACE("A");

    private final String label;

    Rank(String label) {
        this.label = label;
    }

    /**
     * Короткое обозначение достоинства (7, 10, J, Q, K, A).
     *
     * @return короткое обозначение достоинства (7, 10, J, Q, K, A)
     */
    public String label() {
        return label;
    }

    /**
     * Находит достоинство по обозначению.
     *
     * @param s обозначение, например "10" или "q"
     * @return достоинство
     * @throws IllegalArgumentException если обозначение неизвестно
     */
    public static Rank fromLabel(String s) {
        for (Rank r : values()) {
            if (r.label.equalsIgnoreCase(s)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Неизвестное достоинство: " + s);
    }
}
