package br.com.beblue.snitch.core

fun String.snakeCase(): String {
    var text: String = ""
    var isFirst = true
    this.forEach {
        if (it.isUpperCase()) {
            if (isFirst) isFirst = false
            else text += "_"
            text += it.toLowerCase()
        } else {
            text += it
        }
    }
    return text
}