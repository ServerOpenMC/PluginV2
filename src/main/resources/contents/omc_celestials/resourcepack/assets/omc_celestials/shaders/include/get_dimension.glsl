int getDimension(vec3 biomeFog, float cloudFogDistance) {
    float bloodCFD        = 512.0032;

    if (approxEquals(cloudFogDistance, bloodCFD, 0.00001))      return 1; // blood moon

    return 0;
}