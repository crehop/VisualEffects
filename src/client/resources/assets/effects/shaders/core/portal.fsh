#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    float brightness = (color.r + color.g + color.b) / 3.0;
    color.a = brightness;
    fragColor = color;
}