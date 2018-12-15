# MinecraftTranslationHelper
A tool to aid in the creation of translation files

Mod translations are annoying. You need to fiddle with text files and switch back and forth in your editors to make sure you get it right.
And then the mod updates, new translations are added and you are lost among invalid translations and untranslated features.
If you are a modder, you may also be afraid of changing translation keys as that may break existing translations.

This tool simplifies your work by providing an intuitive interface to work with.

## Usage instructions
* Download MinecraftTranslationHelper on the [release page](https://github.com/Pyrofab/MinecraftTranslationHelper/releases). Preferably put it near your translations.
* Run it and use the "Load a lang folder" button to navigate to the lang folder of the mod you're currently translating.
* After selecting a lang folder, a popup will appear. You can just press 'OK' if you want to edit all the existing translation files or you can deselect files you do not want to see and lock files you do not want to edit.
<img src="https://image.prntscr.com/image/Sle1DGqQT5KEcPPQ8KEi6A.png" alt="popup screenshot" height="225" width="125"/>
* The tool wil then load all selected translations side by side with the key on the left side. You can then edit any field and the program will do the rest.
* Yes you can drag and drop columns or sort them differently.
* Do you want to delete or change a translation key ? Right click in the table to open the context menu.
* If you run out of inspiration, you can use the 'Joker' button. This will autocomplete the cell based on Google Translate's answer. If you use this feature, please double check the result and keep the wacky translations at a minimum.
* When you are done, click the save button to apply the changes. This will only affect changed files.

Note : this program does not care about comments and fancy line separators. Upon hitting save, all irrelevant information will be erased from any edited file.

![screenshot](https://image.prntscr.com/image/c4djsaUyTPGiuJvF4q9AWQ.png)
