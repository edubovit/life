package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;
import static java.util.Comparator.comparingInt;
import static net.edubovit.life.entity.EntityType.HUNTER;
import static net.edubovit.life.entity.EntityType.NECROPHAGE;
import static net.edubovit.life.entity.EntityType.SIMPLE;

public class Necrophage extends Entity {

    private static final int MAX_HEALTH = 40;

    private static final int HEALTH_STARVATION = 10;

    private static final float HEALTH_ESCAPE_BASIS = 40f;

    private static final int HEALTH_FROM_NECRO = 5;

    private static final int LIFE_EXPECTANCY_FROM_NECRO = 25;

    private static final int HEALTH_MOVE_THRESHOLD = 3;

    private static final int HEALTH_BORN_THRESHOLD = 40;

    private static final int HEALTH_BORN_COST = 30;

    private static final int AGE_OLD_PERCENTAGE = 50;

    private int lifeExpectancy = 100;

    private static final ChildProbability[] childProbabilities = new ChildProbability[] {
            new ChildProbability(HUNTER, 1e-3f),
            new ChildProbability(SIMPLE, 2e-3f)
    };

    public Necrophage(Cell cell) {
        super(cell);
    }

    @Override
    public void doFeed(Cell cell) {
        if (cell.getNecro() > 0) {
            cell.decNecro();
            lifeExpectancy += LIFE_EXPECTANCY_FROM_NECRO;
            health = min(health + HEALTH_FROM_NECRO, maxHealth());
        } else if (cell.getFood() > 0) {
            cell.decFood();
            if (health < HEALTH_STARVATION && health < maxHealth()) {
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
        if (candidate != null) {
            int candidatePoints = 10000 * candidate.getNecro() + candidate.getFood();
            int currentPoints = 10000 * cell.getNecro() + cell.getFood();
            if (currentPoints == 0 || 10 * candidatePoints / currentPoints > 15) {
                return candidate;
            }
        }
        return null;
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
        return age >= lifeExpectancy || health < 0;
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public EntityType getType() {
        return NECROPHAGE;
    }

    @Override
    protected ChildProbability[] childProbabilities() {
        return childProbabilities;
    }

    private int maxHealth() {
        int ageOld = lifeExpectancy * AGE_OLD_PERCENTAGE / 100;
        if (age < ageOld) {
            return MAX_HEALTH;
        } else {
            return MAX_HEALTH * (lifeExpectancy - age) / (lifeExpectancy - ageOld);
        }
    }

    private Cell chooseTastyDirection(List<Cell> candidates) {
        candidates = candidates.stream()
                .filter(cell -> !cell.hasEntity())
                .toList();
        if (candidates.isEmpty()) {
            return null;
        }
        var tastiestCell = candidates.stream()
                .max(comparingInt(Cell::getNecro).thenComparing(Cell::getFood))
                .get();
        candidates = candidates.stream()
                .filter(cell -> cell.getNecro() == tastiestCell.getNecro() && cell.getFood() == tastiestCell.getFood())
                .toList();
        if (candidates.size() == 1) {
            return candidates.get(0);
        } else {
            return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        }
    }

}
