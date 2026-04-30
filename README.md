# Memory Game – JavaFX  
Un jeu de Memory évolutif développé en **JavaFX**, intégrant un système de niveaux, un timer dynamique, des animations 3D, un système de score, et une double journalisation (fichier + SQLite).

---

![animation](img/demo.gif "demo")

---

## Présentation
Ce projet implémente un **jeu de Memory** dans lequel le joueur doit retrouver des paires de cartes.  
La difficulté augmente automatiquement à chaque niveau, et le jeu inclut :

- Une grille dynamique qui s’adapte au niveau  
- Un timer dépendant de la difficulté  
- Des animations de flip 3D  
- Un système de score basé sur la vitesse et la taille du niveau  
- Un écran de fin de partie  
- Un système de logs (fichier + base SQLite)  
- Un joueur persistant avec icône et score  

---

## Fonctionnalités principales

### Gameplay

* Progression multi-niveaux
* Génération automatique de grilles optimisées
* Gestion des coups et des erreurs

### Scoring System

* Score basé sur :
  * Paires trouvées
  * Combos
  * Temps restant
  * Progression

### Hardcore Mode

* Grande grille fixe
* Pas de timer
* Score orienté skill uniquement
* Ranking : `S / A / B / C`

### Timer System

* Dynamique selon niveau
* Impact direct sur le score
* Désactivé en hardcore

### Animations  
Les cartes utilisent une **animation 3D Flip** pour révéler ou cacher leur icône.

### Game Modes

| Mode        | Description           |
| ----------- | --------------------- |
|  Normal     | Progression + timer   |
|  Hardcore   | Skill pur, sans timer |


### Difficulty

* `NORMAL`
* `HARD`

### Tier (auto scaling)

| Tier   | Cards |
| ------ | ----- |
| EASY   | ≤ 20  |
| MEDIUM | ≤ 36  |
| HARD   | 36+   |


### Logs  
Chaque fin de partie génère un log contenant :

*  Nom du joueur
*  Score final
*  Niveau atteint
*  Temps restant
*  Rang

Les logs sont envoyés simultanément vers :

- Un fichier texte  
- Une base SQLite  

---

### Prérequis
- Java 21+  
- JavaFX (modules graphiques)  
- SQLite (si utilisation du logger SQLite)
