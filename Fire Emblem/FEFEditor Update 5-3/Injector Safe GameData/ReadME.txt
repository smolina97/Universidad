This GameData.bin.lz is safe to use with the Character, Class, and Item Injector in FEFNightmare.

It also includes the DLC Items and Classes so newly injected classes and item don't overwrite what is found in the DLC.

A patch for Gay Fates is also included 
-----------------------------------------------------------------------

To patch your already existing game data to work with Nightmare injectors
you have to add the line 

0x00000000

at the end of the Character, Class, and Item blocks
the end of each and beginning of the next blocks are,

"PTR1: 20" For Character

"PTR2: JCID_飛行" For Class

"PTR1: 7" For Items

Just put the line "0x00000000" before the lines listed and your GameData
should be injector safe.