(Sorry for the long read)
HOW TO PLAY:
1) Select your weapons
    - Players may not have duplicate weapons (even between players there cannot be duplicates)
    - If you want to access weapons that are blocked by the ready to fight bar, do something invalid
      (Try to put in a weapon that will be a duplicate) and the ready bar will disappear
    - You can also just exit the choose your fight game state by pressing the back button and reselect your weapons
2) Click the ready to fight bar to enter battle
3) Shoot the boss
    - You can quickly tap one of your two fire buttons to launch a basic attack
    - Or you can hold one of your two fire buttons to charge a charged attack
        - You may only charge 1 attack at a time
        - You can only charge a charged attack if you have at least one skill point (white bars at the bottom)
            - You regenerate skill points passively
            - Or you can hit the boss or your friend with basic attacks to gain skill points
        - Release the fire button you're holding to fire at any time
            - You consume half a skill point if it's less than halfway charged, and a full one otherwise
        - There is a time limit to how long you can hold it
        - Use as many skill points as possible, as they increase your score
    - After firing a projectile, your blaster will be on cooldown for a short while. Charged attacks make your blaster go on cooldown for a long while.
      So if you can't shoot for a while, it's probably because you just used a charged attack
    - Look in the slideshow in game to see what keys are your controls
    - Friendly fire is on, so try not to shoot your friend, since that will reduce your score
    - You can press escape at any time to pause the game (In case you need to dookie mid-fight)
        - When you press resume, there is a READY? animation so you don't need to be immediately ready to fight
4) Kill the boss or lose
    - You gain 50% if you beat the boss and the omega people on the sides will be happier and not drawn by me
    - Formative stats don't count towards your score, summative stats do
    - If you don't name your battle, it will be called "THE UNKNOWN BATTLE" (I might add numbers after "THE UNKNOWN BATTLE" during the summer)

NOTES:
- Version 3 is the latest version of the game. You should run that one. I keep versions in case I want to look at or copy old code that I changed.
- If you don't have a number pad, you can change the controls of the players in lines 248-249 of OmegaFight3 (I might add the ability to change controls in the summer)
- I fulfilled the HashMap requirement with button numbers
- I fulfilled the Sorting requirement with the battle log (Try sorting by title or grade in the battle log)
- I did all of the stuff I promised to do in the initial proposal and also:
    - Music (You should turn up your volume before you run the game, I think the music is pretty fire)
    - SFX
    - Smoke trails when you get knocked back
- Sometimes you'll walk off a platform, immediately jump but notice that that immediate jump didn't count as a double jump even though you were airborne.
  This is intentional.
    - This is done in order to make the game feel more forgiving with platforming (and also because I suck at platforming)
    - To be precise, you have exactly 5 frames after you walk off a platform to jump and not count it as a double jump
    - The term for this is called "Coyote Time". You can read more about it here:
        - https://gamerant.com/celeste-coyote-time-mechanic-platforming-impact-hidden-mechanics/
- One of the major things that differentiates this game from Omega Fight 2 (on top of the bosses and the much larger variety of weapons)
  is the jumping physics. If you quickly tap and release jump, you'll do a quick, small jump. But the longer you hold your jump button,
  the higher you will jump. This is why your jump height varies sometimes.
- Another major difference from Omega Fight 2 is projectile to projectile collision. Certain projectiles can collide with other projectiles and some can't.
    - Which projectile(s) die is determine by their shouldDieTo methods and their durability
    - The following player projectiles can hit projectiles:
        - Bullet
        - Rocket
        - Shotgun
        - Firework
        - Missile
        - Sniper
        - Bouncer
        - Spike
    - The following player projectiles can't:
        - Spammer
        - Laser
        - Boomerang
        - Splitter
- Because I didn't to make 38-ish java files, I have consolidated similar classes into java files in order to reduce the amount of scrolling and control-f ing I have to do.
- There are some similarities in code in projectile processing, but I did that because there were a lot of variations and tweaks between each projectile's processing and I felt it was less of a pain and A LOT easier
  to work with copy pasted solitary methods with a TON of helper methods that I can individually customize to my liking than to have one or two general methods with a kajillion parameters for all of the
  different variations in processing for each projectile.
- It is possible to gain a score below 0. This is intentional. It can be done if both players score badly enough in the summative categories and deal a lot of damage to each other
- OmegaFight3 is the driver class
- If you'd like to see what happens when the battle log is empty, delete everything in the text file and type "0". (First number represents number of battles stored)

BUGS:
- (More of a hardware limitation than a bug) Cheap keyboards can’t register very specific 3+ key combos
    - Low-end keyboards block signals when too many keys are pressed.
    - Gaming keyboards avoid this with "N-key rollover" (NKRO).
    - Try this website to see what combinations of keys you can't press together!
        - https://www.mechanical-keyboard.org/key-rollover-test/- 
    - This is called "Keyboard Ghosting" or "Keyboard Rollover". Here are my sources
        - https://discourse.processing.org/t/keypressed-for-multiple-keys-pressed-at-the-same-time/18892/5
        - https://stackoverflow.com/questions/38847661/java-keylistener-multiple-keys
        - http://board.flashkit.com/board/showthread.php?789015-wont-respond-to-keyboard-up-left-and-space-at-the-same-time
        - http://forums.steampowered.com/forums/showthread.php?t=1928521
        - http://www.tomshardware.co.uk/answers/id-2159074/alt-space-bar-work-holding-left-arrow.html
        - https://unix.stackexchange.com/questions/268850/leftupspace-keys-not-working-on-thinkpad-x201
    - This should be a problem for all games if you have a not-so-great keyboard
        - Although it is more noticeable on my game since so many keys are pressed on one keyboard.

CHEAT KEYS (Must have CHEATS final variable (line 31) in OmegaFight3 set to true to use):
In-game gamestate:
    - Ctrl + K - Hurt the boss (Hold it for 4 seconds-ish to kill the boss)

Class(es) with thorough method comments:
- OmegaFight3

Class(es) with data encapsulation:
- Smoke (getter and setter used in line 755 of Omegaman)