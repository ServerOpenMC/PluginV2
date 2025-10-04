package fr.openmc.core.utils;

import net.minecraft.util.Mth;

public class PlayerBodyUtils {

    /**
     * Adjusts the yaw rotation of a player's body based on positional and rotational data,
     * ensuring smooth transitions and constraints between the head and body rotations.
     * Taken from the minecraft client's code
     * If you have any question, ask Mojang, not me I have no idea what this code does
     *
     * @param attackAnim    The animation progress for an attack, where a value greater than 0 adjusts the rotation.
     * @param headYawRotation          The current yaw rotation of the player's head in degrees.
     * @param currentXPosition             The player's current x-coordinate.
     * @param currentZPosition             The player's current z-coordinate.
     * @param previousXPosition            The player's previous x-coordinate.
     * @param previousZPosition            The player's previous z-coordinate.
     * @param previousBodyYawRotation     The previous body yaw rotation of the player in degrees.
     * @return The updated body yaw rotation of the player.
     */
    public static float getBodyYaw(float attackAnim, float headYawRotation, float currentXPosition, float currentZPosition, float previousXPosition, float previousZPosition, float previousBodyYawRotation) {
        float yBodyRot = previousBodyYawRotation;
        double deltaX = currentXPosition - previousXPosition;
        double deltaZ = currentZPosition - previousZPosition;
        float movement = (float)(deltaX * deltaX + deltaZ * deltaZ);
        float targetBodyYaw = yBodyRot;
        if (movement > 0.0025000002F) {
            float calculatedYaw = (float)Mth.atan2(deltaZ, deltaX) * (180.0F / (float)Math.PI) - 90.0F;
            float yawDifference = Math.abs(Mth.wrapDegrees(headYawRotation) - calculatedYaw);
            if (95.0F < yawDifference && yawDifference < 265.0F) {
                targetBodyYaw = calculatedYaw - 180.0F;
            } else {
                targetBodyYaw = calculatedYaw;
            }
        }

        if (attackAnim > 0.0F) {
            targetBodyYaw = headYawRotation;
        }

        yBodyRot = tickHeadTurn(targetBodyYaw, yBodyRot, headYawRotation);

        while (yBodyRot - previousBodyYawRotation < -180.0F) {
            previousBodyYawRotation -= 360.0F;
        }

        while (yBodyRot - previousBodyYawRotation >= 180.0F) {
            previousBodyYawRotation += 360.0F;
        }
        return yBodyRot;
    }

    private static float tickHeadTurn(float targetYaw, float yBodyRot, float headYawRotation) {
        float yawAdjustment = Mth.wrapDegrees(targetYaw - yBodyRot);
        yBodyRot += yawAdjustment * 0.3F;
        float headToBodyYawDifference = Mth.wrapDegrees(headYawRotation - yBodyRot);
        float maxHeadRotation = getMaxHeadRotationRelativeToBody();
        if (Math.abs(headToBodyYawDifference) > maxHeadRotation) {
            yBodyRot = yBodyRot + (headToBodyYawDifference - Mth.sign(headToBodyYawDifference) * maxHeadRotation);
        }
        return yBodyRot;
    }

    /**
     * Minecraft really made a function just to get a value
     * @return The maximum head rotation relative to the body.
     */
    private static float getMaxHeadRotationRelativeToBody() {
        return 50.0F;
    }
}
