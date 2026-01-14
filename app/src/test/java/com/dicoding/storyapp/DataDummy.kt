package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl $i",
                "createdAt $i",
                "name $i",
                "description $i",
                null,
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}
