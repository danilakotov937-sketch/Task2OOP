package king.rules;

import java.util.List;
import king.model.Card;
import king.model.Suit;

/**
 * Кон «не брать червей»: по 2 штрафных очка за каждую червовую карту.
 * Заходить червой нельзя, пока в руке есть карты другой масти.
 */
public final class NoHeartsContract extends PerCardContract {
    /** Штраф за червовую карту. */
    public static final int PENALTY = -2;

    /** Создаёт кон «не брать червей». */
    public NoHeartsContract() {
        super("Не брать червей", c -> c.suit() == Suit.HEARTS ? PENALTY : 0);
    }

    @Override
    public List<Card> legalLeads(List<Card> hand) {
        return Contract.hasNonHearts(hand)
                ? hand.stream().filter(c -> c.suit() != Suit.HEARTS).toList()
                : hand;
    }
}
