package ca.lukegrahamlandry.examplemod.obj;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class TestEntity extends Drowned {
    public TestEntity(EntityType<? extends Drowned> entityType, Level level) {
        super(entityType, level);
    }
}
