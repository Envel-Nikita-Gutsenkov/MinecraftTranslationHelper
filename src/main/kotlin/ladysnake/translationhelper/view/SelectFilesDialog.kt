package ladysnake.translationhelper.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.cell.CheckBoxTableCell
import ladysnake.translationhelper.model.workspace.SourcesMap
import ladysnake.translationhelper.model.workspace.toSourceFile
import tornadofx.checkbox
import tornadofx.column
import tornadofx.tableview
import tornadofx.tooltip
import java.io.File

class SelectFilesDialog(files: List<File>) : Dialog<SourcesMap>() {

    init {
        val list = FXCollections.observableArrayList<ExtendedFile>()
        files.mapTo(list) { f ->    // Conveniently, this order makes .json be opened instead of .lang by default
            ExtendedFile(f, list.none { it.file.nameWithoutExtension == f.nameWithoutExtension && it.isToOpen.get() }, false)
        }
        this.dialogPane.content = tableview(list) {
            isEditable = true
            column("Open", CheckBoxTableCell::class) {
                setCellFactory { CheckBoxTableCell { i -> list[i].isToOpen } }
                isEditable = true
                prefWidth = 60.0
                graphic = checkbox {
                    selectedProperty().addListener { _, _, openAll ->
                        for (f in list) {
                            if (list.any { it != f && it.file.nameWithoutExtension == f.file.nameWithoutExtension && it.isToOpen.get() }) {
                                f.isToOpen.set(false)
                            } else {
                                f.isToOpen.set(openAll)
                            }
                        }
                    }
                    isSelected = false
                    tooltip("Open none/all of the files")
                }
            }
            column("Lock", CheckBoxTableCell::class) {
                setCellFactory { CheckBoxTableCell { i -> list[i].isToLock } }
                isEditable = true
                prefWidth = 60.0
                graphic = checkbox {
                    selectedProperty().addListener { _, _, lockAll ->
                        list.forEach { it.isToLock.set(lockAll) }
                    }
                    isSelected = false
                    tooltip("(Dis)allow file edition for all files")
                }
            }
            column<ExtendedFile, String>("File") { SimpleStringProperty(it.value.file.name) }

        }
        this.title = "File Selector"
        this.headerText = "Choose which files you wish to load or lock"
        this.dialogPane.buttonTypes.add(ButtonType.OK)
        this.setResultConverter { type -> if (type == ButtonType.OK) {
                list.stream().peek { println(it) }.filter { f -> f.isToOpen.value }
                    .map { f -> f.file.toSourceFile(f.isToLock.value) }.collect({SourcesMap()}, SourcesMap::plusAssign, SourcesMap::putAll)
            } else null
        }
    }

    internal class ExtendedFile constructor(val file: File, toOpen: Boolean, toLock: Boolean) {
        val isToOpen: BooleanProperty = SimpleBooleanProperty(toOpen)
        val isToLock: BooleanProperty = SimpleBooleanProperty(toLock)

        override fun toString(): String {
            return "ExtendedFile [file=$file, toOpen=$isToOpen, toLock=$isToLock]"
        }

    }

}
