package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import king.model.Card;
import king.model.Rank;
import king.model.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    @DisplayName("Разбор записи KH")
    void parsesKingOfHearts() {
        assertEquals(new Card(Suit.HEARTS, Rank.KING), Card.parse("KH"));
    }

    @Test
    @DisplayName("Разбор десятки и строчной масти")
    void parsesTenAndLowercase() {
        assertEquals(new Card(Suit.SPADES, Rank.TEN), Card.parse("10s"));
    }

    @Test
    @DisplayName("Некорректная запись — исключение")
    void rejectsGarbage() {
        assertThrows(IllegalArgumentException.class, () -> Card.parse("ZZ"));
        assertThrows(IllegalArgumentException.class, () -> Card.parse("K"));
    }

    @Test
    @DisplayName("Король червей распознаётся как кинг")
    void detectsKing() {
        assertTrue(Card.parse("KH").isKingOfHearts());
        assertFalse(Card.parse("KS").isKingOfHearts());
    }
}
