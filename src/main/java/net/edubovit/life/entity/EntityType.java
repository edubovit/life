package net.edubovit.life.entity;

import lombok.RequiredArgsConstructor;
import net.edubovit.life.Cell;

import java.util.function.Function;

@RequiredArgsConstructor
public enum EntityType {

    SIMPLE(SimpleEntity::new),
    NECROPHAGE(Necrophage::new),
    HUNTER(Hunter::new),
    CRAZY(Crazy::new);

    private final Function<Cell, Entity> born;

    public static EntityType byDigit(int digit) {
        return switch (digit) {
            case 1 -> SIMPLE;
            case 2 -> NECROPHAGE;
            case 3 -> HUNTER;
            case 4 -> CRAZY;
            default -> throw new IllegalArgumentException();
        };
    }

    public Entity newborn(Cell cell) {
        return born.apply(cell);
    }

}
