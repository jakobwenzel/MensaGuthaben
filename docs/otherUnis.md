Supporting other universities
=============================

Supported types of cards
------------------------

The App supports cards using Mifare DESFire chips. The card is structured like a file system, but with cryptic names. The challenge is to find out which file contains the current balance. This is where you come into play.

Individual files can be set to require a key to read and / or write. Since we do not have that key, we cannot change the balance. In most universities, reading the balance file does not require a key. However, if it does, the app can never support those cards.

Mifare Classic cards are currently not supported. However, patches are welcome :)

How to identify the value files
-------------------------------

The app [NFC TagInfo](https://play.google.com/store/apps/details?id=at.mroland.android.apps.nfctaginfo) can read cards and display all data.

Using this app, you can save all information the card provides into a file:
* Open the app
* Scan the card by holding the phone over it
* Press the menu button
* Press "Save tag to file"
* Choose a filename and press save

You need to do this both before and after you pay for something. Afterwards, either you can search for differences in the files yourself, or you can send them to me to analyze. However, be aware that the information read from the card may contain personal information (such as your name, your matriculation number, ...). 

If the files are identical, you are out of luck: The value is either not stored on the card at all, or in a file only accessible with a key.

Universities that cannot be supported
-------------------------------------
* TU Berlin
