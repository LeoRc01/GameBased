package com.cip.cipstudio.view.dialog


import android.content.Context
import android.util.Log
import android.view.View
import com.cip.cipstudio.databinding.PlatformBottomSheetBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.viewmodel.PlatformDetailsDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlatformDetailsDialog(val context: Context,
                            val platformDetails: PlatformDetails,
                            val view : View) {
    private val dialog : BottomSheetDialog by lazy {
        BottomSheetDialog(context)
    }

    private var binding: PlatformBottomSheetBinding = PlatformBottomSheetBinding.bind(view)
    private var viewModel : PlatformDetailsDialogViewModel

    init {
        binding.platDetails = platformDetails
        Log.i("URL", platformDetails.platformLogo)
        Picasso.get().load(platformDetails.platformLogo).into(binding.platformBottomSheetPlatformLogo)
        viewModel = PlatformDetailsDialogViewModel(binding)
        dialog.setCancelable(true)
        dialog.setContentView(view)


    }

    fun show(){
        dialog.show()
    }

}