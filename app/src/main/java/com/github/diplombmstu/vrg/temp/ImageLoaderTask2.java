package com.github.diplombmstu.vrg.temp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by vitaly on 26.07.2016.
 */
public class ImageLoaderTask2 extends AsyncTask<AssetManager, Void, Bitmap>
{

    private static final String TAG = "ImageLoaderTask";
    private final WeakReference<VrPanoramaView> viewReference;
    private final VrPanoramaView.Options viewOptions;
    private final File file;

    @Override
    protected Bitmap doInBackground(AssetManager... params) {
        try (InputStream istr = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            Log.e(TAG, "Could not decode default bitmap: " + e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        final VrPanoramaView vw = viewReference.get();
        if(vw != null && bitmap != null) {
            vw.loadImageFromBitmap(bitmap, viewOptions);
        }
    }

    public ImageLoaderTask2(VrPanoramaView view, VrPanoramaView.Options viewOptions, File file) {
        viewReference = new WeakReference<>(view);
        this.viewOptions = viewOptions;
        this.file = file;
    }
}
