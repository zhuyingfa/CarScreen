package com.example.carscreen;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ScreenFilter {

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;

    int vTexture;

    //4 *2 * 4  视频播放   opengl     视频   旋转   gpu
    float[] VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };
    float[] TEXTURE = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    int program;
    int vPosition;

    int vCoord;
//   String gVertexShader =
//            "attribute vec4 vPosition;\n"+
//            "void main() {\n"+
//            "  gl_Position = vPosition;\n"+
//            "}\n";
////  字符串
//String gFragmentShader =
//            "precision mediump float;\n"+
//            "void main() {\n"+
//            "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n"+
//            "}\n";


    // 准备工作
    public ScreenFilter(Context context) {
        // 对象数组作为入参、出参
        int[] status = new int[1];

        // 1.顶点程序
        String vertexSharder = readRawTextFile(context, R.raw.camera_vert);
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        //加载 顶点程序代码
        GLES20.glShaderSource(vShader, vertexSharder);
        //编译（配置）
        GLES20.glCompileShader(vShader);
        //查看配置 是否成功
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog
                    (vShader));
        }

        // 2.片元程序
        String fragSharder = readRawTextFile(context, R.raw.camera_frag);
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        //加载着色器代码
        GLES20.glShaderSource(fShader, fragSharder);
        //编译（配置）
        GLES20.glCompileShader(fShader);
        //查看配置 是否成功
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog
                    (vShader));
        }

        program = GLES20.glCreateProgram();
        // 执行 这个程序 加载顶点程序和片元程序
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);
        //链接着色器程序
        GLES20.glLinkProgram(program);

        // 在CPU中定位到GPU中，变量的位置
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoord = GLES20.glGetAttribLocation(program, "vCoord");
        // 采样点的坐标
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");
        // 开辟传送通道
        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.clear();
        // 把数据放进去   还没有穿过去
        vertexBuffer.put(VERTEX);

        textureBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.clear();
        textureBuffer.put(TEXTURE);
    }


    // texture  数据源的地方
    public void onDraw(int mWidth, int mHeight, int texture) {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        GLES20.glUseProgram(program);

        // 起始位置 0
        vertexBuffer.position(0);
        textureBuffer.position(0);

        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT,
                false, 0, vertexBuffer);
        // 激活变量
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT,
                false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // texture图层
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        // 激活的意思
        GLES20.glUniform1i(vTexture, 0);
        // 通知gpu绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public String readRawTextFile(Context context, int rawId) {
        InputStream is = context.getResources().openRawResource(rawId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
