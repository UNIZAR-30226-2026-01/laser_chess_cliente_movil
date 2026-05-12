package com.gracehopper.laserchessapp.ui.customize

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.data.manager.CurrentUserManager
import com.gracehopper.laserchessapp.data.model.game.BoardLayouts
import com.gracehopper.laserchessapp.data.model.shop.ItemType
import com.gracehopper.laserchessapp.data.model.shop.ShopItem
import com.gracehopper.laserchessapp.data.model.user.UpdateAccountRequest
import com.gracehopper.laserchessapp.data.remote.NetworkUtils
import com.gracehopper.laserchessapp.data.repository.ItemRepository
import com.gracehopper.laserchessapp.data.repository.UserRepository
import com.gracehopper.laserchessapp.databinding.FragmentCustomizeBinding
import com.gracehopper.laserchessapp.gameLogic.board.Board
import com.gracehopper.laserchessapp.gameLogic.board.BoardParser
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class CustomizeFragment : Fragment() {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    private val itemRepository by lazy {
        ItemRepository(NetworkUtils.getApiService())
    }

    private val userRepository by lazy {
        UserRepository(NetworkUtils.getApiService())
    }

    private var piecesIndex = 0
    private var boardIndex = 0
    private var animationIndex = 0
    private var avatarIndex = 0

    private val piecesItems = mutableListOf<ShopItem>()
    private val boardItems = mutableListOf<ShopItem>()
    private val animationsItems = mutableListOf<ShopItem>()
    private val avatarItems = mutableListOf<ShopItem>()

    private var boardComposeView: ComposeView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupBoardPreview(view)
    }

    override fun onResume() {
        super.onResume()
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

                val account = CurrentUserManager.getMyCurrentProfile()

                if (account != null) {
                    piecesIndex = findEquippedIndex(piecesItems, account.pieceSkin)
                    boardIndex = findEquippedIndex(boardItems, account.boardSkin)
                    animationIndex = findEquippedIndex(animationsItems, account.winAnimation)
                    avatarIndex = findEquippedIndex(avatarItems, account.avatar)
                } else {
                    piecesIndex = 0
                    boardIndex = 0
                    animationIndex = 0
                    avatarIndex = 0
                }

                renderAll()

            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun findEquippedIndex(items: List<ShopItem>, equippedItemId: Int): Int {
        val index = items.indexOfFirst { it.itemId == equippedItemId }
        return if (index >= 0) index else 0
    }

    private fun setupListeners() {
        binding.btnPreviousPieces.setOnClickListener {
            piecesIndex = previousIndex(piecesIndex, piecesItems.size)
            renderPieces()
            renderBoardPreview()
            savePiecesSelection()
        }

        binding.btnNextPieces.setOnClickListener {
            piecesIndex = nextIndex(piecesIndex, piecesItems.size)
            renderPieces()
            renderBoardPreview()
            savePiecesSelection()
        }

        binding.btnPreviousBoard.setOnClickListener {
            boardIndex = previousIndex(boardIndex, boardItems.size)
            renderBoard()
            renderBoardPreview()
            saveBoardSelection()
        }

        binding.btnNextBoard.setOnClickListener {
            boardIndex = nextIndex(boardIndex, boardItems.size)
            renderBoard()
            renderBoardPreview()
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
            avatarIndex = previousIndex(avatarIndex, avatarItems.size)
            renderAvatar()
            saveAvatarSelection()
        }

        binding.btnNextAvatar.setOnClickListener {
            avatarIndex = nextIndex(avatarIndex, avatarItems.size)
            renderAvatar()
            saveAvatarSelection()
        }
    }

    private fun renderAll() {
        renderPieces()
        renderBoard()
        renderAnimation()
        renderAvatar()
        renderBoardPreview()
    }

    @SuppressLint("SetTextI18n")
    private fun renderPieces() {
        if (piecesItems.isEmpty()) {
            binding.txtPiecesTitle.text = "Piezas · Sin items"
            return
        }

        val item = piecesItems[piecesIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getPiecesPackDrawables(item.itemId)

        binding.txtPiecesTitle.text = "Piezas · $name"

        binding.imgPiecePreview1.setImageResource(imageRes[0])
        binding.imgPiecePreview2.setImageResource(imageRes[1])
        binding.imgPiecePreview3.setImageResource(imageRes[2])
        binding.imgPiecePreview4.setImageResource(imageRes[3])
        binding.imgPiecePreview5.setImageResource(imageRes[4])
    }

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private fun renderAnimation() {
        if (animationsItems.isEmpty()) {
            binding.txtAnimationTitle.text = "Animación · Sin items"
            return
        }

        val item = animationsItems[animationIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getItemDrawable(item.itemId)

        binding.txtAnimationTitle.text = "Animación · $name"
        binding.imgAnimationPreview.setImageResource(imageRes)
    }

    @SuppressLint("SetTextI18n")
    private fun renderAvatar() {
        if (avatarItems.isEmpty()) {
            binding.txtAvatarTitle.text = "Avatar · Sin items"
            return
        }

        val item = avatarItems[avatarIndex]
        val name = ItemUtils.getItemName(item.itemId)
        val imageRes = ItemUtils.getItemDrawable(item.itemId)

        binding.txtAvatarTitle.text = "Avatar · $name"
        binding.imgAvatarPreview.setImageResource(imageRes)
    }

    private fun savePiecesSelection() {
        val item = piecesItems.getOrNull(piecesIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar piezas itemId=${item.itemId}")

        val dto = UpdateAccountRequest(pieceSkin = item.itemId)
        equipItem(dto)
    }

    private fun saveBoardSelection() {
        val item = boardItems.getOrNull(boardIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar tablero itemId=${item.itemId}")

        val dto = UpdateAccountRequest(boardSkin = item.itemId)
        equipItem(dto)
    }

    private fun saveAnimationSelection() {
        val item = animationsItems.getOrNull(animationIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar animación itemId=${item.itemId}")

        val dto = UpdateAccountRequest(winAnimation = item.itemId)
        equipItem(dto)
    }

    private fun saveAvatarSelection() {
        val item = avatarItems.getOrNull(avatarIndex) ?: return
        Log.d("CUSTOMIZE", "Equipar avatar itemId=${item.itemId}")

        val dto = UpdateAccountRequest(avatar = item.itemId)
        equipItem(dto)
    }

    private fun equipItem(dto: UpdateAccountRequest) {

        userRepository.updateMyProfile(
            dto,
            onSuccess = { profile ->
                CurrentUserManager.setMyProfile(profile)
            },
            onError = {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error equipando item", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

    }

    private fun nextIndex(current: Int, size: Int): Int {
        if (size == 0) return 0
        return (current + 1) % size
    }

    private fun previousIndex(current: Int, size: Int): Int {
        if (size == 0) return 0
        return if (current - 1 < 0) size - 1 else current - 1
    }

    /**
     * Configura el ComposeView para mostrar el tablero seleccionado.
     */
    private fun setupBoardPreview(view: View) {
        val boardContainer = view.findViewById<ViewGroup>(R.id.boardContainerCustomize)

        view.findViewById<ImageView?>(R.id.imgBoardPlaceholderCustomize)?.visibility = View.GONE
        val composeView = ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        boardComposeView = composeView
        boardContainer.addView(composeView)
        renderBoardPreview()
    }

    /**
     * Parsea el CSV del tablero y lo muestra en el ComposeView.
     */
    private fun renderBoardPreview() {
        val pieceSkin = piecesItems.getOrNull(piecesIndex)?.itemId
            ?: CurrentUserManager.getMyCurrentPieceSkin()
        val boardSkin = boardItems.getOrNull(boardIndex)?.itemId
            ?: CurrentUserManager.getMyCurrentBoardSkin()

        boardComposeView?.setContent {
            val board = Board(rows = 10, cols = 8)
            BoardParser.boardFromCSV(board, BoardLayouts.getCsvForBoard("ACE"))
            CustomizeBoardPreview(
                board = board,
                pieceSkin = pieceSkin,
                boardSkin = boardSkin)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}