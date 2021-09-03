# AVC-Detection

Projet d'études pratiques en collaboration avec l'INSA de Rennes 
Pole St-Hélier - Octobre 2019 à juin 2020

Contexte
Le pôle Saint-Hélier propose le projet suivant : celui d’assurer la
surveillance médicale permanente et sur le long terme des patients
exposés à un fort risque de récidive d’un AVC, dans le but de déceler à un
stade précoce l’incidence de l’accident (prévention secondaire).

Résultats
Nous sommes parvenus à concevoir un système capable de récolter les
mesures de fréquence cardiaque des patients du Pôle Saint-Hélier à haut
risque d’AVC. Ces données sont ensuite retransmises à l’application
mobile du patient où il peut les visualiser et en n, elles sont transmises au
serveur du Pôle Saint-Hélier où les cliniciens y pourront régulièrement les
consulter.

Environnement technique
Detection de fréquence cardiaque : Capteur MAXREFDES117
Traitement de données collectées et envoi vers téléphone mobile via
Bluetooth : Arduino Nano Bluno
Stockage amovible et horodatage des données collectées : Shield data
logging ADA1141
Optimisation du stockage dans le terminal mobile : SQLite
Sécurisation des données : Algorithme DES en base 64
Application Mobile : Android Studio
Backend Application Web : Base relationnelle SQL (serveur MySQL) ,
service REST (framework Flask)
Frontend Application Web : Bootstrap, ChartJs
Gestion de projet : GanttProject, Gitlab
