package com.aitgacem.openmal.glide

import android.content.Context
import android.graphics.drawable.Drawable
import com.aitgacem.openmal.R
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions


@GlideModule
class GlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .transform(RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.corner_radius)))
            .placeholder(R.drawable.border)
        builder.setDefaultRequestOptions(requestOptions)
            .setDefaultTransitionOptions(
                Drawable::class.java, DrawableTransitionOptions.withCrossFade(800)
            )
    }
}