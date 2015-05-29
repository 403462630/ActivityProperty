package com.activity.property;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by rjhy on 15-1-15.
 */
public class PictureDialog extends Dialog {
    private static final String TAG = "PictureDialog";
    @InjectView(R.id.tv_save)
    TextView saveVew;
    @InjectView(R.id.tv_close)
    TextView closeView;
    @InjectView(R.id.iv_picture)
    ImageView pictureView;
    @InjectView(R.id.pb_image_progress)
    ProgressBar imageProgress;

    private String lastImageUrl = "";
    private ProgressDialog progressDialog;

    private PhotoViewAttacher attacher;

    public PictureDialog(Context context) {
        super(context, android.R.style.Theme_Light);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_picture);
        ButterKnife.inject(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        attacher = new PhotoViewAttacher(pictureView);
        attacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveVew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new Thread(new Runnable() {

                    private File savePicture(File dir) {
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();

                        FileOutputStream fOut = null;
                        File file = new File(dir, UUID.randomUUID() + ".png");
                        try {
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            Uri.fromFile(file);
                            pictureView.buildDrawingCache();
                            Bitmap bitmap = pictureView.getDrawingCache();
                            fOut = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 75, fOut);
                        } catch (IOException e) {
                            e.printStackTrace();
                            file = null;
                        } finally {
                            try {
                                if (fOut != null) {
                                    fOut.flush();
                                    fOut.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            bundle.putString("message", "保存成功");
                            message.setData(bundle);
                            message.sendToTarget();
                        }
                        return file;
                    }

//                    private File saveGifPriture(File dir) {
//                        byte[] bytes = GifImageLoad.with(getContext()).getCache(lastImageUrl);
//                        if (bytes == null || bytes.length < 1) {
//                            return null;
//                        } else {
//                            File file = new File(dir, UUID.randomUUID() + ".gif");
//                            FileOutputStream fileOutputStream = null;
//                            try {
//                                fileOutputStream = new FileOutputStream(file);
//                                fileOutputStream.write(bytes);
//                                fileOutputStream.flush();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                if (fileOutputStream != null) {
//                                    try {
//                                        fileOutputStream.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                            return file;
//                        }
//                    }

                    @Override
                    public void run() {
                        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "ytxmobile" + File.separator);
                        dir.mkdirs();
                        File file = savePicture(dir);
                        addImageToGallery(file.getAbsolutePath());
                    }
                }).start();
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getContext(), msg.getData().get("message")+"", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            dismiss();
        }
    };

    public void addImageToGallery(String filePath) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageProgress.setVisibility(View.GONE);
            pictureView.setImageBitmap(bitmap);
            attacher.update();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageProgress.setVisibility(View.GONE);
            pictureView.setImageDrawable(errorDrawable);
            attacher.update();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageProgress.setVisibility(View.VISIBLE);
        }
    };
    public void show(String imageUrl) {
        show();
        Log.d(TAG, "--imageUrl:" + imageUrl);
        if (lastImageUrl == null || !lastImageUrl.equals(imageUrl)) {
            pictureView.setImageBitmap(null);
        }
        lastImageUrl = imageUrl.replaceAll(" ", "%20");
//        Picasso.with(getContext())
//                .load(lastImageUrl)
//                .error(R.drawable.default_image)
//                .into(target);
        GifImageLoad.with(getContext())
                .load(imageUrl)
                .placeholder(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                .error(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                .into(pictureView);
    }
}
