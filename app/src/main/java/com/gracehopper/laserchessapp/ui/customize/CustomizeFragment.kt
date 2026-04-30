package com.gracehopper.laserchessapp.ui.customize

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.ItemRepository
import com.gracehopper.laserchessapp.databinding.FragmentCustomizeBinding
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class CustomizeFragment : Fragment() {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    private val itemRepository by lazy {
        ItemRepository(NetworkUtils.getApiService())
    }

    private var piecesIndex = 0
    private var boardIndex = 0
    private var animationIndex = 0
    private var avatarIndex = 0

    private val piecesItems = mutableListOf<ShopItem>()
    private val boardItems = mutableListOf<ShopItem>()
    private val animationsItems = mutableListOf<ShopItem>()
    private val avatarItems = mutableListOf<ShopItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        loadInventoryItems()
    }

    private fun loadInventoryItems() {

        itemRepository.getInventory(
            onSuccess = { items ->

                piecesItems.clear()
                boardItems.clear()
                animationsItems.clear()
                avatarItems.clear()

                piecesItems.addAll(items.filter { it.itemType == ItemType.PIECE_SKIN })
                boardItems.addAll(items.filter { it.itemType == ItemType.BOARD_SKIN })
                animationsItems.addAll(items.filter { it.itemType == ItemType.WIN_ANIMATION })
                avatarItems.addAll(items.filter { it.itemType == ItemType.AVATAR })

                piecesIndex = 0
                boardIndex = 0
                animationIndex = 0
                avatarIndex = 0

                renderAll()

            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun setupListeners() {
        binding.btnPreviousPieces.setOnClickListener {
            piecesIndex = previousIndex(piecesIndex, piecesItems.size)
            renderPieces()
            savePiecesSelection()
        }

        binding.btnNextPieces.setOnClickListener {
            piecesIndex = nextIndex(piecesIndex, piecesItems.size)
            renderPieces()
            savePiecesSelection()
        }

        binding.btnPreviousBoard.setOnClickListener {
            boardIndex = previousIndex(boardIndex, boardItems.size)
            renderBoard()
            saveBoardSelection()
        }

        binding.btnNextBoard.setOnClickListener {
            boardIndex = nextIndex(boardIndex, boardItems.size)
            renderBoard()
            saveBoardSelection()
        }

        binding.btnPreviousAnimation.setOnClickListener {
            animationIndex = previousIndex(animationIndex, animationsItems.size)
            renderAnimation()
            saveAnimationSelection()
        }

        binding.btnNextAnimation.setOnClickListener {
            animationIndex = nextIndex(animationIndex, animationsItems.size)
            renderAnimation()
            saveAnimationSelection()
        }

        binding.btnPreviousAvatar.setOnClickListener {
            Toast.makeText(requireContext(), "Avatar pendiente de backend", Toast.LENGTH_SHORT).show()
        }

        binding.btnNextAvatar.setOnClickListener {
            Toast.makeText(requireContext(), "Avatar pendiente de backend", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderAll() {
        renderPieces()
        renderBoard()
        renderAnimation()
        renderAvatar()
    }

    private fun renderPieces() {
        if (piecesItems.isEmpty()) {
            binding.txtPiecesTitle.text = "Piezas · Sin items"
            return
        }

        val item = piecesItems[piecesIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getItemDrawable(item.itemId)

        binding.txtPiecesTitle.text = "Piezas · $name"

        binding.imgPiecePreview1.setImageResource(imageRes)
        binding.imgPiecePreview2.setImageResource(imageRes)
        binding.imgPiecePreview3.setImageResource(imageRes)
        binding.imgPiecePreview4.setImageResource(imageRes)
        binding.imgPiecePreview5.setImageResource(imageRes)
    }

    private fun renderBoard() {
        if (boardItems.isEmpty()) {
            binding.txtBoardSkinTitle.text = "Tablero · Sin items"
            return
        }

        val item = boardItems[boardIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getItemDrawable(item.itemId)

        binding.txtBoardSkinTitle.text = "Tablero · $name"
        binding.imgBoardPreview.setImageResource(imageRes)
    }

    private fun renderAnimation() {
        if (animationsItems.isEmpty()) {
            binding.txtVictorySkinTitle.text = "Victoria · Sin items"
            return
        }

        val item = animationsItems[animationIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getItemDrawable(item.itemId)

        binding.txtVictorySkinTitle.text = "Victoria · $name"
        binding.imgAnimationPreview.setImageResource(imageRes)
    }

    private fun renderAvatar() {
        binding.txtAvatarTitle.text = "Avatar · Pendiente"
    }

    private fun savePiecesSelection() {
        val item = piecesItems.getOrNull(piecesIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar piezas itemId=${item.itemId}")

        // TODO: llamar a repository para equipar piece_skin
    }

    private fun saveBoardSelection() {
        val item = boardItems.getOrNull(boardIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar tablero itemId=${item.itemId}")

        // TODO: llamar a repository para equipar board_skin
    }

    private fun saveAnimationSelection() {
        val item = animationsItems.getOrNull(animationIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar animación itemId=${item.itemId}")

        // TODO: llamar a repository para equipar win_animation
    }

    private fun nextIndex(current: Int, size: Int): Int {
        if (size == 0) return 0
        return (current + 1) % size
    }

    private fun previousIndex(current: Int, size: Int): Int {
        if (size == 0) return 0
        return if (current - 1 < 0) size - 1 else current - 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}