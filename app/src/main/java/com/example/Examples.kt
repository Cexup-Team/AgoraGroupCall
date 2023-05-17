package com.example

import com.example.agoratesting.Example
import java.util.Collections

object Examples {
    const val BASIC = "BASIC"
    const val ADVANCED = "ADVANCED"

    val ITEM_MAP: MutableMap<String, MutableList<Example>> = HashMap()

    fun addItem(item: Example) {
        val group = item.group
        val list = ITEM_MAP.getOrPut(group) { mutableListOf() }
        list.add(item)
    }

    fun sortItem() {
        for ((_, exampleList) in ITEM_MAP) {
            exampleList.sortWith { o1, o2 -> o1.index - o2.index }
        }
    }
}
