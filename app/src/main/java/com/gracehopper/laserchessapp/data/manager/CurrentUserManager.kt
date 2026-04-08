package com.gracehopper.laserchessapp.data.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gracehopper.laserchessapp.data.model.user.MyProfile

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
     * Indica si el perfil actual del usuario ha sido cargado.
     *
     * @return true si el perfil ha sido cargado, false en caso contrario
     */
    fun isProfileLoaded(): Boolean {
        return _myProfile.value != null
    }

}