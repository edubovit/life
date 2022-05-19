package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;
import static net.edubovit.life.entity.EntityType.CRAZY;
import static net.edubovit.life.entity.EntityType.SIMPLE;

public class Crazy extends Entity {

    private static final int MAX_HEALTH = 30;

    private static final float HEALTH_ESCAPE_BASIS = 50f;

    private static final int HEALTH_MOVE_THRESHOLD = 2;

    private static final int HEALTH_BORN_THRESHOLD = 15;

    private static final int HEALTH_BORN_COST = 5;

    private static final int AGE_DEATH = 60;

    private static final int AGE_OLD = 30;

    private static final ChildProbability[] childProbabilities = new ChildProbability[] {
            new ChildProbability(SIMPLE, 1e-4f)
    };

    public Crazy(Cell cell) {
        super(cell);
    }

    @Override
    public void doFeed(Cell cell) {
        int maxHealth = maxHealth();
        if (cell.getFood() > 1 && health + 1 < maxHealth) {
            cell.decFood(2);
            health += 2;
        } else if (cell.getFood() > 0) {
            cell.decFood();
            if (health < maxHealth) {
                health++;
            }
        } else {
            health--;
        }
    }

    @Override
    public Cell decideBorn() {
        if (health >= HEALTH_BORN_THRESHOLD
                && (health == maxHealth() || ThreadLocalRandom.current().nextFloat() < bornProbability())) {
            return chooseRandomVacantDirection();
        } else {
            return null;
        }
    }

    @Override
    public EntityType doBorn() {
        int bornFeed = min(2, cell.getFood());
        cell.decFood(bornFeed);
        health -= HEALTH_BORN_COST - bornFeed;
        return child();
    }

    @Override
    public Cell decideMove() {
        if (health < HEALTH_MOVE_THRESHOLD) {
            return null;
        } else {
            return chooseRandomVacantDirection();
        }
    }

    @Override
    public MovementResult doMove(Cell cell) {
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
        return Color.CYAN;
    }

    @Override
    public EntityType getType() {
        return CRAZY;
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

    private float bornProbability() {
        return (float) (health - HEALTH_BORN_THRESHOLD) / (MAX_HEALTH - HEALTH_BORN_THRESHOLD) / 2;
    }

}
