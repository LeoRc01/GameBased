package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.HistoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private val TAG = "HistoryFragment"
    private lateinit var historyBinding: FragmentHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private var offset : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        historyViewModel = HistoryViewModel(historyBinding)

        initializeHistory()
        historyBinding.vm = historyViewModel
        historyBinding.lifecycleOwner = this

        historyBinding.fHistoryIvDelete.setOnClickListener {
            if (historyBinding.fHistoryLlEmpty.visibility == View.GONE) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_history))
                    .setMessage(getString(R.string.delete_history_message))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        lifecycleScope.launch{
                            historyViewModel.deleteHistory()
                            historyBinding.fHistoryLlEmpty.visibility = View.VISIBLE
                            historyBinding.fHistoryIvDelete.visibility = View.GONE
                            initializeHistory()
                            Toast.makeText(context, "History deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                Toast.makeText(context, "History is empty", Toast.LENGTH_SHORT).show()
            }
        }

        historyBinding.reusableNoRecentActivitiesInclude.button.setOnClickListener {
            startActivity(Intent(historyBinding.root.context, MainActivity::class.java))
            activity?.finish()
        }


        return historyBinding.root
    }

    private fun initializeHistory() {
        historyViewModel.addMoreGame { it ->
            val adapter = GamesBigRecyclerViewAdapter(requireContext(), it, ActionGameDetailsEnum.HISTORY)
            val manager = LinearLayoutManager(requireContext())
            manager.orientation = RecyclerView.VERTICAL
            historyBinding.fHistoryRvGames.layoutManager = manager
            historyBinding.fHistoryRvGames.setItemViewCacheSize(50)
            historyBinding.fHistoryRvGames.itemAnimator = null
            historyBinding.fHistoryRvGames.adapter = adapter
            historyBinding.fHistoryShimmerLayout.visibility = View.GONE
            historyBinding.fHistoryRvGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && historyViewModel.isPageLoading.value == false) {
                        offset++
                        historyViewModel.addMoreGame(offset) { games ->
                            (historyBinding.fHistoryRvGames.adapter as GamesBigRecyclerViewAdapter).addItems(games)
                            Log.i(TAG, games.toString())
                        }
                        Log.i(TAG, "onScrollStateChanged")

                    }
                }
            })

            if (it.isEmpty()) {
                historyBinding.fHistoryIvDelete.visibility = View.GONE
                historyBinding.fHistoryLlEmpty.visibility = View.VISIBLE
            } else {
                historyBinding.fHistoryIvDelete.visibility = View.VISIBLE
                historyBinding.fHistoryLlEmpty.visibility = View.GONE
            }
        }
    }
}