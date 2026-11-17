package king.rules;

import king.model.Trick;

/** Кон «не брать взяток»: по 2 штрафных очка за каждую взятку. */
public final class NoTricksContract implements Contract {
    /** Создаёт экземпляр. */
    public NoTricksContract() {
    }

    /** Штраф за взятку. */
    public static final int PENALTY = -2;

    @Override
    public String title() {
        return "Не брать взяток";
    }

    @Override
    public int trickPoints(Trick trick, int trickIndex) {
        return PENALTY;
    }
}
