package fr.openmc.api.cooldown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicCooldown {
    /**
     * The cooldown group name
     * 
     * @return group name
     */
    String group() default "general";

    /**
     * The message to show when cooldown is active
     * 
     * @return message
     */
    String messageKey() default "api.cooldown.must_wait";
    /*
     * <arg:0> | Le temps restant en secondes
     * <arg:1> | Le temps restant en millisecondes
     * <arg:2> | Le temps restant formaté (ex: 1m30s)
     */
}
