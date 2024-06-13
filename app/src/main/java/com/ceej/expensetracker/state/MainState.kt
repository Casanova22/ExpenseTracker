package com.ceej.expensetracker.state

open class MainState {
    object ShowLoader : MainState()
    object HideLoader : MainState()
    data class ShowError(val message: String) : MainState()
    object Success : MainState()

}