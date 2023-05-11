//确定形状vec4   vec

attribute  vec4 vPosition;

//android坐标系  屏幕
attribute vec4 vCoord;

//怎么输出
varying vec2 aCoord;

//vec4  =  [{-1,1},{-1,1},{-1,1},{-1,1},]
void main() {
//    GPU程序内置的系统变量     GPU  矩形
    gl_Position = vPosition;
    aCoord=vCoord.xy;
}
