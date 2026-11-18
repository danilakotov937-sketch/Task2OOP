package king.ai;

import java.util.List;
import java.util.Random;
import king.model.Card;

/**
 * Бот, выбирающий случайную допустимую карту. Использует тот же генератор, что и вся партия.
 */
public final class RandomStrategy implements PlayerStrategy {
    private final Random rng;

    /**
     * Создаёт бота со случайным выбором карты.
     *
     * @param rng общий генератор партии
     */
    public RandomStrategy(Random rng) {
        this.rng = rng;
    }

    @Override
    public Card chooseCard(PlayerView view) {
        List<Card> moves = view.legalMoves();
        return moves.get(rng.nextInt(moves.size()));
    }
}
