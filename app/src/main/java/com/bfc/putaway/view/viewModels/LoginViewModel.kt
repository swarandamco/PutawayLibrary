package com.bfc.putaway.view.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bfc.putaway.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    fun makeLoginRequest(json: String) {

        viewModelScope.launch {
//            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            val jsonBody = json
            val result = try {
                loginRepository.makeLoginRequest(jsonBody)
            } catch(e: Exception) {
//                Result.Error(Exception("Network request failed"))
            }
            when (result) {
//                is Result.Success<LoginModelRes> -> {
                    // Happy path
//                }
//                else -> {
//                     Show error in UI
//                }
            }
        }
    }
}