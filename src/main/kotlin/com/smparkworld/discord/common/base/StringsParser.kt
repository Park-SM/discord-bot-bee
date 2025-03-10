package com.smparkworld.discord.common.base

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

object StringsParser {

    private var code = LanguageCode.KOREAN
    private val stringsMap = mutableMapOf<String, String>()

    init {
        initialize()
    }

    fun changeLanguage(code: LanguageCode) {
        StringsParser.code = code
        initialize()
    }

    private fun initialize() {
        stringsMap.clear()

        val file = this::class.java.classLoader.getResourceAsStream("strings${code.postfix}.xml")
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.parse(file)

        val elements = doc.getElementsByTagName("string")
        for (i in 0 until elements.length) {
            val node = elements.item(i)
            val key = node.attributes.getNamedItem("name").nodeValue
            val value = node.textContent.replace("\\n", "\n")
            stringsMap[key] = value
        }
    }

    fun getString(code: StringCode): String =
        stringsMap[code.key] ?: throw IllegalArgumentException("Not found res string | key : ${code.key}")

    fun getString(code: StringCode, vararg args: Any) =
        getString(code).format(args.map(Any::toString))

    enum class LanguageCode(
        val postfix: String
    ) {
        KOREAN("-kor")
    }
}