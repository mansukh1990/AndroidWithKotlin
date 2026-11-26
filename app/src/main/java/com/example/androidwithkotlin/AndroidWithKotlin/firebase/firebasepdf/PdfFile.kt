package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebasepdf

data class PdfFile(
    val fileName: String,
    val downloadUrl: String
) {
    constructor() : this("", "")
}
