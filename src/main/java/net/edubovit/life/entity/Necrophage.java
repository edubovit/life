package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import java.util.List;

import static java.util.Comparator.comparingInt;
import static net.edubovit.life.entity.EntityType.HUNTER;
import static net.edubovit.life.entity.EntityType.NECROPHAGE;
import static net.edubovit.life.entity.EntityType.SIMPLE;
import static net.edubovit.life.utils.Random.RANDOM;

public class Necrophage extends Entity {

    private static final int MAX_HEALTH = 40;

    private static final int HEALTH_STARVATION = 10;

    private static final float HEALTH_ESCAPE_BASIS = 40;

    private static final int HEALTH_FROM_NECRO = 5;

    private static final int LIFE_EXPECTANCY_FROM_NECRO = 25;

    private static final int HEALTH_MOVE_THRESHOLD = 2;

    private static final int HEALTH_BORN_THRESHOLD = 40;

    private static final int HEALTH_BORN_COST = 30;

    private static final float CHANCE_BORN_SIMPLE = 0.002f;

    private static final float CHANCE_BORN_HUNTER = 0.001f;

    private static final int AGE_OLD_PERCENTAGE = 50;

    private int lifeExpectancy = 100;

    public Necrophage(Cell cell) {
        super(cell);
    }

    @Override
    public void doFeed(Cell cell) {
        if (cell.getNecro() > 0) {
            cell.decNecro();
            lifeExpectancy += LIFE_EXPECTANCY_FROM_NECRO;
            health += HEALTH_FROM_NECRO;
            int maxHealth = maxHealth();
            if (health > maxHealth) {
                health = maxHealth;
            }
        } else if (cell.getFood() > 0) {
            cell.decFood();
            if (health < HEALTH_STARVATION) {
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
        float roll = RANDOM.nextFloat();
        if (roll < CHANCE_BORN_HUNTER) {
            return HUNTER;
        } else if (roll < CHANCE_BORN_SIMPLE) {
            return SIMPLE;
        } else {
            return NECROPHAGE;
        }
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
        }
        var escapeWays = cell.getNeighbours()
                .stream()
                .filter(escape -> !escape.hasEntity())
                .toList();
        if (escapeWays.isEmpty()) {
            return null;
        } else if (escapeWays.size() == 1) {
            return escapeWays.get(0);
        } else {
            return escapeWays.get(RANDOM.nextInt(escapeWays.size()));
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
            return candidates.get(RANDOM.nextInt(candidates.size()));
        }
    }

}
