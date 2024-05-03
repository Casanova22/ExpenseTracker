package com.ceej.expensetracker.viewmodel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {

    private val _db = FirebaseFirestore.getInstance()

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

                handleFireStoreResult(FireStoreResult.Success(documentId))
            } catch (e:Exception) {

            }
        }
    }

    private fun handleFireStoreResult(result: FireStoreResult) {
        when(result) {
            is FireStoreResult.Success -> {
                Log.w(VIEWMODEL, "Success")
            }
            is FireStoreResult.Failure -> {
                Log.e(VIEWMODEL, "Failed")
            }
        }
    }

    companion object {
        private const val VIEWMODEL = "ViewModel"
    }

}