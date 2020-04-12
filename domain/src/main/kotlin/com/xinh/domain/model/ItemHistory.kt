package com.xinh.domain.model

const val ITEM_LOAD_MORE_TYPE = 1000

data class ItemHistory(
    var id: String,
    var staticMap: String,
    var distance: String,
    var avgSpeed: String,
    var time: String,
    var itemType: Int = 0
)