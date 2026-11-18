package king.ai;

import java.util.List;
import king.model.Card;
import king.model.Play;
import king.model.Suit;
import king.rules.Contract;

/**
 * Информация, доступная игроку при выборе хода: его рука и открытые данные стола.
 *
 * @param seat        номер игрока
 * @param hand        его рука
 * @param legalMoves  допустимые карты
 * @param contract    текущий кон
 * @param trump       козырь или {@code null}
 * @param trickSoFar  карты текущей взятки
 * @param playedCards карты, уже сыгранные в этой раздаче
 * @param dealNumber  номер раздачи, с единицы
 * @param totals      суммарные очки игроков до начала этой раздачи
 */
public record PlayerView(int seat, List<Card> hand, List<Card> legalMoves, Contract contract, Suit trump,
                         List<Play> trickSoFar, List<Card> playedCards, int dealNumber, List<Integer> totals) {
}
