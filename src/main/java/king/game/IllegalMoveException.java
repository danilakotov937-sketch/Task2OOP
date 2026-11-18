package king.game;

/**
 * Недопустимый ход: не очередь игрока, карты нет в руке, нарушено правило кона или партия закончена.
 */
public class IllegalMoveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с описанием нарушения.
     *
     * @param message описание нарушения
     */
    public IllegalMoveException(String message) {
        super(message);
    }
}
