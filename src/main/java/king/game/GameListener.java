package king.game;

/**
 * Подписчик на события партии (паттерн «наблюдатель»).
 */
@FunctionalInterface
public interface GameListener {
    /**
     * Обрабатывает событие партии.
     *
     * @param event произошедшее событие
     */
    void onEvent(GameEvent event);
}
