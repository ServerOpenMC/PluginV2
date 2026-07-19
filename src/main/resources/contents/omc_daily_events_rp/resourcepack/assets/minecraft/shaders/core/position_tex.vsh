#version 330
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:fog.glsl>
#moj_import <omc_daily_events_rp:compare_float.glsl>
#moj_import <omc_daily_events_rp:get_dimension.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform sampler2D Sampler0;

out vec2 texCoord0;
flat out int isCelestial;
flat out float frames;
flat out float textureShift;
flat out float textureHeight;
flat out vec2 atlasSize;

bool isMarker(vec4 color, vec4 compareColor) {
    return all(lessThan(abs(color.rgb - compareColor.rgb), vec3(0.004)));
}

int detectMarker(vec2 uv) {
    ivec2 texelCoord = ivec2(uv * atlasSize);
    vec4 c = texelFetch(Sampler0, texelCoord, 0);

    if (isMarker(c, vec4(1.0, 1.0, 1.0, 1.0) / 255.0)) return 1; // Sun
    else if (isMarker(c, vec4(3.0, 3.0, 3.0, 1.0) / 255.0)) return 2; // Moon
    return 0;
}

mat3 rotateX(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat3(
        1.0, 0.0, 0.0,
        0.0,  c,  -s,
        0.0,  s,   c
    );
}

mat3 rotateZ(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat3(
         c, -s, 0.0,
         s,  c, 0.0,
        0.0, 0.0, 1.0
    );
}

mat3 rotateY(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat3(
         c, 0.0,  s,
        0.0, 1.0, 0.0,
        -s, 0.0,  c
    );
}

void main() {
    texCoord0 = UV0;
    atlasSize = vec2(textureSize(Sampler0, 0));
    float size = 1.0;
    float tilt = 0.0;
    float SunOffset = 0.0;

    switch (detectMarker(UV0))
    { // Sun
        case 1: {
            isCelestial = 1;
            frames = 1.0;
            textureHeight = 64.0;
            switch (getDimension(FogColor.rgb, FogCloudsEnd))
            {
                default: textureShift = 0.0; size = 1.0;
            } break;
        } // Moon
        case 2: {
            isCelestial = 1;
            frames = 2.0;
            textureHeight = 64.0;
            switch (getDimension(FogColor.rgb, FogCloudsEnd))
            {
                case 1: textureShift = 1.0; size = 3.0; break; // texture de blood moon
                default: textureShift = 0.0; size = 1.0;
            } break;
        }
        default: {
            isCelestial = 0;
        }
    }
    vec3 rotated = rotateY(radians(tilt)) * Position;
    vec4 pos = vec4(rotated, size);
    pos.x += SunOffset;

    gl_Position = ProjMat * ModelViewMat * pos;
}
