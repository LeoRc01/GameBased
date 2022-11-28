package com.cip.cipstudio.view.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel

class GameListFragment : Fragment() {

    private lateinit var gameListViewModel: GameListViewModel
    private lateinit var gameListBinding: FragmentGameListBinding

    private lateinit var gameType: GameTypeEnum

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameType = GameTypeEnum.valueOf(arguments?.getString("gameType")!!)
        gameListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_list, container, false)
        gameListViewModel = GameListViewModel()
        gameListBinding.vm = gameListViewModel
        gameListBinding.title = getString(gameType.getName())
        initializeGames()
        return gameListBinding.root
    }

    private fun initializeGames() {
        gameListViewModel.initialize(
            gameType){
            val gvAdapter = FavouriteGridViewAdapter(requireContext(),
                it,
                gameListBinding.root.findNavController(),
                ActionGameDetailsEnum.GAME_LIST)
            gameListBinding.fGameListGvGames.adapter = gvAdapter
        }
    }


}