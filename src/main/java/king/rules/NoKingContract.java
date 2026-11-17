package king.rules;

import java.util.List;
import king.model.Card;

/**
 * Кон «не брать кинга»: 16 штрафных очков за короля червей.
 * Заходить червой нельзя, пока есть другая масть; если нет масти захода, кинга нужно сбросить.
 */
public final class NoKingContract extends PerCardContract {
    /** Штраф за кинга. */
    public static final int PENALTY = -16;

    /** Создаёт кон «не брать кинга». */
    public NoKingContract() {
        super("Не брать кинга", c -> c.isKingOfHearts() ? PENALTY : 0);
    }

    @Override
    public List<Card> legalLeads(List<Card> hand) {
        return new NoHeartsContract().legalLeads(hand);
    }

    @Override
    public List<Card> legalDiscards(List<Card> hand) {
        return hand.stream().anyMatch(Card::isKingOfHearts)
                ? hand.stream().filter(Card::isKingOfHearts).toList()
                : hand;
    }
}
