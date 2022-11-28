package com.cip.cipstudio.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.DialogFragmentCollectionBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.CollectionDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CollectionDialogFragment : BottomSheetDialogFragment() {

    private lateinit var collectionDialogBinding : DialogFragmentCollectionBinding
    private lateinit var collectionDialogViewModel : CollectionDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val collectionName = arguments?.get("collectionName") as String
        val navigator = Navigation.findNavController(requireActivity(), R.id.f_gameDetails)

        collectionDialogBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_collection, container, false)
        collectionDialogBinding.collectionName = collectionName
        collectionDialogBinding.loading = Loading()

        collectionDialogViewModel = CollectionDialogViewModel(collectionName,
            { setGamesCollection(it, navigator) }
        ) {
            collectionDialogBinding.loading!!.isPageLoading.postValue(false)
        }


        collectionDialogBinding.lifecycleOwner = this

        return collectionDialogBinding.root
    }

    private fun setGamesCollection(games : List<GameDetails>, navigator : NavController){
        val adapter = GamesRecyclerViewAdapter(games, ActionGameDetailsEnum.COLLECTION, navigator)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        collectionDialogBinding.dialogCollectionCollectionGamesRecyclerView.layoutManager = linearLayoutManager
        collectionDialogBinding.dialogCollectionCollectionGamesRecyclerView.adapter = adapter
        collectionDialogBinding.dialogCollectionCollectionGamesRecyclerView.itemAnimator = null
        collectionDialogBinding.dialogCollectionCollectionGamesRecyclerView.setItemViewCacheSize(50)
        collectionDialogBinding.dialogCollectionCollectionGamesRecyclerView.adapter = adapter
    }

}