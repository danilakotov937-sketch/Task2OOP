package king.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import king.model.Card;
import king.model.Seats;
import king.model.Suit;
import king.model.Trick;
import king.rules.Contract;

/**
 * Одна раздача (один кон): руки, текущая взятка, очки за кон. Не знает ни о сдаче колоды,
 * ни о партии в целом, поэтому её удобно тестировать на заранее заданных руках.
 */
public final class Deal {
    private final Contract contract;
    private final Suit trump;
    private final Seats<List<Card>> hands;
    private final Seats<Integer> scores;
    private final Consumer<GameEvent> sink;
    private final List<Card> played = new ArrayList<>();

    private Trick trick;
    private int trickIndex;
    private int current;
    private boolean finished;

    /**
     * Создаёт раздачу с заданными руками.
     *
     * @param contract правила кона
     * @param trump    козырь или {@code null}
     * @param hands    руки игроков (будут изменяться по ходу игры)
     * @param leader   игрок, который заходит первым
     * @param sink     приёмник событий
     */
    public Deal(Contract contract, Suit trump, Seats<List<Card>> hands, int leader, Consumer<GameEvent> sink) {
        this.contract = contract;
        this.trump = trump;
        this.hands = hands;
        this.scores = Seats.of(hands.size(), i -> 0);
        this.sink = sink;
        this.current = leader;
        this.trick = new Trick(hands.size(), trump);
    }

    /**
     * Правила кона.
     *
     * @return правила кона
     */
    public Contract contract() {
        return contract;
    }

    /**
     * Козырь или {@code null}.
     *
     * @return козырь или {@code null}
     */
    public Suit trump() {
        return trump;
    }

    /**
     * {@code true}, если все взятки сыграны.
     *
     * @return {@code true}, если все взятки сыграны
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
        return current;
    }

    /**
     * Число уже завершённых взяток.
     *
     * @return число уже завершённых взяток
     */
    public int tricksPlayed() {
        return trickIndex;
    }

    /**
     * Текущая (незавершённая) взятка.
     *
     * @return текущая (незавершённая) взятка
     */
    public Trick currentTrick() {
        return trick;
    }

    /**
     * Очки каждого игрока за этот кон.
     *
     * @return очки каждого игрока за этот кон
     */
    public List<Integer> scores() {
        return scores.toList();
    }

    /**
     * Все карты, сыгранные в раздаче, в порядке розыгрыша.
     *
     * @return все карты, сыгранные в раздаче, в порядке розыгрыша
     */
    public List<Card> playedCards() {
        return List.copyOf(played);
    }

    /**
     * Возвращает копию руки игрока.
     *
     * @param seat игрок
     * @return копия руки игрока
     */
    public List<Card> hand(int seat) {
        return List.copyOf(hands.get(seat));
    }

    /**
     * Карты, которыми игрок может сходить сейчас. Правила по порядку: нужно ответить в масть захода;
     * если масти нет — действуют ограничения кона на отбой; при заходе — ограничения кона на заход.
     *
     * @param seat игрок
     * @return допустимые карты; пустой список, если сейчас не его очередь или раздача закончена
     */
    public List<Card> legalMoves(int seat) {
        if (finished || seat != current) {
            return List.of();
        }
        List<Card> hand = hands.get(seat);
        if (trick.isEmpty()) {
            return List.copyOf(contract.legalLeads(hand));
        }
        Suit led = trick.ledSuit();
        List<Card> follow = hand.stream().filter(c -> c.suit() == led).toList();
        return follow.isEmpty() ? List.copyOf(contract.legalDiscards(hand)) : follow;
    }

    /**
     * Играет карту. После последней карты взятки определяет победителя и начисляет очки.
     *
     * @param seat игрок
     * @param card карта
     * @throws IllegalMoveException если ход недопустим
     */
    public void play(int seat, Card card) {
        if (finished) {
            throw new IllegalMoveException("Раздача уже закончена");
        }
        if (seat != current) {
            throw new IllegalMoveException("Сейчас ходит игрок " + current + ", а не " + seat);
        }
        if (!hands.get(seat).contains(card)) {
            throw new IllegalMoveException("У игрока " + seat + " нет карты " + card);
        }
        if (!legalMoves(seat).contains(card)) {
            throw new IllegalMoveException("Карта " + card + " нарушает правила кона «" + contract.title() + "»");
        }
        hands.get(seat).remove(card);
        played.add(card);
        trick.add(seat, card);
        sink.accept(new GameEvent.CardPlayed(seat, card));
        if (trick.isComplete()) {
            finishTrick();
        } else {
            current = hands.next(seat);
        }
    }

    private void finishTrick() {
        int winner = trick.winner();
        int points = contract.trickPoints(trick, trickIndex);
        scores.set(winner, scores.get(winner) + points);
        sink.accept(new GameEvent.TrickWon(trickIndex + 1, winner, trick.plays(), points));
        trickIndex++;
        current = winner;
        if (trickIndex == Contract.TRICKS_PER_DEAL) {
            finished = true;
        } else {
            trick = new Trick(hands.size(), trump);
        }
    }
}
