package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterSkill {
    String name();
}
