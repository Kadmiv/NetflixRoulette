package com.kadmiv.filmrepo.utils.databinding

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kadmiv.filmrepo.R

@BindingAdapter("bindImageCrop")
fun bindImageCrop(view: ImageView, path: String?) {
    try {

        if (path == null) {
            Glide.with(view)
                    .load(R.drawable.ic_error)
                    .into(view)
            return
        }
        val options = RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        Glide.with(view)
                .load(Uri.parse(path))
                .thumbnail(
                        Glide.with(view.context)
                                .load(R.raw.waiting_icon)
                )
                .apply(options)
                .into(view)

    } catch (ex: Exception) {
    }
}

@BindingAdapter("bindImage")
fun bindImage(view: ImageView, path: String?) {
    try {

        if (path == null) {
            Glide.with(view)
                    .load(R.drawable.ic_error)
                    .into(view)
            return
        }
        val options = RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        Glide.with(view)
                .load(Uri.parse(path))
                .thumbnail(
                        Glide.with(view.context)
                                .load(R.raw.waiting_icon)
                )
                .apply(options)
                .into(view)

    } catch (ex: Exception) {
    }
}

@BindingAdapter("bindImageInt")
fun bindImageInt(view: ImageView, id: Int) {
    try {

        Glide.with(view)
                .load(id)
                .into(view)

    } catch (ex: Exception) {
    }
}

@BindingAdapter("formatText")
fun formatText(view: TextView, value: String) {
    view.text = "${view.text} $value"
}
