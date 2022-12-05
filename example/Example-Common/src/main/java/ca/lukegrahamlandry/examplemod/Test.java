package ca.lukegrahamlandry.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class Test extends Drowned {
    public Test(EntityType<? extends Drowned> entityType, Level level) {
        super(entityType, level);
    }
}
