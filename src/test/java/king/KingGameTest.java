package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import king.ai.LowestCardStrategy;
import king.ai.PlayerStrategy;
import king.ai.RandomStrategy;
import king.game.GameEvent;
import king.game.GameRunner;
import king.game.IllegalMoveException;
import king.game.KingGame;
import king.model.Card;
import king.rules.Contracts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KingGameTest {

    private static KingGame playFull(long seed) {
        Random rng = new Random(seed);
        KingGame game = new KingGame(rng, Contracts.standardTen());
        List<PlayerStrategy> bots = List.of(new RandomStrategy(rng), new RandomStrategy(rng),
                new LowestCardStrategy(), new RandomStrategy(rng));
        GameRunner.runToEnd(game, bots);
        return game;
    }

    @Test
    @DisplayName("Полная партия из зафиксированного посева доигрывается до конца")
    void fullGameFinishes() {
        KingGame game = playFull(12345);
        assertTrue(game.isFinished());
        assertEquals(GOLDEN_TOTALS_12345, game.totals());
    }

    @Test
    @DisplayName("Один посев — одна и та же партия (журналы событий равны)")
    void reproducible() {
        assertEquals(playFull(99).events(), playFull(99).events());
    }

    @Test
    @DisplayName("Разные посевы дают разные партии")
    void differentSeeds() {
        assertTrue(!playFull(1).events().equals(playFull(2).events()));
    }

    @Test
    @DisplayName("Сумма очков партии равна нулю на любом посеве")
    void zeroSum() {
        for (long seed = 0; seed < 50; seed++) {
            assertEquals(0, playFull(seed).totals().stream().mapToInt(Integer::intValue).sum());
        }
    }

    @Test
    @DisplayName("Журнал: 10 раздач, 80 взяток, 320 сыгранных карт")
    void eventCounts() {
        List<GameEvent> log = playFull(5).events();
        assertEquals(10, log.stream().filter(e -> e instanceof GameEvent.DealStarted).count());
        assertEquals(10, log.stream().filter(e -> e instanceof GameEvent.DealFinished).count());
        assertEquals(80, log.stream().filter(e -> e instanceof GameEvent.TrickWon).count());
        assertEquals(320, log.stream().filter(e -> e instanceof GameEvent.CardPlayed).count());
        assertTrue(log.get(log.size() - 1) instanceof GameEvent.GameFinished);
    }

    @Test
    @DisplayName("Механика не печатает в консоль")
    void noConsoleOutput() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            playFull(777);
        } finally {
            System.setOut(original);
        }
        assertEquals(0, buffer.size());
    }

    @Test
    @DisplayName("Козырь объявляется только в конах на взятие")
    void trumpOnlyInPositiveContracts() {
        List<GameEvent.DealStarted> starts = playFull(3).events().stream()
                .filter(e -> e instanceof GameEvent.DealStarted)
                .map(e -> (GameEvent.DealStarted) e).toList();
        for (int i = 0; i < 10; i++) {
            if (i < 6) {
                assertNull(starts.get(i).trump());
            } else {
                assertNotNull(starts.get(i).trump());
            }
        }
    }

    @Test
    @DisplayName("Сдающий меняется по кругу")
    void dealerRotates() {
        List<Integer> dealers = playFull(3).events().stream()
                .filter(e -> e instanceof GameEvent.DealStarted)
                .map(e -> ((GameEvent.DealStarted) e).dealer()).toList();
        assertEquals(List.of(0, 1, 2, 3, 0, 1, 2, 3, 0, 1), dealers);
    }

    @Test
    @DisplayName("Слушатель получает те же события, что попадают в журнал")
    void listenerSeesAllEvents() {
        Random rng = new Random(8);
        KingGame game = new KingGame(rng, Contracts.standardTen());
        List<GameEvent> received = new ArrayList<>();
        game.addListener(received::add);
        GameRunner.runToEnd(game, List.of(new LowestCardStrategy(), new LowestCardStrategy(),
                new LowestCardStrategy(), new LowestCardStrategy()));
        // первая раздача началась до подписки, поэтому в журнале на одно событие больше
        assertEquals(game.events().size() - 1, received.size());
    }

    @Test
    @DisplayName("Недопустимый ход в партии — исключение, состояние не меняется")
    void illegalMoveInGame() {
        KingGame game = new KingGame(new Random(1), Contracts.standardTen());
        int seat = game.currentSeat();
        int wrongSeat = (seat + 1) % 4;
        Card any = game.currentDeal().hand(wrongSeat).get(0);
        assertThrows(IllegalMoveException.class, () -> game.play(wrongSeat, any));
        assertEquals(seat, game.currentSeat());
    }

    @Test
    @DisplayName("После окончания партии ходить нельзя")
    void playAfterGameEnds() {
        KingGame game = playFull(4);
        assertThrows(IllegalMoveException.class, () -> game.play(0, Card.parse("7S")));
    }

    @Test
    @DisplayName("Победители — игроки с максимальным счётом")
    void winnersHaveMaxScore() {
        KingGame game = playFull(21);
        int max = game.totals().stream().max(Integer::compare).orElseThrow();
        assertTrue(!game.winners().isEmpty());
        game.winners().forEach(w -> assertEquals(max, game.totals().get(w).intValue()));
    }

    @Test
    @DisplayName("Пустой список конов недопустим")
    void emptyContracts() {
        assertThrows(IllegalArgumentException.class, () -> new KingGame(new Random(1), List.of()));
    }

    private static final List<Integer> GOLDEN_TOTALS_12345 = List.of(-10, -3, -9, 22);
}
