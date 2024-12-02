package com.ceej.expensetracker.viewmodel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ceej.expensetracker.state.MainState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SignInViewModel : ViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state : LiveData<MainState> get() = _state

    private val _fireStoreResult = MutableLiveData<FireStoreResult>()
    val fireStoreResult: LiveData<FireStoreResult> get() = _fireStoreResult

    fun saveUserDataToFireStore(username: String, email: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )

        _state.value = MainState.ShowLoader

        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").add(user).await()
                _fireStoreResult.postValue(FireStoreResult.Success)
                _state.postValue(MainState.HideLoader)
                _state.postValue(MainState.Success)
                Log.e(TAG, "Success")
            } catch (e: Exception) {
                _fireStoreResult.postValue(FireStoreResult.Failure(e.message ?: "UnkownError"))
                _state.postValue(MainState.HideLoader)
                _state.postValue(MainState.ShowError(e.message ?: "Unknown Error"))
                Log.e(TAG, "Failed")
            }
        }
    }

    fun login(username: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        _state.value = MainState.ShowLoader

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = db.collection("users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get()
                    .await()

                if (document.isEmpty) {
                    _fireStoreResult.postValue(FireStoreResult.Failure("Invalid login credentials"))
                    _state.postValue(MainState.ShowLoader)
                } else {
                    _fireStoreResult.postValue(FireStoreResult.Success)
                    _state.postValue(MainState.HideLoader)
                    _state.postValue(MainState.Success)
                }
            } catch (e: Exception) {
                _fireStoreResult.postValue(FireStoreResult.Failure(e.message ?: "Unknown Error"))
                _state.postValue(MainState.HideLoader)
                _state.postValue(MainState.ShowError(e.message ?: "Unknown Error"))

            }
        }
    }

    sealed class FireStoreResult {
        object Success : FireStoreResult()
        data class Failure(val error: String) : FireStoreResult()
    }

    companion object {
        private const val TAG = "ViewModel"
    }
}
