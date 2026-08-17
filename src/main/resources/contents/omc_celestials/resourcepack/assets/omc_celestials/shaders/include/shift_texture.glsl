vec2 shiftTextureUV(vec2 uv, float texHeight, float frameCount, float shiftAmount) 
{
    // +1 because the atlas seems to adds a row of pixels around the image (not sure why +2 doesn't seem correct then tho)
    float realHeight = texHeight + 1.0; 

    // Calculate the static corner UV for the texture in the atlas. 
    // This is done by flooring the division of the current pixel coordinate with the total texture height. 
    // This gets the rounded amount of times the texture would fit between the current pixel and the (0,0).
    // Multiply this by the height of the texture to get the corner of each fragment in the texture.
    vec2 origin = vec2(0.0, (floor(uv.y / (realHeight)) * realHeight));

    // Shift the texture to the correct frame
    float shiftBy = shiftAmount * realHeight / frameCount;

    // normalize the texture coordinate, apply the transformations, move it back
    uv -= origin;
    uv *= vec2(1.0, 1.0 / frameCount);
    uv += vec2(0.0, shiftBy);
    uv += origin;

    return uv;
}

vec2 shiftTextureUV_special(vec2 uv, float texHeight, float frameCount, float shiftAmount) 
{
    // +1 because the atlas seems to adds a row of pixels around the image (not sure why +2 doesn't seem correct then tho)
    float realHeight = texHeight + 1.0; 

    // height of the sun texture (+1) that must be accounted for when the moon phase texture is on the same column as the sun texture on the celestial atlas
    float sunHeight = 513.0;

    // Calculate the static corner UV for the texture in the atlas. 
    // This is done by flooring the division of the current pixel coordinate with the total texture height. 
    // This gets the rounded amount of times the texture would fit between the current pixel and the (0,0).
    // Multiply this by the height of the texture to get the corner of each fragment in the texture.
    vec2 origin = vec2(0.0, (floor((uv.y - sunHeight) / (realHeight)) * realHeight) + sunHeight);

    // Shift the texture to the correct frame
    float shiftBy = shiftAmount * realHeight / frameCount;

    // normalize the texture coordinate, apply the transformations, move it back
    uv -= origin;
    uv *= vec2(1.0, 1.0 / frameCount);
    uv += vec2(0.0, shiftBy);
    uv += origin;

    return uv;
}