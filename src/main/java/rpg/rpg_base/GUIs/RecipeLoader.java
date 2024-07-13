package rpg.rpg_base.GUIs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.RPG_Base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeLoader {
    static ItemStack result;
    static ItemStack[] recipe;

    public static void loadRecipe(YamlConfiguration cfg) {
        for (String key : cfg.getKeys(false)) {
            List<String> loadedRecipe = cfg.getStringList(key + ".shape");
            ItemStack[] finishedRecipe = new ItemStack[9];
            HashMap<String, ItemStack> ingredientsMap = new HashMap<>();

            for (String ingredient : cfg.getConfigurationSection(key + ".ingredients").getKeys(false)) {
                ingredientsMap.put(ingredient, ItemHandlers.getCustomItemByName(cfg.getString(key + ".ingredients." + ingredient)));
            }

            int i = 0;
            for (String recipeLine : loadedRecipe) {
                for (String item : recipeLine.split(",")) {
                    finishedRecipe[i] = ingredientsMap.getOrDefault(item, new ItemStack(Material.AIR));
                    i++;
                }
            }

            recipe = finishedRecipe;
            result = ItemHandlers.getCustomItemByName(cfg.getString(key + ".result"));

            if (recipe != null && result != null && result.getType() != Material.AIR) {
                CraftingHandler.addRecipe(recipe, result);
            } else {
                RPG_Base.getInstance().getLogger().warning("Error loading recipe " + key + ", Item can't be null or air");
            }
            RPG_Base.getInstance().getLogger().fine(ChatColor.RED + "RECIPE " + Arrays.toString(recipe) + " RESULT "+ result);
            recipe = null;
            result = null;
        }
    }
}
