package king.rules;

import king.model.Trick;

/** Кон на взятие: с козырем, +3 очка за каждую взятку. */
public final class TakeTricksContract implements Contract {
    /** Награда за взятку. */
    public static final int REWARD = 3;

    private final int number;

    /**
     * Создаёт кон на взятие.
     *
     * @param number порядковый номер кона на взятие (1–4), нужен только для названия
     */
    public TakeTricksContract(int number) {
        this.number = number;
    }

    @Override
    public String title() {
        return "Брать взятки №" + number;
    }

    @Override
    public boolean usesTrump() {
        return true;
    }

    @Override
    public int trickPoints(Trick trick, int trickIndex) {
        return REWARD;
    }
}
