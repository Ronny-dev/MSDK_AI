package com.geoai.msdk_ai;

import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.geoai.msdk_ai.ml.Detect;
import com.geoai.msdk_ai.ml.Model;
import com.google.gson.Gson;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import dji.sdk.keyvalue.key.DJIKey;
import dji.sdk.keyvalue.key.ProductKey;
import dji.v5.common.callback.CommonCallbacks;
import dji.v5.common.error.IDJIError;
import dji.v5.common.video.channel.VideoChannelState;
import dji.v5.common.video.channel.VideoChannelType;
import dji.v5.common.video.decoder.DecoderOutputMode;
import dji.v5.common.video.decoder.VideoDecoder;
import dji.v5.common.video.interfaces.IVideoChannel;
import dji.v5.common.video.interfaces.IVideoDecoder;
import dji.v5.common.video.interfaces.YuvDataListener;
import dji.v5.common.video.stream.PhysicalDeviceCategory;
import dji.v5.common.video.stream.StreamSource;
import dji.v5.manager.KeyManager;
import dji.v5.manager.datacenter.video.VideoStreamManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private TensorBuffer inputFeature0;
    private Detect model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = findViewById(R.id.surface);
        findViewById(R.id.btn_test).setOnClickListener(this);
        findViewById(R.id.btn_enable_surface).setOnClickListener(this);

        initTensorflow();
    }

    private void initTensorflow() {
        try {
            model = Detect.newInstance(this);

            // Creates inputs for reference.
//            inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
            inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 300, 300, 3}, DataType.UINT8);
        } catch (IOException e) {
            // TODO Handle the exception
        }

//        try {
//            classifier = Classifier.create(this, Classifier.Model.FLOAT, Classifier.Device.GPU, 1);
//            Toast.makeText(MainActivity.this, "Tensorflow init succ.", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Log.i("Ronny", "failed to create classifier." + e.toString());
//            e.printStackTrace();
//        }
    }



    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enable_surface:
                enableVideoFrame();
                break;
            case R.id.btn_test:
                break;
        }
    }

    private void enableVideoFrame() {
        Boolean isAircraftConnected = KeyManager.getInstance().getValue(DJIKey.create(ProductKey.KeyConnection));
        if (isAircraftConnected == null || !isAircraftConnected) {
            Toast.makeText(MainActivity.this, "无人机未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        //获取可用的码流源
        List<StreamSource> availableStreamSources = VideoStreamManager.getInstance().getAvailableStreamSources();
        if (availableStreamSources == null || availableStreamSources.size() == 0) {
            Toast.makeText(MainActivity.this, "相机未连接或为获取到视频源", Toast.LENGTH_SHORT).show();
            return;
        }
        StreamSource surfaceStreamSource = null;
        for (StreamSource availableStreamSource : availableStreamSources) {
            if (availableStreamSource.getPhysicalDeviceCategory() == PhysicalDeviceCategory.CAMERA &&
                    !availableStreamSource.getPhysicalDeviceType().getDeviceType().toUpperCase(Locale.ROOT).equals("FOV")) {
                surfaceStreamSource = availableStreamSource;
                Toast.makeText(MainActivity.this, "获取到视频源： " + surfaceStreamSource.getPhysicalDeviceType().getDeviceType(), Toast.LENGTH_SHORT).show();
            }
        }
        if (surfaceStreamSource == null) {
            Toast.makeText(MainActivity.this, "未获取到视频源", Toast.LENGTH_SHORT).show();
            return;
        }

        IVideoChannel availableVideoChannel = VideoStreamManager.getInstance().getAvailableVideoChannel(VideoChannelType.PRIMARY_STREAM_CHANNEL);
        if (availableVideoChannel == null) {
            Toast.makeText(MainActivity.this, "绑定视频流失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (availableVideoChannel.getVideoChannelStatus() == VideoChannelState.SOCKET_ON ||
                availableVideoChannel.getVideoChannelStatus() == VideoChannelState.ON) {
            bindFpv(availableVideoChannel);
            return;
        }
        availableVideoChannel.startChannel(surfaceStreamSource, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "视频源绑定成功", Toast.LENGTH_SHORT).show();
                bindFpv(availableVideoChannel);
            }

            @Override
            public void onFailure(@NonNull IDJIError error) {
                Toast.makeText(MainActivity.this, "视频源绑定失败： " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindFpv(IVideoChannel availableVideoChannel) {
        IVideoDecoder yuvDecoder = new VideoDecoder(
                MainActivity.this,
                availableVideoChannel.getVideoChannelType(),
                DecoderOutputMode.YUV_MODE,
                mSurfaceView.getHolder(),
                mSurfaceView.getWidth(),
                mSurfaceView.getHeight(), false);

        new VideoDecoder(
                MainActivity.this,
                availableVideoChannel.getVideoChannelType(),
                DecoderOutputMode.SURFACE_MODE,
                mSurfaceView.getHolder(),
                mSurfaceView.getWidth(),
                mSurfaceView.getHeight(), false);

        yuvDecoder.addYuvDataListener((mediaFormat, data, width, height) -> {
            if (aa) {

                aa = false;

                long l = System.currentTimeMillis();

                TensorImage tensorImage = new TensorImage(DataType.UINT8);

                tensorImage.load(rawByteArray2RGBABitmap2(data, width, height));

                ImageProcessor imageProcessor = new ImageProcessor
                        .Builder()
                        .add(new ResizeWithCropOrPadOp(300, 300))
                        .add(new NormalizeOp(0f,1f)).build();

                imageProcessor.process(tensorImage);

                Detect.Outputs outputs = model.process(tensorImage.getTensorBuffer());

                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                TensorBuffer outputFeature1 = outputs.getOutputFeature1AsTensorBuffer();
                TensorBuffer outputFeature2 = outputs.getOutputFeature1AsTensorBuffer();
                TensorBuffer outputFeature3 = outputs.getOutputFeature1AsTensorBuffer();

                Log.i("Ronny", "res0: " + new Gson().toJson(outputFeature0));
                Log.i("Ronny", "res1: " + new Gson().toJson(outputFeature1));
                Log.i("Ronny", "res2: " + new Gson().toJson(outputFeature2));
                Log.i("Ronny", "res3: " + new Gson().toJson(outputFeature3));

                Log.i("Ronny", "time: " + (System.currentTimeMillis() - l));

                // Releases model resources if no longer used.
                model.close();
            }
        });
    }

    private boolean aa = true;

    public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
        return bmp;
    }
}