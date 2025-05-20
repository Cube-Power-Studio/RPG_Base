package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.reflections.Reflections;
import rpg.rpg_base.RPG_Base;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SkillRegistry {
    public static final Map<String, Skill> registeredSkills = new HashMap<>();

    public static void registerAllSkills() {
        Reflections reflections = new Reflections("rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem"); // Package scan
        Set<Class<? extends Skill>> skillClasses = reflections.getSubTypesOf(Skill.class);

        for (Class<? extends Skill> skillClass : skillClasses) {
            if (skillClass.isAnnotationPresent(RegisterSkill.class) && !Modifier.isAbstract(skillClass.getModifiers())) {
                try {
                    RegisterSkill annotation = skillClass.getAnnotation(RegisterSkill.class);
                    Skill skillInstance = skillClass.getDeclaredConstructor().newInstance();
                    registeredSkills.put(annotation.name(), skillInstance);
                    System.out.println("Registered skill: " + annotation.name());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Skill getSkill(String name) {
        return registeredSkills.get(name);
    }
}
