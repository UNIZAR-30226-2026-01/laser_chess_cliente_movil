package com.gracehopper.laserchessapp.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.ItemRepository
import com.gracehopper.laserchessapp.databinding.FragmentShopBinding

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private val itemRepository by lazy {
        ItemRepository(NetworkUtils.getApiService())
    }

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
        loadShopItems()
    }

    private fun setupAdapters() {
        piecesAdapter = ShopProductAdapter(emptyList()) { product ->
            buyProduct(product)
        }

        boardsAdapter = ShopProductAdapter(emptyList()) { product ->
            buyProduct(product)
        }

        animationsAdapter = ShopProductAdapter(emptyList()) { product ->
            buyProduct(product)
        }

        binding.recyclerPiecesShop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = piecesAdapter
        }

        binding.recyclerBoardsShop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = boardsAdapter
        }

        binding.recyclerAnimationsShop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = animationsAdapter
        }
    }

    private fun loadShopItems() {

        itemRepository.getAllShopItems(
            onSuccess = { items ->
                Log.d("SHOP_FRAGMENT", "Items recibidos: ${items.size}")

                val products = items.map { item ->
                    item.toShopProduct()
                }

                val pieces = products.filter { it.itemType == ItemType.PIECE_SKIN }
                val boards = products.filter { it.itemType == ItemType.BOARD_SKIN }
                val animations = products.filter { it.itemType == ItemType.WIN_ANIMATION }

                Log.d(
                    "SHOP_FRAGMENT",
                    "Piezas=${pieces.size}, tableros=${boards.size}, animaciones=${animations.size}"
                )

                piecesAdapter.updateData(pieces)
                boardsAdapter.updateData(boards)
                animationsAdapter.updateData(animations)
            },
            onError = { error ->
                Log.e("SHOP_FRAGMENT", "Error cargando tienda: $error")
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun buyProduct(product: ShopProduct) {

        itemRepository.buyItem(itemId = product.itemId,
            onSuccess = {
                Toast.makeText(requireContext(),
                    "Has comprado ${product.name}",
                    Toast.LENGTH_SHORT).show()

                loadShopItems()
            },
            onError = { error ->
                Log.e("SHOP_FRAGMENT", "Error comprando item: $error")
                Toast.makeText(requireContext(),
                    error,
                    Toast.LENGTH_SHORT).show()
            }
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private fun ShopItem.toShopProduct(): ShopProduct {

    return ShopProduct(
        itemId = itemId,
        name = getShopItemName(itemId, itemType),
        itemType = itemType,
        price = price,
        levelRequisite = levelRequisite,
        isDefault = isDefault
    )

}

private fun getShopItemName(itemId: Int, itemType: ItemType): String {

    return when (itemType) {
        ItemType.PIECE_SKIN -> "Skin $itemId"
        ItemType.BOARD_SKIN -> "Tablero $itemId"
        ItemType.WIN_ANIMATION -> "Animación $itemId"
    }

}