package rpg.rpg_base.Data;

@Deprecated(since="Latest dev version")
public enum DataBaseColumn {
    UUID("STRING"),
    USERNAME("STRING"),
    LVL("INTEGER"),
    XP("INTEGER"),
    TOTALXP("INTEGER"),
    ELVL("INTEGER"),
    SLVL("INTEGER"),
    DLVL("INTEGER"),
    ILVL("INTEGER"),
    ALVL("INTEGER"),
    GOLD("INTEGER"),
    RUNICSIGILS("INTEGER"),
    GUILDMEDALS("INTEGER"),
    SPENTSKILLPOINTS("INTEGER"),
    SPENTABILITYPOINTS("INTEGER"),
    UNLOCKEDABILITIES("INTEGER");

    private final String columnType;

    DataBaseColumn(String type) {
        this.columnType = type;
    }

    public String getColumnType() {
        return columnType;
    }
}
