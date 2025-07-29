package rpg.rpg_base.CustomizedClasses.items.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum ItemRarity {
    COMMON(TextColor.color(Integer.parseInt("c9c9c9", 16))),
    UNCOMMON(NamedTextColor.GREEN),
    RARE(NamedTextColor.BLUE),
    EPIC(NamedTextColor.DARK_PURPLE),
    LEGENDARY(NamedTextColor.GOLD),
    GLITCH(NamedTextColor.BLACK),
    UNIQUE(NamedTextColor.DARK_RED);

    private TextColor color;

    ItemRarity(TextColor color){
        this.color = color;
    }

    public TextColor getColor(){
        return color;
    }

    public void setColor(TextColor color){
        this.color = color;
    }

    public static ItemRarity matchRarity(String rarity){
        switch (rarity.toLowerCase()){
            case "common" -> {
                return COMMON;
            }
            case "uncommon" -> {
                return UNCOMMON;
            }
            case "rare" -> {
                return RARE;
            }
            case "epic" -> {
                return EPIC;
            }
            case "legendary" -> {
                return LEGENDARY;
            }
            case "glitch" -> {
                return GLITCH;
            }
            case "unique" -> {
                return UNIQUE;
            }
            default -> {
                return GLITCH;
            }
        }
    }
}
