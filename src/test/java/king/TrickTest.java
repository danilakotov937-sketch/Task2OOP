package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import king.model.Card;
import king.model.Suit;
import king.model.Trick;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TrickTest {

    private static Trick trick(Suit trump, String... cards) {
        Trick t = new Trick(4, trump);
        for (int i = 0; i < cards.length; i++) {
            t.add(i, Card.parse(cards[i]));
        }
        return t;
    }

    @Test
    @DisplayName("Без козыря берёт старшая карта масти захода")
    void winnerWithoutTrump() {
        assertEquals(2, trick(null, "9S", "AH", "KS", "QS").winner());
    }

    @Test
    @DisplayName("Козырь берёт взятку")
    void trumpWins() {
        assertEquals(1, trick(Suit.HEARTS, "AS", "7H", "KS", "QS").winner());
    }

    @Test
    @DisplayName("Среди козырей берёт старший")
    void highestTrumpWins() {
        assertEquals(3, trick(Suit.HEARTS, "AS", "7H", "9H", "10H").winner());
    }

    @Test
    @DisplayName("Незавершённая взятка не имеет победителя")
    void incompleteHasNoWinner() {
        Trick t = new Trick(4, null);
        t.add(0, Card.parse("7S"));
        assertThrows(IllegalStateException.class, t::winner);
    }

    @Test
    @DisplayName("В завершённую взятку нельзя добавить карту")
    void cannotOverfill() {
        Trick t = trick(null, "7S", "8S", "9S", "10S");
        assertTrue(t.isComplete());
        assertThrows(IllegalStateException.class, () -> t.add(0, Card.parse("AS")));
    }
}
