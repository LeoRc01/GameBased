package com.cip.cipstudio.view.dialog


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.PlatformBottomSheetBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.viewmodel.PlatformDetailsDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso


class PlatformDetailsDialog() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val platformDetails = arguments?.get("platform") as PlatformDetails
        val view = layoutInflater.inflate(R.layout.platform_bottom_sheet, null)
        binding = DataBindingUtil.inflate(inflater, R.layout.platform_bottom_sheet, container, false)
        //binding = DataBindingUtil.inflate(inflater, , container, false)
        binding.platDetails = platformDetails
        if(platformDetails.platformLogo != "")
            Picasso.get().load(platformDetails.platformLogo).into(binding.platformBottomSheetPlatformLogo)
        viewModel = PlatformDetailsDialogViewModel(binding)
        isCancelable = true
        showsDialog = false
        dialog!!.setContentView(binding.root.rootView)
        binding.lifecycleOwner=this
        return binding.root
    }

    private lateinit var binding: PlatformBottomSheetBinding
    private lateinit var viewModel : PlatformDetailsDialogViewModel

}