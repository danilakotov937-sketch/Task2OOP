package king.model;

/**
 * Одна сыгранная карта во взятке.
 *
 * @param seat номер игрока
 * @param card сыгранная карта
 */
public record Play(int seat, Card card) {
}
