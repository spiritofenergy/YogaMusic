package com.kodex.yogamusic.sign_in

import android.service.autofill.UserData

class SignInResult (
    val data: UserData?,
    val errorMessage : String?
){
    data class UserData(
        val userId: String?,
        val userName: String?,
        val profilePictureUrl: String?
    )
}
