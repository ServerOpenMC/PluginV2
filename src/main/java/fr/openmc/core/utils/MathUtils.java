package fr.openmc.core.utils;

import com.mojang.math.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {

    /**
     * Rotates a point around an origin using the given rotation angles.
     *
     * @param point    The point to rotate
     * @param origin   The origin to rotate around
     * @param rotation The rotation angles in degrees (x, y, z)
     * @return The rotated point
     */
    public static Vector3f rotatePointAroundOrigin(Vector3f point, Vector3f origin, Quaternionf rotation) {
        Vector3f translatedPoint = new Vector3f(point).sub(origin);
        rotation.transform(translatedPoint);
        translatedPoint.add(origin);

        return translatedPoint;
    }

    /**
     * Creates a transformation based on the given parameters.
     *
     * @param YawRotation   The yaw rotation in degrees
     * @param isSneaking    Whether the entity is sneaking
     * @param isSwimming    Whether the entity is swimming
     * @param isGliding     Whether the entity is gliding
     * @param pitch         The pitch angle in degrees
     * @return A Transformation object representing the transformation
     */
    public static Transformation getTransformation(float YawRotation, boolean isSneaking, boolean isSwimming, boolean isGliding, float pitch) {
        Vector3f offset = new Vector3f(0, 0, 0);
        Vector3f origin = new Vector3f(0, 0, 0);
        Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(-YawRotation % 360));
        if (isSneaking && !isSwimming && !isGliding) {
            rotation.rotateX((float) Math.toRadians(30));
            offset.add(0, 0, 0.2f);
        } else if (isSwimming && !isGliding) {
            rotation.rotateX((float) Math.toRadians(pitch+90));
            offset.add(0, 0.2f, -0.2f);
            origin.add(0, -0.5f, 0);
        } else if (isGliding) {
            return new Transformation(new Vector3f(0), new Quaternionf(), new Vector3f(0), new Quaternionf());
        }
        Vector3f translation;
        if (offset.equals(new Vector3f(0, 0, 0), 0.0001f)) {
            translation = new Vector3f(0, 0, 0);
        } else {
            translation = MathUtils.rotatePointAroundOrigin(
                    offset,
                    origin,
                    rotation
            );
        }
        return new Transformation(translation, rotation, new Vector3f(1), new Quaternionf());
    }
}
