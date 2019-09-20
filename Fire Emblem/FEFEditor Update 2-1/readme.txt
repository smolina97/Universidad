Thank you for downloading FEFEditor - I hope it can be of use to you with regards
to modifying Fire Emblem Fates. I've designed this tool from the ground up to function
as a development environment for my own projects, so it should come equipped with
everything you need to get started with adding and designing new characters and chapters.

I'll go through a couple of housekeeping things below, but you don't really need to pay
attention to those unless you need help or want some info about modifying the source.
There's no real installation to go through for this tool. If you have an up to date
version of Java, you can run the JAR file to get started.

Things to Note
-------------------------------------------------------
- If you are building your hack on top of the Same-Sex Expansion Patch, make sure to swap out
your "Supports.xml" file for the correct one.
- Simply double click on an entry in the main window to open the tool. FEFNightmare and Fates
Script will open as entirely separate programs, but the rest are integrated into the editor.
- Place any modules that you want to show up FEFNightmare inside the "Modules" folder. They should
show up in the list next time you open the program.

Notes on Source Code
-------------------------------------------------------
- I still consider myself a beginner programmer. You're probably going to run into some pretty
scary code, so prepare yourself.
- Fates Script and FEFNightmare are considered separate programs altogether. I've included their
source code in separate directories from FEFEditor's. Please note that these programs also come
with their own separate licensing.
- If you have any suggestions or potential changes that you'd like to add to the tool, feel free
to message me through either Gbatemp or SerenesForest. I may create a github repository in the
future to make this process easier, though for the time being one does not exist.

Third-Party Software & Licenses
-------------------------------------------------------
- FEFNightmare is built off of Nightmare 2.0. Additionally, functions from the Apache Commons are
used for handling the module list and identifying compressed files. FEFNightmare uses the
GNU General Public License v3.0. The Apache Commons are licensed under
the Apache License v2.0.
- Fates Script uses RichTextFX for syntax highlighting and general text editing. Fates Script is licensed
under the GPLv2 while RichTextFX is licensed a BSD 2-clause license.
- FEFEditor itself is licensed under GPLv3.

Troubleshooting
-------------------------------------------------------
This tool is in its early stages. Additionally, it covers a LOT of different aspects of Fates
hacking relative to previous tools. Therefore, I expect there to be plenty of bugs that I failed
to catch during testing. On that note, always backup your work before doing anything major and
save frequently.

If you ever encounter any bugs, please report them. I can't fix bugs if I don't know that they
exist in the first place!

Credits
-------------------------------------------------------
SecretiveCactus, SciresM, and Einstein95 - Both FEFEditor and FEFNightmare make use of code for
handling lz11 compressed files. The code used for this is derived from Einstein95's DSDecmp and SciresM's
FEAT. That code was ported over to Java by SecretiveCactus.
Hextator - The developer of Nightmare 2.0, which FEFNightmare uses as a base.
RainThunder - Developed the original scripts for injecting data into Fates' bin files and created Fates'
Nightmare modules. A huge portion of this program was created using his resources as a reference.
DeathChaos25, TildeHat - For their general contributions to the 3DS Fire Emblem hacking scene.

Happy editing!