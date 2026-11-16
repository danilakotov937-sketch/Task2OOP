package king.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * Обобщённое хранилище «по одному значению на место за столом» с круговым порядком.
 * Используется для рук, очков и любых других данных, привязанных к игроку.
 *
 * @param <T> тип значения, хранимого для каждого места
 */
public final class Seats<T> {
    private final List<T> values;

    private Seats(List<T> values) {
        this.values = values;
    }

    /**
     * Создаёт хранилище, заполняя каждое место значением из фабрики.
     *
     * @param count   число мест
     * @param factory функция «номер места → значение»
     * @param <T>     тип значения
     * @return новое хранилище
     */
    public static <T> Seats<T> of(int count, IntFunction<T> factory) {
        List<T> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(factory.apply(i));
        }
        return new Seats<>(list);
    }

    /**
     * Число мест.
     *
     * @return число мест
     */
    public int size() {
        return values.size();
    }

    /**
     * Возвращает значение на месте.
     *
     * @param seat номер места
     * @return значение на месте
     */
    public T get(int seat) {
        return values.get(seat);
    }

    /**
     * Заменяет значение на месте.
     *
     * @param seat  номер места
     * @param value новое значение
     */
    public void set(int seat, T value) {
        values.set(seat, value);
    }

    /**
     * Возвращает следующее место по часовой стрелке.
     *
     * @param seat номер места
     * @return следующее место по часовой стрелке
     */
    public int next(int seat) {
        return (seat + 1) % values.size();
    }

    /**
     * Поток значений в порядке мест.
     *
     * @return поток значений в порядке мест
     */
    public Stream<T> stream() {
        return values.stream();
    }

    /**
     * Неизменяемая копия значений.
     *
     * @return неизменяемая копия значений
     */
    public List<T> toList() {
        return List.copyOf(values);
    }

    /**
     * Строит новое хранилище, применяя функцию к каждому значению.
     *
     * @param f   преобразование
     * @param <R> тип результата
     * @return новое хранилище
     */
    public <R> Seats<R> map(Function<? super T, ? extends R> f) {
        return new Seats<>(new ArrayList<R>(values.stream().<R>map(f).toList()));
    }
}
