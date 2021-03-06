package ladysnake.translationhelper.model

import ladysnake.translationhelper.model.data.Language
import ladysnake.translationhelper.model.data.LanguageMap
import ladysnake.translationhelper.model.serialization.AbstractLanguageMapAdapter
import ladysnake.translationhelper.model.serialization.LanguageMapAdapter
import java.io.File
import java.nio.file.Files

object TranslationLoader {
    private val adapters: MutableList<LanguageMapAdapter> = mutableListOf()
    val knownExtensions: List<String> @Synchronized get() = adapters.filterIsInstance<AbstractLanguageMapAdapter>().map { it.fileExtension }

    @Synchronized
    fun registerAdapter(adapter: LanguageMapAdapter) {
        adapters += adapter
    }

    fun supports(file: File): Boolean {
        if (!file.isFile) {
            return false
        }
        for (adapter in adapters) {
            if (adapter.accepts(file.extension)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun load(langFile: File): LanguageMap? {
        val locale = langFile.nameWithoutExtension
        val extension = langFile.extension
        for (adapter in adapters) {
            if (adapter.accepts(extension)) {
                val languageMap = LanguageMap(Language(locale))
                adapter.deserialize(Files.lines(langFile.toPath(), Charsets.UTF_8), languageMap)
                return languageMap
            }
        }
        return null
    }

    @Synchronized
    fun save(languageMap: LanguageMap, langFile: File) {
        val extension = langFile.extension
        for (adapter in adapters) {
            if (adapter.accepts(extension)) {
                langFile.bufferedWriter().use {writer ->
                    writer.write(adapter.serialize(languageMap))
                }
            }
        }
    }
}