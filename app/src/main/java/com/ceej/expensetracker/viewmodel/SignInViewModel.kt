package com.ceej.expensetracker.viewmodel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {

    private val _db = FirebaseFirestore.getInstance()
    private val _fireStoreResult = MutableLiveData<FireStoreResult>()
    val fireStoreResult: LiveData<FireStoreResult> get() = _fireStoreResult

    sealed class FireStoreResult {
        data class Success(val documentId: String) : FireStoreResult()
        data class Failure(val error: String) : FireStoreResult()
    }

    fun saveUserDataToFireStore(username: String, email: String, password: String) {
        val user = hashMapOf(
            "Username" to username,
            "Email" to email,
            "Password" to password
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val documentReference = _db.collection("info").add(user).await()
                val documentId = documentReference.id

                _fireStoreResult.postValue(FireStoreResult.Success(documentId))
            } catch (e: Exception) {
                _fireStoreResult.postValue(FireStoreResult.Failure(e.message ?: "Unknown error"))
            }
        }
    }

    companion object {
        private const val VIEWMODEL = "ViewModel"
    }
}
