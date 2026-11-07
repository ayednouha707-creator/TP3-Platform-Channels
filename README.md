# TP3 Platform Channels


1.MethodChannel : 
une question, une reponse (ex : demander la permission du micro). EventChannel : flux continu tant que Flutter ecoute (ex : le son de la ruche).

2. Le hot reload recharge seulement le Dart, le Kotlin doit etre recompile avec flutter run.

3. Oubli : le await Dart reste bloque. Deux fois : l'app plante (Reply already submitted).

4. read() est bloquant donc IO pour ne pas figer l'ecran, et events.success() doit etre sur le thread principal donc Main.

5 onCancel libere le capteur ou le micro quand Flutter arrete d'ecouter. Sinon il continue et vide la batterie.

6 Parce que la reponse de l'utilisateur arrive plus tard dans onRequestPermissionsResult.

7 battery_plus contient du code natif (MethodChannel + BatteryManager), ecrit par quelqu'un d'autre.

Exercice 3

On envoie une valeur seulement si 200 ms sont passees depuis la derniere (System.currentTimeMillis()). Ca evite de consommer la batterie pour rien.


Exercice 4 - Ruche

Module : analyse du son de la ruche avec le micro
Canal : ruche/audio (EventChannel)
API : AudioRecord + FFT
Permission : RECORD_AUDIO
