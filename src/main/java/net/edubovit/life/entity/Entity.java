package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import lombok.Data;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import static net.edubovit.life.utils.Random.RANDOM;

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

    protected abstract ChildProbability[] childProbabilities();

    protected EntityType child() {
        float roll = RANDOM.nextFloat();
        for (var childProbability : childProbabilities()) {
            if (roll < childProbability.probability) {
                return childProbability.type;
            }
        }
        return getType();
    }

    protected Cell chooseRandomVacantDirection() {
        var candidates = cell.getNeighbours()
                .stream()
                .filter(neighbour -> !neighbour.hasEntity())
                .toList();
        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return candidates.get(0);
        } else {
            return candidates.get(RANDOM.nextInt(candidates.size()));
        }
    }

    protected record ChildProbability(EntityType type, float probability) {
    }

}
