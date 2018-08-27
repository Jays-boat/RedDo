package com.jayboat.reddo.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine


/*
 by Cynthia at 2018/8/25
 */
class ImageEngine : ImageEngine {

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        val option = RequestOptions.overrideOf(resize, resize).centerCrop()
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(option)
                .into(imageView)
    }

    override fun loadAnimatedGifThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView,
                                          uri: Uri) {
        val option = RequestOptions.placeholderOf(placeholder).override(resize, resize).centerCrop()
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(option)
                .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        val options = RequestOptions.overrideOf(resizeX, resizeY).priority(Priority.HIGH)
        Glide.with(context)
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        val options = RequestOptions.overrideOf(resizeX, resizeY).priority(Priority.HIGH)
        Glide.with(context)
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }
}