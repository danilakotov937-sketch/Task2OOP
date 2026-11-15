package king.model;

/**
 * Масть карты. Порядок констант задаёт порядок сортировки руки.
 */
public enum Suit {
    /** Пики. */
    SPADES('S', "♠"),
    /** Трефы. */
    CLUBS('C', "♣"),
    /** Бубны. */
    DIAMONDS('D', "♦"),
    /** Червы. */
    HEARTS('H', "♥");

    private final char code;
    private final String symbol;

    Suit(char code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    /**
     * Однобуквенный код масти (S, C, D, H).
     *
     * @return однобуквенный код масти (S, C, D, H)
     */
    public char code() {
        return code;
    }

    /**
     * Символ масти для вывода.
     *
     * @return символ масти для вывода
     */
    public String symbol() {
        return symbol;
    }

    /**
     * Находит масть по однобуквенному коду без учёта регистра.
     *
     * @param c код масти
     * @return масть
     * @throws IllegalArgumentException если кода не существует
     */
    public static Suit fromCode(char c) {
        char up = Character.toUpperCase(c);
        for (Suit s : values()) {
            if (s.code == up) {
                return s;
            }
        }
        throw new IllegalArgumentException("Неизвестная масть: " + c);
    }
}
