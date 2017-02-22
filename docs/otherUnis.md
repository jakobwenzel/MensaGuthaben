Supporting other universities
=============================

Supported types of cards
------------------------

The App supports cards using Mifare DESFire chips. The card is structured like a file system, but with cryptic names. The challenge is to find out which file contains the current balance. This is where you come into play.

Individual files can be set to require a key to read and / or write. In most universities, reading the balance file does not require a key. However, if it does, the app cannot support those cards.

Also, since this question keeps coming up: The balance almost certainly will require a key to write. Also, there is probably a central database that keeps track of all balances, so modifications would be noticed.

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

You need to do this both before and after you pay for something. Afterwards, either you can search for differences in the files yourself, or you can send them to me to analyze. Please include the actual balance that your card had at the time of reading. However, be aware that the information read from the card may contain personal information (such as your name, your matriculation number, ...). Posting it in a Github issue is probably not advisable.

If the files are identical, you are out of luck: The value is either not stored on the card at all, or in a file only readable with a key.

Universities that cannot be supported
-------------------------------------
Dumps of cards of these universities have been analyzed and no differences have been found:

* TU Berlin

If your university is on this list
----------------------------------
If the data is on the card at all, it is encrypted. I do not know why your administration chose to do so. As breaking the encryption is ethically and legally questionable, we cannot go that way. 

### If you work for the administration / Studierendenwerk as someone who can decide stuff

You have multiple options to go forward. You have to decide which way works best for you:

* Fork this app and publish your own build that contains the key.

  However, be aware that this app is licensed under GPL. If a binary is made public, the source code has to be made public as well. This does not include the key.
  
* Send me the key.

  Obviously, if the key that can read the balance has too many permissions, you should not do that. I will do my best to include it in the app, while not to making it key public.
  
* Lift the requirement for a key to read the balance.

  This probably requires some changes to your infrastructure.

### If you are everyone else

The only way to go forward is to contact the administration of your university / Studierendenwerk. Tell them that it would be great to have this app (or something like it) at your university. Get your friends to contact them as well. Ask your student council (Fachschaft) for help. Hopefully, you can make an impact.

