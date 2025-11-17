package com.example.revd_up.presentation.views.customer

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Dedicated routes for the multi-step posting flow (where the bottom bar should hide)
object AddPostRoute {
    const val PICK_MEDIA = "post_pick_media"
    const val FILTER_EDIT = "post_filter_edit/{mediaUri}"
    const val CAPTION_DETAILS = "post_caption_details/{mediaUri}/{filterIndex}"

    fun createFilterEditRoute(mediaUri: Uri): String {
        val encodedUri = URLEncoder.encode(mediaUri.toString(), StandardCharsets.UTF_8.toString())
        return "post_filter_edit/$encodedUri"
    }

    fun createCaptionDetailsRoute(mediaUri: Uri, filterIndex: Int): String {
        val encodedUri = URLEncoder.encode(mediaUri.toString(), StandardCharsets.UTF_8.toString())
        return "post_caption_details/$encodedUri/$filterIndex"
    }
}