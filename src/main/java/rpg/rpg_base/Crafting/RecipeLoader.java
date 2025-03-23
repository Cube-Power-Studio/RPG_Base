package rpg.rpg_base.Crafting;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemClass;
import rpg.rpg_base.Utils.Util;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.List;

public class RecipeLoader {
    private static Util util = new Util();

    public static void loadRecipe(YamlConfiguration cfg) {
        for (String key : cfg.getKeys(false)) {
            ItemStack[] recipe;
            ItemStack result;
            ItemClass category;
            String name;

            List<String> loadedRecipe = cfg.getStringList(key + ".shape");
            ItemStack[] finishedRecipe = new ItemStack[9];
            HashMap<String, ItemStack> ingredientsMap = new HashMap<>();

            for (String ingredient : cfg.getConfigurationSection(key + ".ingredients").getKeys(false)) {
                ingredientsMap.put(ingredient, CItem.customItemsByName.getOrDefault(cfg.getString(key + ".ingredients." + ingredient), new CItem(RPG_Base.getInstance(), util)).getItem());
            }

            int i = 0;
            for (String recipeLine : loadedRecipe) {
                for (String item : recipeLine.split(",")) {
                    finishedRecipe[i] = ingredientsMap.getOrDefault(item, new ItemStack(Material.AIR));
                    i++;
                }
            }

            recipe = finishedRecipe;
            if(cfg.contains(key + ".amount")){
                ItemStack temp = CItem.customItemsByName.get(cfg.getString(key + ".result")).getItem();
                temp.setAmount(cfg.getInt(key + ".amount") > 0 ? cfg.getInt(key + ".amount") : 1);
                result = temp;
            }else{
                result = CItem.customItemsByName.get(cfg.getString(key + ".result")).getItem();
            }

            category = ItemClass.valueOf(result.getItemMeta().getPersistentDataContainer().get(CItem.itemClass, PersistentDataType.STRING));

            name = cfg.contains(key + ".name") ? cfg.getString(key + ".name") : "RECIPE NAME NOT FOUND";

            if (result.getType() != Material.AIR) {
                Recipe recipeClass = new Recipe(recipe, result, category, name);
                CraftingHandler.addRecipe(recipeClass);
            } else {
                RPG_Base.getInstance().getLogger().warning("Error loading recipe " + key + ", Item can't be null or air");
            }
        }
    }
}
