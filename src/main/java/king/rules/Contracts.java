package king.rules;

import java.util.List;
import king.model.Rank;

/**
 * Фабрика стандартного набора из десяти конов.
 */
public final class Contracts {
    private Contracts() {
    }

    /**
     * Не брать дам: по 4 штрафных очка за даму.
     *
     * @return не брать дам: по 4 штрафных очка за даму
     */
    public static Contract noQueens() {
        return new PerCardContract("Не брать дам", c -> c.rank() == Rank.QUEEN ? -4 : 0);
    }

    /**
     * Не брать королей и валетов: по 2 штрафных очка за карту.
     *
     * @return не брать королей и валетов: по 2 штрафных очка за карту
     */
    public static Contract noKingsAndJacks() {
        return new PerCardContract("Не брать королей и валетов",
                c -> c.rank() == Rank.KING || c.rank() == Rank.JACK ? -2 : 0);
    }

    /**
     * Десять конов партии в порядке розыгрыша: шесть штрафных и четыре на взятие.
     * Сумма штрафов (−96) равна сумме наград (+96), поэтому общая сумма очков партии равна нулю.
     *
     * @return список конов
     */
    public static List<Contract> standardTen() {
        return List.of(
                new NoTricksContract(),
                new NoHeartsContract(),
                noQueens(),
                noKingsAndJacks(),
                new NoLastTwoContract(),
                new NoKingContract(),
                new TakeTricksContract(1),
                new TakeTricksContract(2),
                new TakeTricksContract(3),
                new TakeTricksContract(4));
    }
}
