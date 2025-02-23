package rpg.rpg_base.Data;

public enum DataBaseColumn {
    UUID("STRING"),
    USERNAME("STRING"),
    LVL("INTEGER"),
    XP("INTEGER"),
    TOTALXP("INTEGER"),
    ELVL("INTEGER"),
    SLVL("INTEGER"),
    ILVL("INTEGER"),
    ALVL("INTEGER"),
    GOLD("INTEGER"),
    RUNICSIGILS("INTEGER"),
    GUILDMEDALS("INTEGER");

    private final String columnType;

    DataBaseColumn(String type) {
        this.columnType = type;
    }

    public String getColumnType() {
        return columnType;
    }
}
