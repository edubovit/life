package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Comparator.comparingInt;
import static net.edubovit.life.entity.EntityType.CRAZY;
import static net.edubovit.life.entity.EntityType.HUNTER;
import static net.edubovit.life.entity.EntityType.NECROPHAGE;
import static net.edubovit.life.entity.EntityType.SIMPLE;

public class SimpleEntity extends Entity {

    private static final int MAX_HEALTH = 20;

    private static final float HEALTH_ESCAPE_BASIS = 20f;

    private static final int HEALTH_MOVE_THRESHOLD = 2;

    private static final int HEALTH_BORN_THRESHOLD = 20;

    private static final int HEALTH_BORN_COST = 10;

    private static final int AGE_DEATH = 100;

    private static final int AGE_OLD = 60;

    private static final ChildProbability[] childProbabilities = new ChildProbability[] {
            new ChildProbability(HUNTER, 1e-6f),
            new ChildProbability(CRAZY, 2e-5f),
            new ChildProbability(NECROPHAGE, 1e-4f)
    };

    public SimpleEntity(Cell cell) {
        super(cell);
    }

    @Override
    public void doFeed(Cell cell) {
        if (cell.getFood() > 0) {
            cell.decFood();
            if (health < maxHealth()) {
                health++;
            }
        } else {
            health--;
        }
    }

    @Override
    public Cell decideBorn() {
        if (health >= HEALTH_BORN_THRESHOLD) {
            return chooseTastyDirection(cell.getNeighbours());
        } else {
            return null;
        }
    }

    @Override
    public EntityType doBorn() {
        health -= HEALTH_BORN_COST;
        return child();
    }

    @Override
    public Cell decideMove() {
        if (health < HEALTH_MOVE_THRESHOLD) {
            return null;
        }
        var candidate = chooseTastyDirection(cell.getNeighbours());
        if (candidate == null || (cell.getFood() > 0 && 100 * candidate.getFood() / cell.getFood() < 150)) {
            return null;
        } else {
            return candidate;
        }
    }

    @Override
    public MovementResult doMove(Cell cell) {
        health--;
        return null;
    }

    @Override
    public Cell canEscape() {
        if (health < HEALTH_MOVE_THRESHOLD) {
            return null;
        } else {
            return chooseRandomVacantDirection();
        }
    }

    @Override
    public float getEscapeChance() {
        return health / HEALTH_ESCAPE_BASIS;
    }

    @Override
    public boolean isDead() {
        return age >= AGE_DEATH || health < 0;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public EntityType getType() {
        return SIMPLE;
    }

    @Override
    protected ChildProbability[] childProbabilities() {
        return childProbabilities;
    }

    private int maxHealth() {
        if (age <= AGE_OLD) {
            return MAX_HEALTH;
        } else {
            return MAX_HEALTH * (AGE_DEATH - age) / (AGE_DEATH - AGE_OLD);
        }
    }

    private Cell chooseTastyDirection(List<Cell> candidates) {
        candidates = candidates.stream()
                .filter(cell -> !cell.hasEntity())
                .toList();
        if (candidates.isEmpty()) {
            return null;
        }
        int maxFood = candidates.stream()
                .max(comparingInt(Cell::getFood))
                .get()
                .getFood();
        candidates = candidates.stream()
                .filter(cell -> cell.getFood() == maxFood)
                .toList();
        if (candidates.size() == 1) {
            return candidates.get(0);
        } else {
            return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        }
    }

}
