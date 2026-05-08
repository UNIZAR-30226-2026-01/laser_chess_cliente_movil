package com.gracehopper.laserchessapp.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gracehopper.laserchessapp.data.model.user.MyProfile
import com.gracehopper.laserchessapp.data.model.user.UserRatings

/**
 * Objeto singleton que almacena el perfil actual del usuario.
 */
object CurrentUserManager {

    private val _myProfile = MutableLiveData<MyProfile?>()
    val myProfile: LiveData<MyProfile?> = _myProfile

    private val _sessionExpired = MutableLiveData(false)
    val sessionExpired: LiveData<Boolean> = _sessionExpired

    /**
     * Establece el perfil actual del usuario.
     *
     * @param profile Perfil actual del usuario.
     */
    fun setMyProfile(profile: MyProfile) {
        _myProfile.postValue(profile)
        _sessionExpired.postValue(false)
    }

    /**
     * Borra el perfil actual del usuario.
     */
    fun clearMyProfile() {
        _myProfile.postValue(null)
    }

    /**
     * Expira la sesión del usuario.
     */
    fun expireSession() {
        _myProfile.postValue(null)
        _sessionExpired.postValue(true)
    }

    /**
     * Resetea la flag de expiración de sesión.
     */
    fun resetSessionExpiredFlag() {
        _sessionExpired.postValue(false)
    }

    /**
     * Obtiene el perfil actual del usuario.
     *
     * @return Perfil actual del usuario, o null si no se ha establecido
     */
    fun getMyCurrentProfile(): MyProfile? {
        return _myProfile.value
    }

    /**
     * Obtiene el ID del usuario actual.
     *
     * @return ID del usuario actual, o null si no se ha establecido
     */
    fun getMyCurrentId(): Long? {
        return _myProfile.value?.id
    }

    /**
     * Obtiene el mail del usuario actual.
     *
     * @return Mail del usuario actual, o null si no se ha establecido
     */
    fun getMyCurrentMail(): String? {
        return _myProfile.value?.mail
    }

    /**
     * Obtiene el level del usuario actual.
     *
     * @return Level del usuario actual, o null si no se ha establecido
     */
    fun getMyCurrentLevel(): Int? {
        return _myProfile.value?.level
    }

    /**
     * Obtiene la xp por nivel del usuario actual.
     *
     * @return Xp por nivel del usuario actual, o null si no se ha establecido
     */
    fun getMyCurrentXpLevel(): Int? {
        return _myProfile.value?.xpLevel
    }

    /**
     * Obtiene la xp requerida para completar el nivel del usuario actual.
     *
     * @return Xp requerida para completar el nivel del usuario actual, o null si no se ha establecido.
     */
    fun getMyCurrentMaxXpLevel(): Int? {
        return _myProfile.value?.xpRequired
    }

    /**
     * Obtiene las skin de piezas del usuario actual.
     *
     * @return Skin de piezas del usuario actual, o default(1) si no se ha establecido.
     */
    fun getMyCurrentPieceSkin(): Int {
        return _myProfile.value?.pieceSkin ?: 1
    }

    /**
     * Obtiene las skin de tablero del usuario actual.
     *
     * @return Skin de tablero del usuario actual, o default(4) si no se ha establecido.
     */
    fun getMyCurrentBoardSkin(): Int {
        return _myProfile.value?.boardSkin ?: 4
    }

    fun getMyCurrentWinAnimation(): Int {
        return _myProfile.value?.winAnimation ?: 7
    }

    /**
     * Indica si el perfil actual del usuario ha sido cargado.
     *
     * @return true si el perfil ha sido cargado, false en caso contrario
     */
    fun isProfileLoaded(): Boolean {
        return _myProfile.value != null
    }

}