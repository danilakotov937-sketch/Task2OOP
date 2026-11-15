package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import king.model.Card;
import king.model.Deck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeckTest {

    @Test
    @DisplayName("В колоде 32 разные карты")
    void deckHas32UniqueCards() {
        List<Card> cards = Deck.standard().cards();
        assertEquals(32, cards.size());
        assertEquals(32, new HashSet<>(cards).size());
    }

    @Test
    @DisplayName("Один и тот же посев даёт одну и ту же тасовку")
    void sameSeedSameShuffle() {
        assertEquals(Deck.standard().shuffle(new Random(7)).cards(),
                Deck.standard().shuffle(new Random(7)).cards());
    }

    @Test
    @DisplayName("Разные посевы дают разные тасовки")
    void differentSeedsDiffer() {
        assertNotEquals(Deck.standard().shuffle(new Random(1)).cards(),
                Deck.standard().shuffle(new Random(2)).cards());
    }
}
