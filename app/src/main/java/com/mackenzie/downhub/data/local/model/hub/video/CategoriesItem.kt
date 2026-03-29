package com.mackenzie.downhub.data.local.model.hub.video


data class CategoriesItem(
    val categories: List<CategoryItem>,
    val count: Int
)

data class CategoryItem(
    val category: String
)
