package com.archer.github.app.logic.model

data class DropDownMenuList(
    val options: List<String>
)

val SINCE_DROP_DOWN = DropDownMenuList(
    options = listOf(
        "daily",
        "weekly",
        "monthly"
    )
)

val LANGUAGE_DROP_DOWN = DropDownMenuList(
     options = listOf(
        "All",
        "Java",
        "Kotlin",
        "Dart",
        "Swift",
        "Javascript"
    )
)
