package com.gracehopper.laserchessapp.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
import com.gracehopper.laserchessapp.databinding.FragmentShopBinding

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private lateinit var piecesAdapter: ShopProductAdapter
    private lateinit var boardsAdapter: ShopProductAdapter
    private lateinit var animationsAdapter: ShopProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapters()
        loadFakeData()
    }

    private fun setupAdapters() {
        piecesAdapter = ShopProductAdapter(emptyList()) { product ->
            Toast.makeText(requireContext(), "Comprar ${product.name}", Toast.LENGTH_SHORT).show()
        }

        boardsAdapter = ShopProductAdapter(emptyList()) { product ->
            Toast.makeText(requireContext(), "Comprar ${product.name}", Toast.LENGTH_SHORT).show()
        }

        animationsAdapter = ShopProductAdapter(emptyList()) { product ->
            Toast.makeText(requireContext(), "Comprar ${product.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerPiecesShop.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = piecesAdapter
        }

        binding.recyclerBoardsShop.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = boardsAdapter
        }

        binding.recyclerAnimationsShop.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = animationsAdapter
        }
    }

    private fun loadFakeData() {
        piecesAdapter.updateData(
            listOf(
                ShopProduct(1, "Piezas 1", ItemType.PIECE_SKIN, 75),
                ShopProduct(2, "Piezas 2", ItemType.PIECE_SKIN, 123),
                ShopProduct(3, "Piezas 3", ItemType.PIECE_SKIN, 240)
            )
        )

        boardsAdapter.updateData(
            listOf(
                ShopProduct(1, "Tablero 1", ItemType.BOARD_SKIN, 75),
                ShopProduct(2, "Tablero 2", ItemType.BOARD_SKIN, 123),
                ShopProduct(3, "Tablero 3", ItemType.BOARD_SKIN, 240)
            )
        )

        animationsAdapter.updateData(
            listOf(
                ShopProduct(1, "Animación 1", ItemType.WIN_ANIMATION, 75),
                ShopProduct(2, "Animación 2", ItemType.WIN_ANIMATION, 123),
                ShopProduct(3, "Animación 3", ItemType.WIN_ANIMATION, 240)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}