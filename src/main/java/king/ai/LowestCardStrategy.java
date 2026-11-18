package king.ai;

import java.util.Comparator;
import king.model.Card;

/**
 * Простой бот: всегда ходит самой младшей из допустимых карт. Не зависит от случайности.
 */
public final class LowestCardStrategy implements PlayerStrategy {
    /** Создаёт экземпляр. */
    public LowestCardStrategy() {
    }

    @Override
    public Card chooseCard(PlayerView view) {
        return view.legalMoves().stream()
                .min(Comparator.comparing(Card::rank).thenComparing(Card::suit))
                .orElseThrow();
    }
}
