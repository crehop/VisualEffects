#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    // Check if the color is black (r, g, and b components are all 0)
    if (color.r == 0.0 && color.g == 0.0 && color.b == 0.0) {
        color.a = 0.0; // Set alpha to 0 (fully transparent)
    } else {
        color.a = 1.0; // Set alpha to 1 (fully opaque)
    }
    fragColor = color;
}
