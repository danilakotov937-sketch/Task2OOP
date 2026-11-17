package king.rules;

import king.model.Trick;

/** Кон «не брать две последние взятки»: по 8 штрафных очков за каждую из них. */
public final class NoLastTwoContract implements Contract {
    /** Создаёт экземпляр. */
    public NoLastTwoContract() {
    }

    /** Штраф за каждую из двух последних взяток. */
    public static final int PENALTY = -8;

    @Override
    public String title() {
        return "Не брать две последние";
    }

    @Override
    public int trickPoints(Trick trick, int trickIndex) {
        return trickIndex >= TRICKS_PER_DEAL - 2 ? PENALTY : 0;
    }
}
