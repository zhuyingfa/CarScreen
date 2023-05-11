package com.example.carscreen;

import android.util.Size;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

public class CameraHelper {

    private Preview.OnPreviewOutputUpdateListener listener;

    public CameraHelper(LifecycleOwner lifecycleOwner, Preview.OnPreviewOutputUpdateListener listener) {
        this.listener = listener;
        // 绑定预览，Preview数据在GPU, YUV在CPU
        CameraX.bindToLifecycle(lifecycleOwner, getPreView());
    }

    private Preview getPreView() {
        // 设置配置
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                // 大小
                .setTargetResolution(new Size(640, 480))
                // 后置摄像头
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();
        Preview preview = new Preview(previewConfig);
        // 监听摄像头更新
        preview.setOnPreviewOutputUpdateListener(listener);
        return preview;
    }
}
