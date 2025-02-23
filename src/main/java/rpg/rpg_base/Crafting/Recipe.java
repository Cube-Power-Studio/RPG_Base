package rpg.rpg_base.Crafting;

import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemClass;

import java.util.Optional;

public class Recipe {
    public ItemStack[] recipe;
    public ItemStack result;
    public ItemClass category;
    public String name;

    public Recipe(ItemStack[] recipe, ItemStack result, ItemClass category, String name) {
        Optional<ItemClass> categoryResolved = Optional.ofNullable(category);

        this.recipe = recipe;
        this.result = result;
        this.category = categoryResolved.orElse(ItemClass.MISC);
        this.name = name;
    }

    public ItemClass category(){
        return category;
    }

    public String name(){
        return name;
    }

    public ItemStack[] recipe(){
        return recipe;
    }

    public ItemStack result(){
        return result;
    }
}
