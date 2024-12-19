package fr.openmc.core.utils.cooldown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicCooldown {
    /**
     * The cooldown group name
     * @return group name
     */
    String group() default "general";

    /**
     * The message to show when cooldown is active
     * @return message
     */
    String message() default "§cVous devez attendre %sec%s";
    /*
    %sec% | Le temps restant en secondes
    %ms%  | Le temps restant en millisecondes
     */
}