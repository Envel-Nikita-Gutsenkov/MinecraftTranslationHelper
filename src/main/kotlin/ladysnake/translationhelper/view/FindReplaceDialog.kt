package ladysnake.translationhelper.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import ladysnake.translationhelper.model.data.Language
import tornadofx.*


class FindReplaceDialog(
    private val languages: ObservableList<String>,
    private val isReadOnly: String.() -> Boolean,
    fromLang: String,
    toLang: String,
    regex: String,
    replace: String
): Dialog<FindReplaceDialog.FindReplaceParameters>() {
    private lateinit var fromLang: ComboBox<String>
    private lateinit var toLang: ComboBox<String>
    private val regex: StringProperty = SimpleStringProperty("")
    private val replacement: StringProperty = SimpleStringProperty("")
    private val replaceExistingTranslations: BooleanProperty = SimpleBooleanProperty(true)
    private val useRegex: BooleanProperty = SimpleBooleanProperty(true)

    constructor(languages: List<String>, isReadOnly: String.() -> Boolean) : this(
        FXCollections.observableList(languages),
        isReadOnly,
        if (languages.isEmpty()) "" else languages[0],
        if (languages.size > 1) languages[1] else if (languages.isEmpty()) "" else languages[0],
        "", ""
    )

    init {
        this.dialogPane.content = gridpane {
            hgap = 10.0
            this@FindReplaceDialog.fromLang = combobox(values = languages) {
                selectionModel.select(fromLang)
                gridpaneConstraints {
                    columnIndex = 1
                    rowIndex = 1
                }
            }
            this@FindReplaceDialog.toLang = combobox(values = languages.filter { !it.isReadOnly() }) {
                selectionModel.select(toLang)
                gridpaneConstraints {
                    columnIndex = 3
                    rowIndex = 1
                }
            }
            textfield {
                gridpaneConstraints {
                    columnIndex = 1
                    rowIndex = 2
                    columnSpan = 2
                }
                this@FindReplaceDialog.regex.bindBidirectional(textProperty())
                text = regex
            }
            textfield {
                gridpaneConstraints {
                    columnIndex = 3
                    rowIndex = 2
                    columnSpan = 2
                }
                text = replace
                this@FindReplaceDialog.replacement.bind(textProperty())
                this@FindReplaceDialog.setOnShown {
                    requestFocus()
                }
            }
            checkbox("Replace existing translations") {
                gridpaneConstraints {
                    columnIndex = 1
                    rowIndex = 3
                }
                replaceExistingTranslations.bind(selectedProperty())
                isSelected = true
            }
            checkbox("Allow wildcards / raw regex") {
                gridpaneConstraints {
                    columnIndex = 1
                    rowIndex = 4
                }
                useRegex.bind(selectedProperty())
                isSelected = true
            }
        }
        this.title = "Search and replace"
        this.headerText = "Search for a string from a language and convert it to something else"
        this.dialogPane.buttonTypes.add(ButtonType.CANCEL)
        this.dialogPane.buttonTypes.add(ButtonType.APPLY)
        this.setResultConverter { type -> if (type == ButtonType.APPLY) FindReplaceParameters(
            Language(this.fromLang.value),
            Language(this.toLang.value),
            this.replacement.get(),
            this.regex.get(),
            this.replaceExistingTranslations.get(),
            this.useRegex.get()
        ) else null }
    }

    fun setFromLang(fromLang: String) {
        if (fromLang in languages) {
            this.fromLang.selectionModel.select(fromLang)
        }
    }

    fun setToLang(toLang: String) {
        if (toLang in languages && !toLang.isReadOnly()) {
            this.toLang.selectionModel.select(toLang)
        }
    }

    fun setRegex(regex: String?) {
        this.regex.set(regex ?: "")
    }



    data class FindReplaceParameters(
        val fromLang: Language,
        val toLang: Language,
        val replacement: String,
        val inputString: String,
        val replaceExistingTranslations: Boolean,
        val useRegex: Boolean
    )
}