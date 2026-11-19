package king.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import king.ai.PlayerView;
import king.model.Card;
import king.model.CardOrder;
import king.model.Deck;
import king.model.Seats;
import king.model.Suit;
import king.rules.Contract;

/**
 * Партия в «Кинг»: четыре игрока, колода из 32 карт, последовательность конов.
 * Класс ничего не печатает — он меняет состояние и рассылает {@link GameEvent}.
 * Вся случайность берётся из переданного генератора.
 */
public final class KingGame {
    /** Число игроков. */
    public static final int PLAYERS = 4;

    private final Random rng;
    private final List<Contract> contracts;
    private final Seats<Integer> totals = Seats.of(PLAYERS, i -> 0);
    private final List<GameListener> listeners = new ArrayList<>();
    private final List<GameEvent> log = new ArrayList<>();

    private Deal deal;
    private int dealIndex;
    private boolean finished;

    /**
     * Создаёт партию и сдаёт первый кон.
     *
     * @param rng       генератор случайных чисел (определяет тасовку)
     * @param contracts коны в порядке розыгрыша, не менее одного
     * @throws IllegalArgumentException если список конов пуст
     */
    public KingGame(Random rng, List<Contract> contracts) {
        if (contracts.isEmpty()) {
            throw new IllegalArgumentException("Нужен хотя бы один кон");
        }
        this.rng = rng;
        this.contracts = List.copyOf(contracts);
        startDeal();
    }

    /**
     * Подписывает слушателя на события. События, уже попавшие в журнал, повторно не отправляются.
     *
     * @param listener слушатель
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Журнал всех событий партии (неизменяемая копия).
     *
     * @return журнал всех событий партии (неизменяемая копия)
     */
    public List<GameEvent> events() {
        return List.copyOf(log);
    }

    /**
     * {@code true}, если сыграны все коны.
     *
     * @return {@code true}, если сыграны все коны
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Игрок, чей сейчас ход.
     *
     * @return игрок, чей сейчас ход
     */
    public int currentSeat() {
        return deal.currentSeat();
    }

    /**
     * Номер текущей раздачи, с единицы.
     *
     * @return номер текущей раздачи, с единицы
     */
    public int dealNumber() {
        return dealIndex + 1;
    }

    /**
     * Текущая раздача (для просмотра состояния).
     *
     * @return текущая раздача (для просмотра состояния)
     */
    public Deal currentDeal() {
        return deal;
    }

    /**
     * Суммарные очки игроков.
     *
     * @return суммарные очки игроков
     */
    public List<Integer> totals() {
        return totals.toList();
    }

    /**
     * Возвращает допустимые карты игрока.
     *
     * @param seat игрок
     * @return допустимые карты игрока в текущий момент
     */
    public List<Card> legalMoves(int seat) {
        return finished ? List.of() : deal.legalMoves(seat);
    }

    /**
     * Игроки с максимальным счётом.
     *
     * @return номера победителей (несколько при равенстве)
     */
    public List<Integer> winners() {
        int max = totals.stream().max(Integer::compare).orElseThrow();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < PLAYERS; i++) {
            if (totals.get(i) == max) {
                result.add(i);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Снимок информации для игрока, чей сейчас ход.
     *
     * @param seat игрок
     * @return видимое игроку состояние
     */
    public PlayerView viewFor(int seat) {
        return new PlayerView(seat, deal.hand(seat), legalMoves(seat), deal.contract(), deal.trump(),
                deal.currentTrick().plays(), deal.playedCards(), dealNumber(), totals.toList());
    }

    /**
     * Играет карту. Когда заканчивается взятка, раздача или партия, переходы выполняются автоматически.
     *
     * @param seat игрок
     * @param card карта
     * @throws IllegalMoveException если ход недопустим или партия закончена
     */
    public void play(int seat, Card card) {
        if (finished) {
            throw new IllegalMoveException("Партия уже закончена");
        }
        deal.play(seat, card);
        if (deal.isFinished()) {
            finishDeal();
        }
    }

    private void finishDeal() {
        List<Integer> deltas = deal.scores();
        for (int i = 0; i < PLAYERS; i++) {
            totals.set(i, totals.get(i) + deltas.get(i));
        }
        emit(new GameEvent.DealFinished(dealIndex + 1, deal.contract().title(), deltas, totals.toList()));
        dealIndex++;
        if (dealIndex == contracts.size()) {
            finished = true;
            emit(new GameEvent.GameFinished(totals.toList(), winners()));
        } else {
            startDeal();
        }
    }

    private void startDeal() {
        Contract contract = contracts.get(dealIndex);
        int dealer = dealIndex % PLAYERS;
        int first = (dealer + 1) % PLAYERS;
        List<Card> cards = Deck.standard().shuffle(rng).cards();

        Seats<List<Card>> hands = Seats.of(PLAYERS, i -> new ArrayList<>());
        for (int k = 0; k < cards.size(); k++) {
            hands.get((first + k) % PLAYERS).add(cards.get(k));
        }
        hands.stream().forEach(h -> h.sort(CardOrder.HAND));

        Suit trump = contract.usesTrump() ? cards.get(cards.size() - 1).suit() : null;
        deal = new Deal(contract, trump, hands, first, this::emit);
        emit(new GameEvent.DealStarted(dealIndex + 1, contract.title(), dealer, trump));
    }

    private void emit(GameEvent event) {
        log.add(event);
        listeners.forEach(l -> l.onEvent(event));
    }
}
