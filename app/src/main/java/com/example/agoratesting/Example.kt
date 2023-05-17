package com.example.agoratesting

/**
 * @author
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Example(
    /**
     * @return example index
     */
    val index: Int,
    /**
     * @return group name
     */
    val group: String = "",
    /**
     * @return example name
     */
    val name: Int,
    /**
     * @return action ID
     */
    val actionId: Int,
    /**
     * @return tips ID
     */
    val tipsId: Int
)
