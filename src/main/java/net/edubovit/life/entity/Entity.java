package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import lombok.Data;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

@Data
public abstract class Entity {

    protected Cell cell;

    protected int age;

    protected int health;

    public Entity(Cell cell) {
        this.cell = cell;
    }

    public void incAge() {
        age++;
    }

    public abstract void doFeed(Cell cell);

    public abstract Cell decideBorn();

    public abstract EntityType doBorn();

    public abstract Cell decideMove();

    public abstract MovementResult doMove(Cell cell);

    public abstract Cell canEscape();

    public abstract float getEscapeChance();

    public abstract boolean isDead();

    public abstract Color getColor();

    public abstract EntityType getType();

}
