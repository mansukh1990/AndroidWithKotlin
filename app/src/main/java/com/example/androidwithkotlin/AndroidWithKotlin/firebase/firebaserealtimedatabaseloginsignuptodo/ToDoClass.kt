package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ToDoClass(
    var dataTitle: String? = null,
    var dataDesc: String? = null,
    var dataPriority: String? = null,
    var dataImage: String? = null,
    var key: String? = null
)