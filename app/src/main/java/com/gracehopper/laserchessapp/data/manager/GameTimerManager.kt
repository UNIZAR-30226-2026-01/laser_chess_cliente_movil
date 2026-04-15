package com.gracehopper.laserchessapp.data.manager

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gracehopper.laserchessapp.data.model.game.PlayerTimer

/**
 * Manager encargado de gestionar los temporizadores de la partida.
 */
object GameTimerManager {

    private val _myTimer = MutableLiveData<PlayerTimer>()
    val myTimer: LiveData<PlayerTimer> = _myTimer

    private val _opponentTimer = MutableLiveData<PlayerTimer>()
    val opponentTimer: LiveData<PlayerTimer> = _opponentTimer

    private val handler = Handler(Looper.getMainLooper())
    private val tickRate = 100L //ms

    private val timerRunnable = object : Runnable {
        override fun run() {

            _myTimer.value?.let {
                if (it.isRunning) {
                    _myTimer.value = it.copy(
                        timeLeftMillis = it.timeLeftMillis - tickRate
                    )
                }
            }

            _opponentTimer.value?.let {
                if (it.isRunning) {
                    _opponentTimer.value = it.copy(
                        timeLeftMillis = it.timeLeftMillis - tickRate
                    )
                }
            }

            handler.postDelayed(this, tickRate)
        }
    }

    /**
     * Inicializa ambos timers
     */
    fun initTimers(startingTimeSeconds: Int) {
        val millis = startingTimeSeconds * 1000L

        _myTimer.value = PlayerTimer(millis, false)
        _opponentTimer.value = PlayerTimer(millis, false)
    }

    /**
     * Inicia el loop del timer
     */
    fun start() {
        handler.post(timerRunnable)
    }

    /**
     * Detiene completamente el timer
     */
    fun stop() {
        handler.removeCallbacks(timerRunnable)
    }

    /**
     * Establece de quién es el turno
     */
    fun setMyTurn(isMyTurn: Boolean) {
        _myTimer.value?.let {
            _myTimer.value = it.copy(isRunning = isMyTurn)
        }

        _opponentTimer.value?.let {
            _opponentTimer.value = it.copy(isRunning = !isMyTurn)
        }
    }

    /**
     * Sincroniza con tiempos del backend
     */
    fun syncTimers(myTime: Long, opponentTime: Long) {
        _myTimer.value = _myTimer.value?.copy(timeLeftMillis = myTime)
        _opponentTimer.value = _opponentTimer.value?.copy(timeLeftMillis = opponentTime)
    }

    /**
     * Añade incremento tras jugada
     */
    fun addIncrementToMe(incrementSeconds: Int) {
        _myTimer.value?.let {
            _myTimer.value = it.copy(
                timeLeftMillis = it.timeLeftMillis + incrementSeconds * 1000L
            )
        }
    }

    fun addIncrementToOpponent(incrementSeconds: Int) {
        _opponentTimer.value?.let {
            _opponentTimer.value = it.copy(
                timeLeftMillis = it.timeLeftMillis + incrementSeconds * 1000L
            )
        }
    }
}