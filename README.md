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

# Installation et Configuration 

## Prérequis

1. **Java 21+** : Assurez-vous d’avoir **Java 21** ou une version supérieure installée.

2. **JavaFX** : Le jeu utilise **JavaFX** pour l’interface graphique, donc vous devez installer le SDK JavaFX.

3. **Maven** : Vous aurez besoin de **Maven** pour gérer les dépendances et construire le projet.

4. **SQLite** : Si vous souhaitez utiliser la fonctionnalité de logs dans une base de données SQLite, assurez-vous d’avoir un **driver SQLite JDBC** compatible.

---

## Étapes d'installation

1. **Clonez le projet**

   Clonez le projet depuis le dépôt GitHub (ou téléchargez l'archive et extrayez-la) :

   ```bash
   git clone git@github.com:AbrariAB/ppe.git
   ```

2. **Configurer l'environnement JavaFX**
   Si vous utilisez **JavaFX 25** ou supérieur, vous devez spécifier le chemin vers les modules JavaFX. Ajoutez la variable d'environnement suivante :

   * **Windows** :

     ```bash
     set PATH_TO_FX=C:\path\to\javafx-sdk-25.0.2\lib
     ```

   * **Linux/Mac** :

     ```bash
     export PATH_TO_FX=/path/to/javafx-sdk-25.0.2/lib
     ```

3. **Vérifiez les dépendances Maven**
   Le projet utilise **Maven** pour gérer ses dépendances. Ouvrez un terminal à la racine du projet et exécutez la commande suivante pour télécharger toutes les dépendances nécessaires :

   ```bash
   mvn clean install
   ```

   Maven téléchargera toutes les dépendances listées dans le fichier `pom.xml` (JavaFX, SQLite, JUnit, etc.).

4. **Compiler le projet avec Maven**

   Compilez et créez le fichier JAR exécutable avec la commande suivante :

   ```bash
   mvn package
   ```

   Le fichier JAR sera généré dans le dossier `target` sous le nom `Mnemo-1.0-SNAPSHOT.jar` (ou le nom que vous avez configuré).

5. **Exécuter le jeu**

   Une fois le projet compilé, vous pouvez lancer le jeu en exécutant le fichier JAR avec la commande suivante :

   * **Windows** :

     ```bash
     java -jar target/Mnemo-1.0-SNAPSHOT.jar --module-path %PATH_TO_FX% --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.media
     ```

   * **Linux/Mac** :

     ```bash
     java -jar target/Mnemo-1.0-SNAPSHOT.jar --module-path $PATH_TO_FX --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.media
     ```

   Cette commande démarre l’application en utilisant les plugins JavaFX nécessaires (maven).
   
    ```bash
     mvn clean javafx:run
     ```

---

## Résolution des problèmes

### Erreur de module JavaFX non trouvé

Si vous obtenez une erreur concernant les modules JavaFX manquants, assurez-vous que la variable d'environnement `PATH_TO_FX` pointe correctement vers le répertoire **lib** de votre SDK JavaFX.

### Problème de dépendances Maven

Si Maven ne parvient pas à télécharger certaines dépendances, essayez de forcer une mise à jour des dépendances avec :

```bash
mvn clean install -U
```

Cela force Maven à récupérer les dernières versions des dépendances.

### Problème d’accès à SQLite

Si vous avez des erreurs liées à SQLite, assurez-vous que le driver JDBC SQLite est bien inclus dans les dépendances Maven et que la base de données SQLite est accessible en écriture.

---

