package com.gracehopper.laserchessapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gracehopper.laserchessapp.R
import com.gracehopper.laserchessapp.ui.game.GameActivity
import com.gracehopper.laserchessapp.ui.notifications.NotificationsDialogFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnPlay = view.findViewById<Button>(R.id.btnPlay)
        btnPlay.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            startActivity(intent)
        }
        
        setupSelectors(view)
        
        return view
    }

    private fun setupSelectors(view: View) {
        var includeBoardSelector = view.findViewById<View>(R.id.includeBoardSelector)
        val txtBoardTitle = includeBoardSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgBoardIcon = includeBoardSelector.findViewById<ImageView>(R.id.imgSelectorIcon)

        txtBoardTitle.text = "Tablero"
        imgBoardIcon.setImageResource(R.drawable.ic_tablero)
        imgBoardIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCRed))
        includeBoardSelector.setOnClickListener {

        }

        var includeTimeSelector = view.findViewById<View>(R.id.includeTimeSelector)
        val txtTimeTitle = includeTimeSelector.findViewById<TextView>(R.id.txtSelectorTitle)
        val imgTimeIcon = includeTimeSelector.findViewById<ImageView>(R.id.imgSelectorIcon)
        txtTimeTitle.text = "Modo de tiempo"
        imgTimeIcon.setImageResource(R.drawable.ic_tiempo)
        imgTimeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.LCBlue))
        includeBoardSelector.setOnClickListener {

        }
    }

    private fun showBottomSheet(titulo: String) {
        // 1. Creamos la instancia del BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        // 2. Inflamos nuestro diseño XML personalizado
        val dialogView = layoutInflater.inflate(R.layout.dialog_selector_desplegable, null)

        // 3. Modificamos el título según qué botón hayamos pulsado
        val txtTitle = dialogView.findViewById<TextView>(R.id.txtDialogTitle)
        txtTitle.text = titulo

        // 4. (Opcional) Configurar las acciones de las opciones internas
        val btnOption1 = dialogView.findViewById<Button>(R.id.btnOption1)
        btnOption1.setOnClickListener {
            // Aquí guardarías la opción seleccionada y cerrarías el diálogo
            bottomSheetDialog.dismiss()
        }

        // 5. Asignamos la vista al diálogo y lo mostramos en pantalla
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }

}