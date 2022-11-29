package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigGridViewAdapter
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentHistoryBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private lateinit var historyBinding: FragmentHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        historyViewModel = HistoryViewModel(historyBinding)

        initializeFavourites()
        historyBinding.vm = historyViewModel
        historyBinding.lifecycleOwner = this


        return historyBinding.root
    }

    private fun initializeFavourites() {
        historyViewModel.initialize {
            val adapter = GamesBigRecyclerViewAdapter(requireContext(), it, ActionGameDetailsEnum.HISTORY)

            val manager = LinearLayoutManager(requireContext())
            manager.orientation = RecyclerView.VERTICAL
            historyBinding.fGameListRvGames.layoutManager = manager
            historyBinding.fGameListRvGames.setItemViewCacheSize(50)
            historyBinding.fGameListRvGames.itemAnimator = null
            historyBinding.fGameListRvGames.adapter = adapter
        }
    }

}