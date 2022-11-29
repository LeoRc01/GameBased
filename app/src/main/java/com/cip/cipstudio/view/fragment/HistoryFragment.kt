package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentHistoryBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var historyBinding: FragmentHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        historyViewModel = HistoryViewModel(historyBinding)

        initializeHistory()
        historyBinding.vm = historyViewModel
        historyBinding.lifecycleOwner = this

        historyBinding.fHistoryIvDelete.setOnClickListener {
            lifecycleScope.launch{
                historyViewModel.deleteHistory()
                initializeHistory()
                Toast.makeText(context, "History deleted", Toast.LENGTH_SHORT).show()
            }
        }

        return historyBinding.root
    }

    private fun initializeHistory() {
        historyViewModel.initialize {
            val adapter = GamesBigRecyclerViewAdapter(requireContext(), it, ActionGameDetailsEnum.HISTORY)

            val manager = LinearLayoutManager(requireContext())
            manager.orientation = RecyclerView.VERTICAL
            historyBinding.fHistoryRvGames.layoutManager = manager
            historyBinding.fHistoryRvGames.setItemViewCacheSize(50)
            historyBinding.fHistoryRvGames.itemAnimator = null
            historyBinding.fHistoryRvGames.adapter = adapter
        }
    }

}