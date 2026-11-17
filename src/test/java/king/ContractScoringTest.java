package king;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import king.model.Card;
import king.model.Trick;
import king.rules.Contract;
import king.rules.Contracts;
import king.rules.NoHeartsContract;
import king.rules.NoKingContract;
import king.rules.NoLastTwoContract;
import king.rules.NoTricksContract;
import king.rules.TakeTricksContract;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContractScoringTest {

    private static Trick trick(String... cards) {
        Trick t = new Trick(4, null);
        for (int i = 0; i < cards.length; i++) {
            t.add(i, Card.parse(cards[i]));
        }
        return t;
    }

    @Test
    @DisplayName("Не брать взяток: -2 за взятку")
    void noTricks() {
        assertEquals(-2, new NoTricksContract().trickPoints(trick("7S", "8S", "9S", "10S"), 0));
    }

    @Test
    @DisplayName("Не брать червей: -2 за каждую червовую карту")
    void noHearts() {
        assertEquals(-4, new NoHeartsContract().trickPoints(trick("7H", "8H", "9S", "10S"), 0));
        assertEquals(0, new NoHeartsContract().trickPoints(trick("7S", "8S", "9S", "10S"), 0));
    }

    @Test
    @DisplayName("Не брать дам: -4 за каждую даму")
    void noQueens() {
        assertEquals(-8, Contracts.noQueens().trickPoints(trick("QS", "QH", "9S", "10S"), 0));
    }

    @Test
    @DisplayName("Не брать королей и валетов: -2 за карту")
    void noKingsAndJacks() {
        assertEquals(-6, Contracts.noKingsAndJacks().trickPoints(trick("KS", "JH", "JS", "10S"), 0));
    }

    @Test
    @DisplayName("Две последние: штраф только за 7-ю и 8-ю взятки")
    void lastTwo() {
        Contract c = new NoLastTwoContract();
        Trick t = trick("7S", "8S", "9S", "10S");
        assertEquals(0, c.trickPoints(t, 5));
        assertEquals(-8, c.trickPoints(t, 6));
        assertEquals(-8, c.trickPoints(t, 7));
    }

    @Test
    @DisplayName("Кинг: -16 только за короля червей")
    void king() {
        Contract c = new NoKingContract();
        assertEquals(-16, c.trickPoints(trick("KH", "8S", "9S", "10S"), 0));
        assertEquals(0, c.trickPoints(trick("KS", "8H", "9S", "10S"), 0));
    }

    @Test
    @DisplayName("Кон на взятие: +3 за взятку, есть козырь")
    void takeTricks() {
        Contract c = new TakeTricksContract(1);
        assertEquals(3, c.trickPoints(trick("7S", "8S", "9S", "10S"), 0));
        assertTrue(c.usesTrump());
    }

    @Test
    @DisplayName("Стандартный набор: 10 конов, козырь только у последних четырёх")
    void standardTen() {
        List<Contract> all = Contracts.standardTen();
        assertEquals(10, all.size());
        assertEquals(4, all.stream().filter(Contract::usesTrump).count());
        assertFalse(all.get(0).usesTrump());
    }
}
