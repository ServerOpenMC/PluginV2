# Système de Réforme - Documentation

## Table des matières
- [Vue d'ensemble](#vue-densemble)
- [Guide d'implémentation](#guide-dimplémentation)
    - [Créer une nouvelle Réforme](#créer-une-nouvelle-réforme)
    - Gérer les comportements d'une Réforme :
      - [Réforme Basique](#réforme-basique)
      - [Réforme Evenementiel](#réforme-événementielle)

## Vue d'ensemble

Le système de Réforme permet d'implémenter de nouvelles façons de jouer et d'établie ces stratégies dans le jeu.

## Guide d'implémentation

### Créer une nouvelle Réforme

1. Aller dans l'enum `Perks` :
Il vous suffit d'ajouter une nouvelle Réforme.
**L'ID de la Réforme doit être unique à chaque Réforme !**

```java
NOM(
    id,
    nom,
    description,
    itemstack
    type de perk
    cooldown si PerksType.EVENT
)
```
id devra etre un int
nom, un string
description, une liste de component
itemstack, un itemstack :
- ItemStack.of(Material)
- CustomItemRegistry.getByName("omc_items:aywenite").getBest(), un CustomItem d'ItemAdder
type de perk, PerkEvent donc soit un BASIC et EVENT

Exemple d'une Réforme basique : 
```java
TNT_RAIN(
    16,
    "§e§lPluie de TNT",
    List.of(
        Component.text("§7Lorsque vous §3tapez un joueur§7, une tnt"),
        Component.text("§7apparaitra au dessus de lui et §3explose §7!"),
    ),
    ItemStack.of(Material.TNT),
    PerkType.BASIC,
    0
)
```

Exemple d'une Réforme événementielle :
```java
IDYLLIC_RAIN(
    14,
    "§e§lPluie idyllique",
    List.of(
        Component.text("§7Fait apparaître de l'§3Aywenite §7dans votre ville pendant§3 1 §7min. §8(Cooldown : 1j)")
    ),
    ItemStack.of(Material.GHAST_TEAR),
    PerkType.EVENT,
    24 * 60 * 60 * 1000L // 1 jour
),
```
### Gérer les comportements d'une Réforme :
#### Réforme Basique
1. Faites une classe dans `mayor.perks.basic`, vous pourrez implementer Listener si votre Réforme peut être activée lorsqu'une telle chose se passe
2. Utilisez `PerkManager.hasPerk(<City>.getMayor(), id)` afin de regarder si le Maire a choisis ce Perk et assurez vous que le Maire est dans sa phase d'application `MayorManager.getInstance().phaseMayor==2`

Exemple avec un Listener:
```java
public class AyweniterPerk implements Listener {

    private static final double DROP_CHANCE = 0.01; //1%
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (MayorManager.getInstance().phaseMayor==2) {
            if (!PerkManager.hasPerk(playerCity.getMayor(), Perks.AYWENITER.getId())) return;

            if (block.getType() == Material.STONE) {

                if (random.nextDouble() < DROP_CHANCE) {
                    ItemStack ayweniteItem = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                    ayweniteItem.setAmount(2);
                    player.getInventory().addItem(ayweniteItem);
                    player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 10.0F, 0.6F);
                    MessagesManager.sendMessage(player, Component.text("§8§o*la bénédiction!*"), Prefix.MAYOR, MessageType.INFO, false);
                }
            }
        }
    }
}
```

Il est aussi autorisé d'implementer un comportement différent dans le code du plugin

Exemple de la modfication des Intérêts :

```java
    import fr.openmc.core.features.city.mayor.perks.Perks;// Interests calculated as proportion not percentage (eg: 0.01 = 1%)

public double calculateCityInterest() {
    double interest = .01; // base interest is 1%

    if (MayorManager.getInstance().phaseMayor == 2) {
        if (PerkManager.hasPerk(getMayor(), Perks.BUISNESS_MAN.getId())) {
            interest = .03; // interest is 3% when perk Buisness Man actived
        }
    }

    return interest;
}
```

#### Réforme Événementielle
1. Faites une classe dans `mayor.perks.event`, vous pourrez mettre les méthodes qui sont concernées par votre Réforme Evenementielle.
2. Allez dans `city.menu.mayor.MayorLawMenu`, vous devrez ajouter la détéction lorsque votre Réforme veut etre activé par le Maire.

Exemple : 
```java
else if(PerkManager.hasPerk(city.getMayor(),Perks.MILITARY_DISSUASION.

getId())){
    // Dissuasion Militaire (id : 13) - Perk Event
    for (UUID uuid : city.getMembers()) {
        Player member = Bukkit.getPlayer(uuid);

        if (member == null || !member.isOnline()) continue;

        MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché la §eDissuasion Militaire §f!"), Prefix.MAYOR, MessageType.INFO, false);
    }

    MilitaryDissuasion.spawnIronMan(city, 10);
    DynamicCooldownManager.use(city.getUUID(), "city:military_dissuasion", 10 * 60 * 1000L); // 10 minutes
    
    DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());

    new BukkitRunnable() {
        @Override
        public void run() {
            if (DynamicCooldownManager.isReady(city.getUUID(), "city:military_dissuasion")) {
                MilitaryDissuasion.clearCityGolems(city);
                this.cancel();
            }
        }
    }.runTaskTimer(OMCPlugin.getInstance(), 20L, 100L);
}
```
`DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());` va donc appliquer le cooldown associé a la Réforme d'Event.

`MilitaryDissuasion.spawnIronMan(city, 10);`
`DynamicCooldownManager.use(city.getUUID(), "city:military_dissuasion", 10 * 60 * 1000L); // 10 minutes`
Les lignes ci dessus sont le corps de votre Réforme, ici on spawn 10 IronMan dans la ville. Et apres 10 minutes on les clear.

## Bonnes pratiques

1. **Nommage clair** : Utilisez des noms et descriptions explicites pour les Réformes
2. **Équilibrage de la Réforme** : Assurez-vous que la Réforme n'est pas trop puissante, ou trop forte comparé à une autre
3. **Documentation** : Commentez votre code pour faciliter la maintenance
4. **Style** : Essayez au maximum de vous brancher sur les mêmes couleurs utilisées

---

Développé par [iambibi_](https://github.com/iambibi)