package com.cip.cipstudio.view.dialog

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.OrientationListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.browser.trusted.ScreenOrientation
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.R
import com.squareup.picasso.Picasso

class GameScreenshotDialog() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.screenshot_full_screen, null)
        val imageView = view.findViewById<ImageView>(R.id.dialog_gameScreenshotDetailsImageView)
        val imageUrl : String = arguments?.getString("imageUrl") as String
        Picasso.get().load(imageUrl).into(imageView)
        dialog!!.setContentView(view)

        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        imageView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LinearLayout.LayoutParams.MATCH_PARENT else 600,
            )
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}