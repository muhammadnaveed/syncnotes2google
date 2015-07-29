This project is maintained, a new version of the tool is being reworked to support oAuth2 and Google Calendar API v3.

## Sync your Lotus Notes Calendar with Google Calendar. ##

This project is created since I needed to sync my HTC magic with Lotus Notes.
I have been searching the net for free software that was working for me, but nothing there...

This project is based on the [GooCalSync](http://openntf.org/projects/pmt.nsf/ProjectLookup/GooCalSync) project.
I have removed, rewritten code and added [Domingo](http://domingo.sourceforge.net/) to it.


### Install and Setup ###

Make sure You have at least JRE 1.5 installed, other wise You can find it here at [java download](http://java.sun.com/javase/downloads/index.jsp).


Download [syncnotes2google-0.0.5.zip](http://syncnotes2google.googlecode.com/files/syncnotes2google-0.0.5.zip) and unzip it in to a preferred folder ex 'c:\syncnotes2google\'.

Edit the **syncnotes2google.bat** file:

Set the **notes-path** to be pointing to your Notes installation ex. `set notes-path=C:\Program Files\Lotus\Notes`

Set the **notes-jar** to be pointing relative to your Notes.jar from the notes installation. ex. `set notes-jar=Notes.jar` or `set notes-jar= jvm/lib/ext/Notes.jar`

Save and close.

Edit the **sync.properties** file:

```
#Google information
# Google account email
google.account.email=nnnn@gmail.com

# Google account password
google.account.password=****

# Google calendar name to sync with
google.calendar.name=Calendar

# Google default reminder time
google.calendar.reminderminutes=15

#Notes information
# Notes File/Preferences/Location Preferences.../Servers/'Home/mail server', if there are \ in the path replace them with /
notes.domino.server=server

# Notes File/Preferences/Location Preferences.../Mail/'Mail file', if there are \ in the path replace them with /
notes.mail.db.file=mail.nsf

#Sync information
# Valid directions notes-to-google (default), google-to-notes ,bi-direction
sync.direction=notes-to-google

# Number of days (ex. 15d) or month (ex. 2m) back in time, default 14 days
sync.start=14d

# Number of days(ex. 15d) or month (ex. 2m) in the future, default 3 month
sync.end=3m
```


---


Furthermore, the following property should be set in Lotus Notes:

Go to menu File -> Security -> User Security....

And check the _Don't prompt for a password from other Notes-based programs user settings_ setting.

This prevents Notes from asking for your password every time you start a Java application that accesses Lotus Notes.

And now just run the **syncnotes2google.bat** and the calendars are synchronized.

If you have problem that it prompts for password make sure that you are loged into youre Notes application.


---

`SyncNotes2Google` is creating 2 files in %USER\_HOME%/.syncnotes2google/ one with last sync time and one with a reference table between notes and google entries.

Be careful to remove the files. if they are removed maybe create duplicates.
But do remove them if one calendar is cleared and you want to sync entries back.


---

Thanx to [sharon.dagan](http://code.google.com/u/sharon.dagan/) for make it possible to run on MacOS se http://code.google.com/p/syncnotes2google/issues/detail?id=20


---

If you are experience problems read the [issues list](http://code.google.com/p/syncnotes2google/issues/list) there are some real good answers there.
A very big commendation to all of You how have answering everyone's problems in the issues list!


---

For more information see the [Domingo](http://domingo.sourceforge.net/) Documentation (Installation and Getting Started).
