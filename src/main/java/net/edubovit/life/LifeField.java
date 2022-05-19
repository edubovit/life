package net.edubovit.life;

import net.edubovit.life.entity.Entity;
import net.edubovit.life.entity.EntityType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static net.edubovit.life.Balance.GROW_FOOD_PERIOD;
import static net.edubovit.life.Balance.INITIAL_FOOD;
import static net.edubovit.life.Balance.MAX_NECRO;
import static net.edubovit.life.Balance.NECRO_DECAY_PERIOD;
import static net.edubovit.life.Balance.NECRO_INCREASE;
import static net.edubovit.life.Configuration.SEPARATION_FACTOR;

public class LifeField {

    private final int width;

    private final int height;

    private final LifeView lifeView;

    private final Cell[][] matrix;

    private final ExecutionArray<Cell> separatedCells;

    public LifeField(int width, int height, LifeView lifeView) {
        this.width = width;
        this.height = height;
        this.lifeView = lifeView;
        this.matrix = new Cell[width][height];
        this.separatedCells = new ExecutionArray<>(SEPARATION_FACTOR * SEPARATION_FACTOR,
                (width * height) / (SEPARATION_FACTOR * SEPARATION_FACTOR) + 1);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var cell = new Cell(x, y);
                cell.setFood(INITIAL_FOOD);
                matrix[x][y] = cell;
                separatedCells.getArray()
                        .get(SEPARATION_FACTOR * (y % SEPARATION_FACTOR) + x % SEPARATION_FACTOR)
                        .add(cell);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var cell = matrix[x][y];
                cell.setNeighbours(getNeighbours(cell));
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return matrix[x][y];
    }

    public ExecutionArray<Entity> getEntities() {
        return separatedCells.map(Cell::hasEntity, Cell::getEntity);
    }

    public void redraw(Cell cell) {
        lifeView.draw(cell);
    }

    public void redrawAll() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                lifeView.draw(matrix[x][y]);
            }
        }
    }

    public void bornEntity(int x, int y, EntityType type) {
        var cell = matrix[x][y];
        cell.setEntity(type.newborn(cell));
        lifeView.draw(cell);
    }

    public void moveEntity(Cell from, Cell to) {
        to.setEntity(from.getEntity());
        from.setEntity(null);
        to.getEntity().setCell(to);
        lifeView.draw(from);
        lifeView.draw(to);
    }

    public void killEntity(Entity entity) {
        entity.getCell().setEntity(null);
        entity.getCell().setNecro(entity.getCell().getNecro() + NECRO_INCREASE);
        if (entity.getCell().getNecro() > MAX_NECRO) {
            entity.getCell().setNecro(MAX_NECRO);
        }
        lifeView.draw(entity.getCell());
    }

    public void growFood() {
        separatedCells.forEach(cell -> {
                    if (cell.getFood() < Balance.MAX_FOOD - cell.getNecro()
                            && ThreadLocalRandom.current().nextFloat() < 1.0f / GROW_FOOD_PERIOD) {
                        cell.incFood();
                        lifeView.draw(cell);
                    }
                });
    }

    public void necroDecay() {
        separatedCells.forEach(cell -> {
                    if (cell.getNecro() > 0 && ThreadLocalRandom.current().nextFloat() < 1.0f / NECRO_DECAY_PERIOD) {
                        cell.decNecro();
                        lifeView.draw(cell);
                    }
                });
    }

    public void flushView() {
        lifeView.flush();
    }

    public void renderStatistics(float tps, int entities) {
        lifeView.renderStatistics(tps, entities);
    }

    private List<Cell> getNeighbours(Cell cell) {
        return Stream.of(getCell(cell.getX(), cell.getY() - 1),
                        getCell(cell.getX() + 1, cell.getY()),
                        getCell(cell.getX(), cell.getY() + 1),
                        getCell(cell.getX() - 1, cell.getY()))
                .filter(Objects::nonNull)
                .toList();
    }

}
