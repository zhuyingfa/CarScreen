package com.example.carscreen;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CarView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener {

    ScreenFilter screenFilter;

    private SurfaceTexture mCameraTexture;

    /**
     * 在GPU的位置（第几个图层，共32个图层随便传一个都行）
     */
    private int textures = 0;

    public CarView(Context context) {
        super(context);
    }

    public CarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CameraHelper cameraHelper = new CameraHelper((LifecycleOwner) getContext(), this);
        // 设置稳定版：2
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 图像，图层
        mCameraTexture.attachToGLContext(textures);
        // 数据过来，我们去onFrameAvailable
        mCameraTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                // 调用了requestRender()相当于View的invilidate()
                // onDrawFrame()相当于onDraw（）
                // requestRender()被调用后，onDrawFrame()方法被调用
                requestRender();
            }
        });

        screenFilter = new ScreenFilter(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    /**
     * 这个渲染方法会不断的被回调：
     * 1.手动触发（调用requestRender()方法）
     * 2.被动触发（16ms一次）
     *
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d("yvan", "---->1");
        // 获取最新camera数据
        mCameraTexture.updateTexImage();
        screenFilter.onDraw(getWidth(), getHeight(), textures);
    }

    /**
     * 摄像头有画面进来，就会被调用。
     * 约20-30帧/s，跟摄像头有关系（设置了输出就会被调用）
     *
     * @param output
     */
    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        // 数据源
        mCameraTexture = output.getSurfaceTexture();
    }
}
