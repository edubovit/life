package net.edubovit.life.entity;

import javafx.scene.paint.Color;
import net.edubovit.life.Cell;
import net.edubovit.life.MovementResult;

import java.util.List;

import static java.util.Comparator.comparingInt;
import static net.edubovit.life.entity.EntityType.HUNTER;
import static net.edubovit.life.utils.Random.RANDOM;

public class Hunter extends Entity {

    private static final int MAX_HEALTH = 50;

    private static final int HEALTH_STARVATION = 20;

    private static final float HEALTH_TRACK_BASIS = 20;

    private static final int HEALTH_MOVE_THRESHOLD = 5;

    private static final int HEALTH_BORN_THRESHOLD = 30;

    private static final int HEALTH_BORN_COST = 20;

    private static final int HEALTH_TRACK_THRESHOLD = 8;

    private static final int AGE_DEATH = 200;

    private static final int AGE_OLD = 50;

    public Hunter(Cell cell) {
        super(cell);
    }

    @Override
    public void doFeed(Cell cell) {
        if (cell.getFood() > 0) {
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
        if (health >= HEALTH_BORN_THRESHOLD && (health == maxHealth() || !haveTargetsToHunt())) {
            return chooseBornDirection(cell.getNeighbours());
        } else {
            return null;
        }
    }

    @Override
    public EntityType doBorn() {
        health -= HEALTH_BORN_COST;
        return HUNTER;
    }

    @Override
    public Cell decideMove() {
        if (health < HEALTH_MOVE_THRESHOLD) {
            return null;
        }
        var scoredNeighbours = cell.getNeighbours()
                .stream()
                .filter(neighbour -> !(neighbour.hasEntity() && neighbour.getEntity().getType() == HUNTER))
                .map(this::scoreCell)
                .toList();
        if (scoredNeighbours.isEmpty()) {
            return null;
        }
        var topCandidate = scoredNeighbours.stream()
                .max(comparingInt(ScoredCell::score))
                .get();
        var candidates = scoredNeighbours.stream()
                .filter(scoredCell -> scoredCell.score == topCandidate.score)
                .toList();
        var chosen = candidates.size() == 1 ? candidates.get(0) : candidates.get(RANDOM.nextInt(candidates.size()));
        if (cell.getFood() == 0 || 10 * chosen.score / cell.getFood() > 12) {
            return chosen.cell;
        } else {
            return null;
        }
    }

    @Override
    public MovementResult doMove(Cell cell) {
        health--;
        if (cell.hasEntity()) {
            var target = cell.getEntity();
            var escapePlan = target.canEscape();
            if (escapePlan == null) {
                eat(target);
            } else if (track(target)) {
                eat(target);
            } else {
                target.doMove(escapePlan);
                return new MovementResult(escapePlan);
            }
        }
        return null;
    }

    @Override
    public Cell canEscape() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getEscapeChance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDead() {
        return age >= AGE_DEATH || health < 0;
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    @Override
    public EntityType getType() {
        return HUNTER;
    }

    private int maxHealth() {
        if (age <= AGE_OLD) {
            return MAX_HEALTH;
        } else {
            return MAX_HEALTH * (AGE_DEATH - age) / (AGE_DEATH - AGE_OLD);
        }
    }

    private void eat(Entity entity) {
        health += 1 + entity.health / 2;
    }

    private boolean track(Entity entity) {
        if (health < HEALTH_TRACK_THRESHOLD) {
            return false;
        }
        float escapeChance = entity.getEscapeChance();
        float tracking = health / HEALTH_TRACK_BASIS;
        return RANDOM.nextFloat() < tracking / (tracking + escapeChance);
    }

    private boolean haveTargetsToHunt() {
        return cell.getNeighbours()
                .stream()
                .anyMatch(neighbour -> neighbour.hasEntity() && neighbour.getEntity().getType() != HUNTER);
    }

    private Cell chooseBornDirection(List<Cell> candidates) {
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
            return candidates.get(RANDOM.nextInt(candidates.size()));
        }
    }

    private ScoredCell scoreCell(Cell cell) {
        int score = 0;
        if (cell.hasEntity()) {
            score += 10000 + 100 * cell.getEntity().health;
        }
        score += cell.getFood();
        return new ScoredCell(cell, score);
    }

    private record ScoredCell(Cell cell, int score) {
    }

}
