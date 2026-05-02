package fr.openmc.core.bootstrap.features.types;

import fr.openmc.core.bootstrap.hooks.Hooks;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Charge la feature ou le listener si un hook est active.
 * Le hook est determine par le parametre generique de l'interface.
 */
public interface LoadIfEnable <T extends Hooks> {
    /**
     * Indique si la feature doit etre chargee selon l'etat du hook cible.
     *
     * @return True si le hook est actif et la feature doit etre chargée
     */
    default boolean shouldLoad() {
        Class<? extends Hooks> hookClass = resolveHookClass();
        if (hookClass == null) {
            return false;
        }
        try {
            Method method;
            try {
                method = hookClass.getMethod("isEnable");
            } catch (NoSuchMethodException e) {
                new RuntimeException("Le hook " + hookClass.getSimpleName() + " doit avoir une méthode statique isEnable()").printStackTrace();
                return false;
            }
            Object result = method.invoke(null);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Resolue la classe du hook a partir du parametre generique.
     *
     * @return Classe du hook cible, ou null si non resolu
     */
    private Class<? extends Hooks> resolveHookClass() {
        for (Type type : getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType parameterizedType) {
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class<?> rawClass && LoadIfEnable.class.isAssignableFrom(rawClass)) {
                    Type arg = parameterizedType.getActualTypeArguments()[0];
                    if (arg instanceof Class<?> hookClass && Hooks.class.isAssignableFrom(hookClass)) {
                        return (Class<? extends Hooks>) hookClass;
                    }
                }
            }
        }
        return null;
    }
}
