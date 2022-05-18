package net.edubovit.life;

import static net.edubovit.life.Balance.*;

public class LifeEmulator implements Runnable {

    private final LifeField lifeField;

    private int cycle = 0;

    private long time = 0;

    public LifeEmulator(LifeField lifeField) {
        this.lifeField = lifeField;
    }

    @Override
    public void run() {
        var entities = lifeField.getEntities();
        if (cycle % 100 == 0) {
            long now = System.currentTimeMillis();
            float fps = 1e5f / (now - time);
            System.out.printf("Cycle %d, %d entities, %.1f fps%n", cycle, entities.size(), fps);
            time = now;
        }
        entities.forEach(entity -> entity.doFeed(entity.getCell()));
        entities.forEach(entity -> {
            var bornDirection = entity.decideBorn();
            if (bornDirection != null) {
                var type = entity.doBorn();
                lifeField.bornEntity(bornDirection.getX(), bornDirection.getY(), type);
                return;
            }
            var moveDirection = entity.decideMove();
            if (moveDirection != null) {
                var movementResult = entity.doMove(moveDirection);
                lifeField.moveEntity(entity.getCell(), moveDirection);
                if (movementResult != null) {
                    lifeField.redraw(movementResult.escapedTo());
                }
            }
        });
        entities.forEach(entity -> {
            entity.incAge();
            if (entity.isDead()) {
                lifeField.killEntity(entity);
            }
        });
        if (cycle % GROW_FOOD_PERIOD == 0) {
            lifeField.growFood();
        }
        if (cycle % NECRO_DECAY_PERIOD == 0) {
            lifeField.necroDecay();
        }
        cycle++;
        lifeField.flushView();
    }

}
