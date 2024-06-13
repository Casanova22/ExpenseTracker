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

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                _fireStoreResult.value = FireStoreResult.Success
                _state.value = MainState.HideLoader
                _state.value = MainState.Success
                Log.e(TAG,"Success")
            }
            .addOnFailureListener { e ->
                _fireStoreResult.value = FireStoreResult.Failure(e.message ?: "Unknown error")
                _state.value = MainState.HideLoader
                _state.value = MainState.ShowError(e.message ?: "Unknown error")
                Log.e(TAG,"Fail")
            }
    }

    fun login(username: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        _state.value = MainState.ShowLoader

        db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get().addOnSuccessListener { document ->
                if (document.isEmpty) {
                    _fireStoreResult.value = FireStoreResult.Failure("Invalid login credentials")
                    _state.value = MainState.ShowLoader
                } else {
                    _fireStoreResult.value = FireStoreResult.Success
                    _state.value = MainState.HideLoader
                    _state.value = MainState.Success
                }
            }
            .addOnFailureListener {e ->
                _fireStoreResult.value = FireStoreResult.Failure(e.message ?: " Unknown Error")
                _state.value = MainState.HideLoader
                _state.value = MainState.ShowError(e.message ?: "Unknown Error")
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
