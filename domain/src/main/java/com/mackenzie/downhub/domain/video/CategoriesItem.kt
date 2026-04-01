package com.mackenzie.downhub.domain.video


data class CategoriesItem(
    val categories: List<CategoryItem>,
    val count: Int
)

data class CategoryItem(
    val category: String
)
