int getDimension(vec3 biomeFog, float cloudFogDistance) {
    float bloodyCFD = 512.0032;
    if (approxEquals(cloudFogDistance, bloodyCFD, 0.00001)) return 1; // Bloody Ambient
    return 0;
}