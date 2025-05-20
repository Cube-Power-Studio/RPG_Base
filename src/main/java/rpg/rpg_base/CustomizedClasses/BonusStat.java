package rpg.rpg_base.CustomizedClasses;

public class BonusStat implements Cloneable {
    public double amount;
    public bonusType type;

    public BonusStat(double amount, bonusType type) {
        this.amount = amount;
        this.type = type;
    }

    public enum bonusType {
        flat,
        scale
    }

    @Override
    public BonusStat clone() {
        try {
            return (BonusStat) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should never happen
        }
    }
}
