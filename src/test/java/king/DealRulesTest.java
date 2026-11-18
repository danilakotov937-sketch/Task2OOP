package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import king.game.Deal;
import king.game.GameEvent;
import king.game.IllegalMoveException;
import king.model.Card;
import king.model.Deck;
import king.model.Seats;
import king.model.Suit;
import king.rules.Contract;
import king.rules.NoHeartsContract;
import king.rules.NoKingContract;
import king.rules.NoTricksContract;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DealRulesTest {

    private static Deal deal(Contract c, Suit trump, int leader, String h0, String h1, String h2, String h3) {
        return new Deal(c, trump, TestHands.hands(h0, h1, h2, h3), leader, e -> { });
    }

    @Test
    @DisplayName("Нужно отвечать в масть захода")
    void mustFollowSuit() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S AH", "8S 9H", "10S", "JS");
        d.play(0, Card.parse("7S"));
        assertEquals(TestHands.cards("8S"), d.legalMoves(1));
    }

    @Test
    @DisplayName("Если масти нет — можно сыграть любую карту")
    void anyCardWithoutSuit() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S AH", "9H 8D", "10S", "JS");
        d.play(0, Card.parse("7S"));
        assertEquals(2, d.legalMoves(1).size());
    }

    @Test
    @DisplayName("Не брать червей: нельзя заходить червой при наличии другой масти")
    void noHeartsLeadForbidden() {
        Deal d = deal(new NoHeartsContract(), null, 0, "7H 8S", "7S", "8D", "9D");
        assertEquals(TestHands.cards("8S"), d.legalMoves(0));
        assertThrows(IllegalMoveException.class, () -> d.play(0, Card.parse("7H")));
    }

    @Test
    @DisplayName("Не брать червей: если на руке одни червы, заходить ими можно")
    void noHeartsLeadAllowedWhenOnlyHearts() {
        Deal d = deal(new NoHeartsContract(), null, 0, "7H 8H", "7S", "8D", "9D");
        assertEquals(2, d.legalMoves(0).size());
    }

    @Test
    @DisplayName("Не брать кинга: при отсутствии масти кинга нужно сбросить")
    void kingMustBeDiscarded() {
        Deal d = deal(new NoKingContract(), null, 0, "7S AD", "KH 8D", "9S", "10S");
        d.play(0, Card.parse("7S"));
        assertEquals(TestHands.cards("KH"), d.legalMoves(1));
    }

    @Test
    @DisplayName("Не брать кинга: при ответе в червовую масть можно играть любую червь")
    void kingNotForcedWhenFollowingHearts() {
        Deal d = deal(new NoKingContract(), null, 0, "7H", "KH 9H", "9S", "10S");
        d.play(0, Card.parse("7H"));
        assertEquals(2, d.legalMoves(1).size());
    }

    @Test
    @DisplayName("Ходить вне очереди нельзя")
    void wrongTurn() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S", "8S", "9S", "10S");
        assertThrows(IllegalMoveException.class, () -> d.play(1, Card.parse("8S")));
    }

    @Test
    @DisplayName("Нельзя сыграть карту, которой нет в руке")
    void cardNotInHand() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S", "8S", "9S", "10S");
        assertThrows(IllegalMoveException.class, () -> d.play(0, Card.parse("AS")));
    }

    @Test
    @DisplayName("Нельзя не ответить в масть, если она есть")
    void cannotIgnoreSuit() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S", "8S 9H", "9S", "10S");
        d.play(0, Card.parse("7S"));
        assertThrows(IllegalMoveException.class, () -> d.play(1, Card.parse("9H")));
    }

    @Test
    @DisplayName("Взявший взятку заходит следующим и получает очки")
    void winnerLeadsNext() {
        Deal d = deal(new NoTricksContract(), null, 0, "7S 8S", "9S 7D", "10S 7C", "JS 8C");
        for (String c : new String[] {"7S", "9S", "10S", "JS"}) {
            d.play(d.currentSeat(), Card.parse(c));
        }
        assertEquals(3, d.currentSeat());
        assertEquals(List.of(0, 0, 0, -2), d.scores());
    }

    @Test
    @DisplayName("Раздача заканчивается после 8 взяток; очки собираются у взявшего")
    void fullDeal() {
        List<Card> deck = Deck.standard().cards();
        Seats<List<Card>> hands = Seats.of(4, i -> new ArrayList<>(deck.subList(i * 8, i * 8 + 8)));
        List<GameEvent> events = new ArrayList<>();
        Deal d = new Deal(new NoTricksContract(), null, hands, 0, events::add);
        while (!d.isFinished()) {
            d.play(d.currentSeat(), d.legalMoves(d.currentSeat()).get(0));
        }
        assertEquals(8, d.tricksPlayed());
        assertEquals(List.of(-16, 0, 0, 0), d.scores());
        assertEquals(32, d.playedCards().size());
        assertEquals(40, events.size());
        assertTrue(events.get(events.size() - 1) instanceof GameEvent.TrickWon);
    }

    @Test
    @DisplayName("После конца раздачи ходить нельзя")
    void playAfterFinish() {
        List<Card> deck = Deck.standard().cards();
        Seats<List<Card>> hands = Seats.of(4, i -> new ArrayList<>(deck.subList(i * 8, i * 8 + 8)));
        Deal d = new Deal(new NoTricksContract(), null, hands, 0, e -> { });
        while (!d.isFinished()) {
            d.play(d.currentSeat(), d.legalMoves(d.currentSeat()).get(0));
        }
        assertThrows(IllegalMoveException.class, () -> d.play(0, Card.parse("7S")));
    }
}
