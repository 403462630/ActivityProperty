package com.activity.property;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.LinkedHashMap;

/**
 * Created by rjhy on 15-5-29.
 */
public class GifLruCache extends LruCache<String, Byte[]> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public GifLruCache(int maxSize) {
        super(maxSize);
    }
}
