package com.mycompany.sma.data.model

data class User (
                 val username : String,
                 val email : String,
                 val password : String,
                 val name : String,
                 val phoneNumber : String = "",
                 val userID : String = ""
)
