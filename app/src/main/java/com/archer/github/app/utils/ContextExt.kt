package com.archer.github.app.utils

import android.content.Context
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache

fun Context.getImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
        .components {
            add(SvgDecoder.Factory())
            add(ImageDecoderDecoder.Factory())
        }
        .respectCacheHeaders(false)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizeBytes(100 * 1024 * 1024)
                .build()
        }
        .build()
}
