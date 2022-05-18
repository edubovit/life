package net.edubovit.life;

import lombok.Data;
import net.edubovit.life.entity.Entity;

import java.util.List;

@Data
public class Cell {

    private final int x;

    private final int y;

    private int food;

    private int necro;

    private Entity entity;

    private List<Cell> neighbours;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void incFood() {
        food++;
    }

    public void decFood() {
        food--;
    }

    public void decNecro() {
        necro--;
    }

    public boolean hasEntity() {
        return entity != null;
    }

}
