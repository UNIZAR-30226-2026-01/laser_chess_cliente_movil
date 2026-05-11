package com.gracehopper.laserchessapp.ui.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.gracehopper.laserchessapp.R

object BackgroundUtils {

    /**
     * Configura el fondo de la pantalla basándose en el skin del tablero equipado.
     * @param imageView El ImageView que mostrará el fondo.
     * @param boardSkinId El ID del skin del tablero.
     */
    fun setupBackground(imageView: ImageView, boardSkinId: Int) {
        val backgroundRes = when (boardSkinId) {
            4 -> R.drawable.bg_classic
            5 -> R.drawable.bg_soretro
            6 -> R.drawable.bg_cats
            else -> R.drawable.bg_classic
        }

        imageView.setImageResource(backgroundRes)
        
        imageView.alpha = 0.7f

        // Limpiar animaciones previas para evitar acumulaciones
        imageView.clearAnimation()
        
        // Reset de transformaciones
        imageView.translationX = 0f
        imageView.translationY = 0f
        imageView.rotation = 0f
        
        // Mantenemos la escala alejada
        val baseScale = if (boardSkinId == 5 || boardSkinId == 6) 0.9f else 1.0f
        imageView.scaleX = baseScale
        imageView.scaleY = baseScale

        if (boardSkinId == 5 || boardSkinId == 6) {
            // Restaurada la velocidad y rangos originales
            val animHorizontal = ObjectAnimator.ofFloat(imageView, "translationX", -170f, 150f).apply {
                duration = 30000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }
            val animVertical = ObjectAnimator.ofFloat(imageView, "translationY", -70f, 90f).apply {
                duration = 20000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }
            val animRotation = ObjectAnimator.ofFloat(imageView, "rotation", -3f, 3f).apply {
                duration = 25000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
            }

            animHorizontal.start()
            animVertical.start()
            animRotation.start()
        }
    }
}
