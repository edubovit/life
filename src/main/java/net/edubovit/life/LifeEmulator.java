package net.edubovit.life;

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
            float tps = 1e5f / (now - time);
            lifeField.renderStatistics(tps, entities.size());
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
        lifeField.growFood();
        lifeField.necroDecay();
        cycle++;
        lifeField.flushView();
    }

}
