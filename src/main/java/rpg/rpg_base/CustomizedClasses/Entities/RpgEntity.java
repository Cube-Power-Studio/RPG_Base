package rpg.rpg_base.CustomizedClasses.Entities;

import org.bukkit.entity.Entity;

public abstract class RpgEntity {
    protected int hp;
    protected int maxHp;
    protected int baseMaxHp;

    protected int def;
    protected int dmg;
    protected int baseDmg;

    public int level = -1;

    public RpgEntity(){
    }

    public abstract void update();

    public abstract void dealDamage(int amount, Entity damager);

    public void heal(int amount){
        hp = Math.max(0, Math.min(maxHp, hp + amount));
        update();
    }

    public int getMaxHp(){
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getDef() {
        return def;
    }

    public int getDmg() {
        return dmg;
    }
}
