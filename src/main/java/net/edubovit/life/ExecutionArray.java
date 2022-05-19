package net.edubovit.life;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
public class ExecutionArray<T> {

    private final List<List<T>> array;

    public ExecutionArray(int sectorSize, int capacity) {
        this.array = new ArrayList<>(sectorSize);
        for (int i = 0; i < sectorSize; i++) {
            this.array.add(new ArrayList<>(capacity));
        }
    }

    private ExecutionArray(List<List<T>> array) {
        this.array = array;
    }

    public void forEach(Consumer<T> action) {
        array.forEach(subarray -> subarray.parallelStream().forEach(action));
    }

    public void clear() {
        array.forEach(List::clear);
    }

    public <R> ExecutionArray<R> map(Predicate<T> filter, Function<T, R> converter) {
        return new ExecutionArray<>(array.stream()
                .map(subarray -> subarray.parallelStream()
                        .filter(filter)
                        .map(converter)
                        .toList())
                .toList());
    }

    public int size() {
        return array.stream()
                .mapToInt(List::size)
                .sum();
    }

}
