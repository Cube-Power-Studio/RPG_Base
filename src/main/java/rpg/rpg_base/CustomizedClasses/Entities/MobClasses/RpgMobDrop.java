package rpg.rpg_base.CustomizedClasses.Entities.MobClasses;

public class RpgMobDrop implements Cloneable {
    CItem item;
    int minAmount;
    int maxAmount;
    float chance;

    public RpgMobDrop(CItem item, int min, int max, float chance){
        this.item = item;
        this.minAmount = min;
        this.maxAmount = max;
        this.chance = chance;
    }

    public CItem getItem() {
        return item;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    @Override
    public RpgMobDrop clone() {
        try {
            RpgMobDrop clone = (RpgMobDrop) super.clone();
            clone.chance = chance;
            clone.maxAmount = maxAmount;
            clone.minAmount = minAmount;
            clone.item = item;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
