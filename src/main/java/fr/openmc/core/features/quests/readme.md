# Système de Quêtes - Documentation

## Table des matières
- [Vue d'ensemble](#vue-densemble)
- [Architecture du système](#architecture-du-système)
- [Guide d'implémentation](#guide-dimplémentation)
    - [Créer une nouvelle quête](#créer-une-nouvelle-quête)
    - [Enregistrer une quête](#enregistrer-une-quête)
    - [Gérer des événements spécifiques](#gérer-des-événements-spécifiques)
- [Composants du système](#composants-du-système)
    - [Récompenses](#récompenses)
    - [Progression](#progression)
- [Utilisation avancée](#utilisation-avancée)
    - [QuestBuilder](#questbuilder)
- [Bonnes pratiques](#bonnes-pratiques)

## Vue d'ensemble

Le système de quêtes permet d'implémenter des objectifs pour les joueurs dans le jeu. Il est conçu pour être flexible, permettant de créer diverses quêtes avec différents niveaux de complexité et de récompenses.

## Architecture du système

Le système est composé des éléments suivants :

| Composant | Description |
|-----------|-------------|
| **Quête** | Objet principal qui contient un nom, une description, une icône et une liste de paliers |
| **Palier de Quête** | Niveau de progression avec un objectif et des récompenses |
| **Étape de Quête** | Sous-objectifs optionnels au sein d'un palier |
| **Récompenses** | Objets ou argent attribués lorsqu'un palier est complété |
| **Progression** | Système de suivi et sauvegarde de l'avancement des joueurs |

## Guide d'implémentation

### Créer une nouvelle quête

1. Créez une classe qui étend `Quest` :

```java
package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;

public class MinerQuest extends Quest {

    public MinerQuest() {
        // Nom de la quête: String, description: String, icône: ItemStack ou Material, Action bar: boolean *facultatif*
        super("Mineur professionnel", "Miner {target} blocs", Material.DIAMOND_PICKAXE, false);

        this.addTiers(
            new QuestTier(10, new QuestMoneyReward(100)),
            new QuestTier(50, new QuestMoneyReward(500)),
            new QuestTier(100, new QuestMoneyReward(1000))
        );
    }
}
```

pour `l'action bar`, le boolean est facultatif, par défaut il est à `false`.
Il permet d'afficher la progression de la quête dans la barre d'action du joueur, elle s'afficheras a chaque palier (50), pour éviter de spammer le joueur.

### Enregistrer une quête

Ajoutez votre quête au gestionnaire dans la méthode `loadDefaultQuests` :

```java
public void loadDefaultQuests() {
    this.registerQuests(
        new MinerQuest(),
        new FishingQuest(),
        // Autres quêtes...
    );
}
```

### Implementer une progression

1. Une progression unique (*1*):

```java
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            // incrementer la progression de la quête de 1
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }
```

2. Une progression multiple (*n*):

```java
    @EventHandler
    public void onPlayerBreakBlock(FurnaceExtractEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            if (event.getItemType().equals(Material.IRON_INGOT)) {
                int amount = event.getItemAmount(); // Récupérer le nombre d'items extraits
                // incrementer la progression de la quête de amount (n)
                this.incrementProgress(event.getPlayer().getUniqueId(), amount);
            }
        }
    }
```

3. Progression avec les étapes:
```java
        this.incrementStepProgress(playerUUID, 0); // Incrémente la progression de l'étape 0 (= index)
        this.incrementProgress(playerUUID); // Incrémente la progression de la quête
```

## Composants du système

### Récompenses

Deux types de récompenses sont disponibles :

1. **Récompenses en objets** (`QuestItemReward`) :
   ```java
   // Donne 5 diamants
   new QuestItemReward(Material.DIAMOND, 5);
   
   // Ou avec un ItemStack personnalisé
   ItemStack customItem = new ItemStack(Material.DIAMOND_SWORD);
   // Personnalisation de l'objet...
   new QuestItemReward(customItem);
   ```

2. **Récompenses en argent** (`QuestMoneyReward`) :
   ```java
   // Donne 500 unités de monnaie
   new QuestMoneyReward(500);
   ```

### Progression

La progression des joueurs est automatiquement gérée par le `QuestProgressSaveManager` :
- Les données sont stockées dans des fichiers YAML
- Emplacement : dossier `quests` du répertoire de données du plugin
- Chargement et sauvegarde automatiques

## Utilisation avancée

### QuestBuilder

Le `QuestBuilder` permet de créer des quêtes avec des étapes.

```java
Quest armorQuest = new QuestBuilder("Armure précieuse", "Fabriquer une armure complète en diamant", new ItemStack(Material.DIAMOND_CHESTPLATE))
        .tier(4, "Fabriquer une armure complète en diamant", new QuestItemReward(Material.DIAMOND, 10))
        .step("Casque en diamant", 1)
        .step("Plastron en diamant", 1)
        .step("Pantalon en diamant", 1)
        .step("Bottes en diamant", 1)
        .requireAllSteps(true)
        .build();

// Enregistrer la quête
for (int i = 0; i < armorQuest.getTiers().size(); i++) {
    this.addTier(armorQuest.getTiers().get(i)); // Ajoute chaque palier à la quête 
}  
```

## Bonnes pratiques

1. **Nommage clair** : Utilisez des noms et descriptions explicites pour les quêtes
2. **Équilibre des récompenses** : Assurez-vous que les récompenses sont proportionnelles à la difficulté
3. **Tests rigoureux** : Vérifiez que les déclencheurs d'événements fonctionnent correctement
4. **Progression intuitive** : Concevez des paliers avec une difficulté progressive
5. **Documentation** : Commentez votre code pour faciliter la maintenance

---

Développé par [Axeno](https://github.com/AxenoDev)