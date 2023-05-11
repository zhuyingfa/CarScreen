#extension GL_OES_EGL_image_external : require
precision lowp float;
//接受的坐标
varying vec2 aCoord;
//[]  对象 片元
uniform samplerExternalOES vTexture;
// 顶点 形状     这么确定形状
void main() {

    float y= aCoord.y;
    if(y<0.5)
    {
        y+=0.25;
    }else{
        y -= 0.25;
    }
    gl_FragColor= texture2D(vTexture, vec2( y,aCoord.x));

//    vec4 src = texture2D(vTexture, aCoord);
//    gl_FragColor = src;
}
