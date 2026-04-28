package com.gracehopper.laserchessapp.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var avatarsAdapter: ShopProductAdapter

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
        setupArrowButtons()
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

        avatarsAdapter = ShopProductAdapter(emptyList()) { product ->
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

        binding.recyclerAvatarsShop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = avatarsAdapter
        }
    }

    private fun setupArrowButtons() {
        binding.btnPrevPieces.setOnClickListener {
            scrollRecycler(binding.recyclerPiecesShop, -1)
        }

        binding.btnNextPieces.setOnClickListener {
            scrollRecycler(binding.recyclerPiecesShop, 1)
        }

        binding.btnPrevBoards.setOnClickListener {
            scrollRecycler(binding.recyclerBoardsShop, -1)
        }

        binding.btnNextBoards.setOnClickListener {
            scrollRecycler(binding.recyclerBoardsShop, 1)
        }

        binding.btnPrevAnimations.setOnClickListener {
            scrollRecycler(binding.recyclerAnimationsShop, -1)
        }

        binding.btnNextAnimations.setOnClickListener {
            scrollRecycler(binding.recyclerAnimationsShop, 1)
        }

        binding.btnPrevAvatars.setOnClickListener {
            scrollRecycler(binding.recyclerAvatarsShop, -1)
        }

        binding.btnNextAvatars.setOnClickListener {
            scrollRecycler(binding.recyclerAvatarsShop, 1)
        }
    }

    private fun scrollRecycler(recyclerView: RecyclerView, direction: Int) {
        val distancePx = (recyclerView.width * 0.75f).toInt()

        recyclerView.smoothScrollBy(direction * distancePx, 0)
    }

    private fun loadShopItems() {

        itemRepository.getAllShopItems(
            onSuccess = { items ->
                Log.d("SHOP_FRAGMENT", "Items recibidos: ${items.size}")

                items.forEachIndexed { index, item ->
                    Log.d(
                        "SHOP_FRAGMENT",
                        "RAW [$index] id=${item.itemId}, type=${item.itemType}, price=${item.price}, level=${item.levelRequisite}, default=${item.isDefault}"
                    )
                }

                val products = items.map { item ->
                    item.toShopProduct()
                }

                products.forEachIndexed { index, product ->
                    Log.d(
                        "SHOP_FRAGMENT",
                        "PRODUCT [$index] id=${product.itemId}, name=${product.name}, type=${product.itemType}, price=${product.price}"
                    )
                }

                val pieces = products.filter { it.itemType == ItemType.PIECE_SKIN }
                val boards = products.filter { it.itemType == ItemType.BOARD_SKIN }
                val animations = products.filter { it.itemType == ItemType.WIN_ANIMATION }
                val avatars = products.filter { it.itemType == ItemType.AVATAR }

                Log.d(
                    "SHOP_FRAGMENT",
                    "Piezas=${pieces.size}, tableros=${boards.size}, animaciones=${animations.size}, avatares=${avatars.size}"
                )

                piecesAdapter.updateData(pieces)
                boardsAdapter.updateData(boards)
                animationsAdapter.updateData(animations)
                avatarsAdapter.updateData(avatars)
            },
            onError = { error ->
                Log.e("SHOP_FRAGMENT", "Error cargando tienda: $error")
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun buyProduct(product: ShopProduct) {

        itemRepository.buyItem(
            itemId = product.itemId,
            onSuccess = {
                Toast.makeText(
                    requireContext(),
                    "Has comprado ${product.name}",
                    Toast.LENGTH_SHORT
                ).show()

                loadShopItems()
            },
            onError = { error ->
                Log.e("SHOP_FRAGMENT", "Error comprando item: $error")
                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()
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
        ItemType.AVATAR -> "Avatar $itemId"
    }

}