PEBProject
==========


  Achtung: Dies ist ein Proof-of-concept, kein fertiges Produkt!
-----------------------------------------------------------------

Die PEB-App kann für Android Geräte zusammen mit der Pebble-Uhr dazu verwendet werden, 
die letzten drei Songs, die auf SRF 3 gespielt wurden, abzufragen.

Die PEB-App arbeitet in zwei verschiedenen Modi:


Pull
----
Die PEB-App registriert sich als Musik-Player auf dem Android-Gerät. Wird die PEB-App in der Pebble Standard-App 
als Musik-Player festgelegt, so kann man auf der Pebble-Uhr über die vorinstallierte App zur Kontrolle eines MP3-Players mit der 
PEB-App kommunizieren:
   - Play oder Pause startet eine Abfrage nach dem aktuellen Song auf SRF 3.
   - Mit der Vorwärts-Taste kann man zum letzten oder vorletzten Song zurück.
   
Push
----
Ein Click auf den Button "Current Song" löst eine Abfrage aus über das Web. Das Resultat wird auf der Pebble-Uhr mit dem
standard Messaging Mechanismus dargestellt.

Ein Click auf den Button "Start Service" startet einen Service, der periodisch überprüft, welches Song zur Zeit gespielt
wird und bei einem Wechsel den neusen Titel auf der Pebble-Uhr anzeigt.



