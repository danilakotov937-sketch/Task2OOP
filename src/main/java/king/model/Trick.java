package king.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Взятка — карты, сыгранные игроками за один круг.
 */
public final class Trick {
    private final int players;
    private final Suit trump;
    private final List<Play> plays = new ArrayList<>();

    /**
     * Создаёт пустую взятку.
     *
     * @param players число игроков (взятка завершена, когда каждый сыграл по карте)
     * @param trump   козырь или {@code null}
     */
    public Trick(int players, Suit trump) {
        this.players = players;
        this.trump = trump;
    }

    /**
     * Добавляет карту во взятку.
     *
     * @param seat игрок
     * @param card карта
     * @throws IllegalStateException если взятка уже завершена
     */
    public void add(int seat, Card card) {
        if (isComplete()) {
            throw new IllegalStateException("Взятка уже завершена");
        }
        plays.add(new Play(seat, card));
    }

    /**
     * {@code true}, если все игроки сыграли.
     *
     * @return {@code true}, если все игроки сыграли
     */
    public boolean isComplete() {
        return plays.size() == players;
    }

    /**
     * {@code true}, если карт ещё нет.
     *
     * @return {@code true}, если карт ещё нет
     */
    public boolean isEmpty() {
        return plays.isEmpty();
    }

    /**
     * Сыгранные карты по порядку (неизменяемый список).
     *
     * @return сыгранные карты по порядку (неизменяемый список)
     */
    public List<Play> plays() {
        return List.copyOf(plays);
    }

    /**
     * Только карты, без номеров игроков.
     *
     * @return только карты, без номеров игроков
     */
    public List<Card> cards() {
        return plays.stream().map(Play::card).toList();
    }

    /**
     * Масть захода (первой карты) или {@code null}, если взятка пуста.
     *
     * @return масть захода (первой карты) или {@code null}, если взятка пуста
     */
    public Suit ledSuit() {
        return plays.isEmpty() ? null : plays.get(0).card().suit();
    }

    /**
     * Козырь взятки или {@code null}.
     *
     * @return козырь взятки или {@code null}
     */
    public Suit trump() {
        return trump;
    }

    /**
     * Определяет игрока, взявшего взятку.
     *
     * @return номер игрока-победителя
     * @throws IllegalStateException если взятка не завершена
     */
    public int winner() {
        if (!isComplete()) {
            throw new IllegalStateException("Взятка не завершена");
        }
        var order = CardOrder.forTrick(ledSuit(), trump);
        Play best = plays.get(0);
        for (Play p : plays) {
            if (order.compare(p.card(), best.card()) > 0) {
                best = p;
            }
        }
        return best.seat();
    }
}
