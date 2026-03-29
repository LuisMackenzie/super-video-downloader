package com.mackenzie.downhub.data.local.model.hub.video


data class TagsResponseItem(
    val tags: List<TagDomainItem>,
    val count: Int
)


data class TagDomainItem(
   val tag: TagDomainInfo
)

data class TagDomainInfo(
    val tagName: String
)
