package rpg.rpg_base.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {

    public Component formatYmlString(String str) {
        List<String> formatableHashes = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "f");
        Component formattedComponent = Component.text("");
        formattedComponent = formattedComponent.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);

        String[] words = str.split(" ");
        TextColor currentColor = null;
        Set<TextDecoration> decorations = new HashSet<>();

        for (String word : words) {
            StringBuilder currentText = new StringBuilder();

            boolean inFormatting = false;
            StringBuilder colorCode = new StringBuilder();

            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);

                if (ch == '&') {
                    if (currentText.length() > 0) {
                        Component tempComponent = Component.text(currentText.toString());

                        if (currentColor != null) {
                            tempComponent = tempComponent.color(currentColor);
                        }
                        for (TextDecoration decoration : decorations) {
                            tempComponent = tempComponent.decorate(decoration);
                        }
                        formattedComponent = formattedComponent.append(tempComponent);
                        currentText = new StringBuilder();
                    }
                    if (i + 1 < word.length() && word.charAt(i + 1) == '<') {
                        decorations = new HashSet<>();
                        currentColor = null;

                        inFormatting = true;
                        i++; // Skip '<'
                    }
                    continue;
                }

                if (inFormatting) {
                    if (ch == '>') {
                        inFormatting = false;
                        if (colorCode.length() == 6) {
                            currentColor = TextColor.color(Integer.parseInt(colorCode.toString(), 16));
                        }
                        colorCode.setLength(0);
                        continue;
                    }

                    switch (ch) {
                        case '*' -> decorations.add(TextDecoration.BOLD);
                        case '/' -> decorations.add(TextDecoration.ITALIC);
                        case '-' -> decorations.add(TextDecoration.STRIKETHROUGH);
                        case '_' -> decorations.add(TextDecoration.UNDERLINED);
                        case '#' -> colorCode = new StringBuilder();
                        default -> {
                            if (formatableHashes.contains(String.valueOf(ch))) {
                                colorCode.append(ch);
                            }
                        }
                    }
                    continue;
                }

                currentText.append(ch);
            }

            if (!currentText.isEmpty()) {
                Component tempComponent = Component.text(currentText.toString());
                tempComponent = tempComponent.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
                if (currentColor != null) {
                    tempComponent = tempComponent.color(currentColor);
                }
                for (TextDecoration decoration : decorations) {
                    tempComponent = tempComponent.decorate(decoration);
                }
                formattedComponent = formattedComponent.append(tempComponent);
            }

            formattedComponent = formattedComponent.append(Component.text(" "));
        }

        return formattedComponent;
    }
    public boolean isLocationInRegion(Location loc, String regionName) {
        // Get WorldGuard Plugin
        WorldGuard worldGuard = WorldGuard.getInstance();
        if (worldGuard == null) {
            return false;
        }

        // Convert Bukkit Location to WorldEdit Location
        com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(loc);

        // Get Region Query
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // Get all regions at this location
        ApplicableRegionSet regions = query.getApplicableRegions(weLoc);

        // Check if the specified region is in the list
        return regions.getRegions().stream().anyMatch(region -> region.getId().equalsIgnoreCase(regionName));
    }

    public static String numberToRoman(int number){
        String[] romanNumerals = {
                "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
                "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"
        };

        return romanNumerals[number - 1];
    }
}
