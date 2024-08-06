package com.aitgacem.openmal.glide

import android.content.Context
import android.util.TypedValue
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions


@GlideModule
class GlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions().format(DecodeFormat.PREFER_RGB_565)
                .transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            10f,
                            context.resources.displayMetrics
                        ).toInt()
                    )
                )
        )
    }
}