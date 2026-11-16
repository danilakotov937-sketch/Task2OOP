package king;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import king.model.Card;
import king.model.Seats;

/** Вспомогательные методы тестов: руки из текстовой записи. */
final class TestHands {
    private TestHands() {
    }

    static List<Card> cards(String line) {
        return line.isBlank() ? new ArrayList<>()
                : new ArrayList<>(Arrays.stream(line.trim().split("\\s+")).map(Card::parse).toList());
    }

    static Seats<List<Card>> hands(String h0, String h1, String h2, String h3) {
        String[] rows = {h0, h1, h2, h3};
        return Seats.of(4, i -> cards(rows[i]));
    }
}
