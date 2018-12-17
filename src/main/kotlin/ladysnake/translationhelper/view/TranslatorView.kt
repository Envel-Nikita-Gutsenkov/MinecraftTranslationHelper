package ladysnake.translationhelper.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Control
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.ToggleButton
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.image.Image
import javafx.util.converter.DefaultStringConverter
import ladysnake.translationhelper.controller.TranslationController
import ladysnake.translationhelper.model.data.TranslationMap
import tornadofx.*

class TranslatorView : View() {
    private val statusProperty: StringProperty = SimpleStringProperty("status: no lang folder selected")
    var status: String by statusProperty
    private val notEditingProperty: BooleanProperty = SimpleBooleanProperty(true)
    var isNotEditing: Boolean by notEditingProperty

    override val root = borderpane {
        prefWidth = 900.0
        prefHeight = 400.0
        title = "Translation O Matik"
        addStageIcon(Image(TranslatorView::class.java.getResourceAsStream("/icon.png")))

        top = menubar {
            menu("_File") {
                isMnemonicParsing = true
                item("Open", "Shortcut+O").action {
                    val langFolder =
                        TranslationController.fileChooser.showDialog(currentStage)
                    if (langFolder != null) {
                        status = "loading lang files"
                        runAsync {
                            TranslationController.chooseFolder(langFolder)
                        } success {
                            status = if (it != null) {
                                genTable(it)
                                "idle"
                            } else {
                                "no lang folder selected"
                            }
                        } fail {
                            status = "erred while reading the folder"
                        }
                    }
                }
                separator()
                item("Save all", "Shortcut+S") {
                    disableProperty().bind(notEditingProperty)
                    action {
                        TranslationController.save()
                        println("Saved !")
                    }
                }
                item("Export all", "Shortcut+Shift+S") {
                    disableProperty().bind(notEditingProperty)
                    action {
                        println("Saved as...")
                    }
                }
                separator()
                item("Exit")
            }
            menu("_Edit") {
                isMnemonicParsing = true
                item("Undo")
                item("Redo")
                separator()
                item("Cut", "Shortcut+X")
                item("Copy", "Shortcut+C")
                item("Paste", "Shortcut+V")
                separator()
                item("Find", "Shortcut+F")
                separator()
                item("Joker", "Shortcut+J") {
                    tooltip("Uses Google Translate to complete the cell based on the english value.")
                    action { TranslationController.joker() }
                }
                disableProperty().bind(notEditingProperty)
            }
            menu("_Add...") {
                isMnemonicParsing = true
                item("language file") {
                    action { TranslationController.createFile() }
                }
                item("translation key") {
                    action { TranslationController.addTranslationKey() }
                }
                disableProperty().bind(notEditingProperty)
            }
        }

        bottom = label {
            textProperty().bind(statusProperty)
        }
    }
    
    fun genTable(items: TranslationMap) {
        root.center = tableview(items) {
            isEditable = true
            readonlyColumn("Lang Key", TranslationMap.TranslationRow::key)
            for (lang in items.languages) {
                column(lang.name, valueProvider = {cell: TableColumn.CellDataFeatures<TranslationMap.TranslationRow, String> ->
                    SimpleStringProperty(cell.value[lang])
                }).apply {
                    isEditable = true
                    maxHeight = Control.USE_PREF_SIZE
                    graphic = ToggleButton().apply {
                        setPrefSize(12.0,12.0)
                        addClass(AppStyle.lockButton)
                        editableProperty().bind(selectedProperty())
                    }
                    setCellFactory {
                        TextFieldTableCell<TranslationMap.TranslationRow, String>(DefaultStringConverter())
                    }
                }
            }
            selectionModel.isCellSelectionEnabled = true
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            selectionModel.select(0)
            onEditCommit { TranslationController.onEditCommit(this) }
            requestFocus()
        }
        isNotEditing = false
    }

}
