package rpg.rpg_base.blocks;

import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import rpg.rpg_base.tags.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerBlock {
    private DyeColor bannerBaseColor;
    private List<Pattern> bannerPattern;

    private static HashMap<String, PatternType> patternKey;
    // bs, mc, cr, drs, dls, hhb, mr, hh, sc, gru, ss, gra, ts, ms, tt
    // bts, tr, tts, sku, cre, tl, vhr, vh, bo, cbo, bri

    // ss, tt
    static {
        patternKey = new HashMap<>();
        patternKey.put("", PatternType.BASE);
        patternKey.put("bo", PatternType.BORDER);
        patternKey.put("bri", PatternType.BRICKS);
        patternKey.put("mc", PatternType.CIRCLE_MIDDLE);
        patternKey.put("cre", PatternType.CREEPER);
        patternKey.put("cr", PatternType.CROSS);
        patternKey.put("cbo", PatternType.CURLY_BORDER);
        patternKey.put("ld", PatternType.DIAGONAL_LEFT);
        patternKey.put("lud", PatternType.DIAGONAL_LEFT_MIRROR);
        patternKey.put("rd", PatternType.DIAGONAL_RIGHT);
        patternKey.put("rud", PatternType.DIAGONAL_RIGHT_MIRROR);
        patternKey.put("flo", PatternType.FLOWER);
        patternKey.put("gra", PatternType.GRADIENT);
        patternKey.put("gru", PatternType.GRADIENT_UP);
        patternKey.put("hh", PatternType.HALF_HORIZONTAL);
        patternKey.put("hhb", PatternType.HALF_HORIZONTAL_MIRROR);
        patternKey.put("vh", PatternType.HALF_VERTICAL);
        patternKey.put("vhr", PatternType.HALF_VERTICAL_MIRROR);
        patternKey.put("moj", PatternType.MOJANG);
        patternKey.put("mr", PatternType.RHOMBUS_MIDDLE);
        patternKey.put("sku", PatternType.SKULL);
        patternKey.put("bl", PatternType.SQUARE_BOTTOM_LEFT);
        patternKey.put("br", PatternType.SQUARE_BOTTOM_RIGHT);
        patternKey.put("tl", PatternType.SQUARE_TOP_LEFT);
        patternKey.put("tr", PatternType.SQUARE_TOP_RIGHT);
        patternKey.put("sc", PatternType.STRAIGHT_CROSS);
        patternKey.put("bs", PatternType.STRIPE_BOTTOM);
        patternKey.put("ms", PatternType.STRIPE_CENTER);
        patternKey.put("dls", PatternType.STRIPE_DOWNLEFT);
        patternKey.put("drs", PatternType.STRIPE_DOWNRIGHT);
        patternKey.put("ls", PatternType.STRIPE_LEFT);
        patternKey.put("ms", PatternType.STRIPE_MIDDLE);
        patternKey.put("rs", PatternType.STRIPE_RIGHT);
        patternKey.put("ss", PatternType.STRIPE_SMALL);
        patternKey.put("ts", PatternType.STRIPE_TOP);
        patternKey.put("bt", PatternType.TRIANGLE_BOTTOM);
        patternKey.put("tt", PatternType.TRIANGLE_TOP);
        patternKey.put("bts", PatternType.TRIANGLES_BOTTOM);
        patternKey.put("tts", PatternType.TRIANGLES_TOP);
    }

    public boolean set(Block block) {
        Banner banner = (Banner) block.getState();
        banner.setBaseColor(bannerBaseColor);
        banner.setPatterns(bannerPattern);
        banner.update(true, false);
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean prep(Map<String, Tag> tileData) {
        // Format for banner is:
        // Patterns = List of patterns
        // id = String "BannerBlock"
        // Base = Int color
        // Then the location
        // z = Int
        // y = Int
        // x = Int
        try {
            // Do the base color
            int baseColor = 15 - ((IntTag) tileData.get("Base")).getValue();
            // //ASkyBlock.getPlugin().getLogger().info("Base value = " +
            // baseColor);
            // baseColor green = 10
            bannerBaseColor = DyeColor.getByDyeData((byte) baseColor);
            // Do the patterns (no idea if this will work or not)
            bannerPattern = new ArrayList<Pattern>();
            ListTag patterns = (ListTag) tileData.get("Patterns");
            if (patterns != null) {
                for (Tag pattern : patterns.getValue()) {
                    // ASkyBlock.getPlugin().getLogger().info("pattern = " +
                    // pattern);
                    // Translate pattern to PatternType
                    if (pattern instanceof CompoundTag) {
                        CompoundTag patternColor = (CompoundTag) pattern;
                        // The tag is made up of pattern (String) and color
                        // (int)
                        Map<String, Tag> patternValue = patternColor.getValue();
                        StringTag mark = (StringTag) patternValue.get("Pattern");
                        Integer markColor = 15 - ((IntTag) patternValue.get("Color")).getValue();
                        // ASkyBlock.getPlugin().getLogger().info("mark = " +
                        // mark.getValue());
                        // ASkyBlock.getPlugin().getLogger().info("color = " +
                        // markColor);
                        DyeColor dColor = DyeColor.getByDyeData(markColor.byteValue());
                        // ASkyBlock.getPlugin().getLogger().info(" dye color = "
                        // + dColor.toString());
                        if (patternKey.containsKey(mark.getValue())) {
                            Pattern newPattern = new Pattern(dColor, patternKey.get(mark.getValue()));
                            bannerPattern.add(newPattern);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
