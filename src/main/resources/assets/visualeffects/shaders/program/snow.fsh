#version 150

uniform sampler2D Sampler0;

in vec2 texCoord;
in float depth;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord);
    float fogFactor = clamp((depth - 0.5) / 0.5, 0.0, 1.0);
    fragColor = vec4(mix(color.rgb, vec3(1.0), fogFactor), color.a);
}