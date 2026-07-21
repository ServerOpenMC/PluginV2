bool approxEqualsVec3(vec3 v1, vec3 v2, float eps) {
  return all(greaterThan(vec3(eps), abs(v1 - v2)));
}

bool approxEquals(float f1, float f2, float eps) {
  return eps >= abs(f1 - f2);
}