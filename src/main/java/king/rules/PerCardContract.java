package king.rules;

import java.util.function.ToIntFunction;
import king.model.Card;
import king.model.Trick;

/**
 * Кон, в котором очки зависят от карт во взятке: за каждую «плохую» карту начисляется штраф.
 * Стоимость карты задаётся лямбдой.
 */
public class PerCardContract implements Contract {
    private final String title;
    private final ToIntFunction<Card> cost;

    /**
     * Создаёт кон со стоимостью карты, заданной лямбдой.
     *
     * @param title название кона
     * @param cost  очки за карту (отрицательные — штраф)
     */
    public PerCardContract(String title, ToIntFunction<Card> cost) {
        this.title = title;
        this.cost = cost;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public int trickPoints(Trick trick, int trickIndex) {
        return trick.cards().stream().mapToInt(cost).sum();
    }
}
