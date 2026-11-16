package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import king.model.Card;
import king.model.CardOrder;
import king.model.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CardOrderTest {

    @Test
    @DisplayName("Козырь бьёт масть захода")
    void trumpBeatsLed() {
        var order = CardOrder.forTrick(Suit.SPADES, Suit.HEARTS);
        assertTrue(order.compare(Card.parse("7H"), Card.parse("AS")) > 0);
    }

    @Test
    @DisplayName("Старшая карта масти захода бьёт младшую")
    void higherLedBeatsLower() {
        var order = CardOrder.forTrick(Suit.SPADES, null);
        assertTrue(order.compare(Card.parse("KS"), Card.parse("QS")) > 0);
    }

    @Test
    @DisplayName("Карта чужой масти не бьёт карту масти захода")
    void offSuitNeverWins() {
        var order = CardOrder.forTrick(Suit.SPADES, null);
        assertTrue(order.compare(Card.parse("AH"), Card.parse("7S")) < 0);
        assertEquals(0, order.compare(Card.parse("AH"), Card.parse("KD")));
    }

    @Test
    @DisplayName("Рука сортируется по масти, внутри масти от старшей")
    void handOrder() {
        List<Card> hand = new ArrayList<>(TestHands.cards("7H AS 9S KH"));
        hand.sort(CardOrder.HAND);
        assertEquals(TestHands.cards("AS 9S KH 7H"), hand);
    }
}
