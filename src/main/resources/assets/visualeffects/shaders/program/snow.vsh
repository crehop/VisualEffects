#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;

out vec2 texCoord;
out float depth;

void main() {
    vec3 pos = Position;
    pos.y += sin(GameTime * 3.0 + Position.x * 0.5 + Position.z * 0.5) * 0.025;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    texCoord = UV0;
    depth = gl_Position.z;
}