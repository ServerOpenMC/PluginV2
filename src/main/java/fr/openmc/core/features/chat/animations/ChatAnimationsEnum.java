package fr.openmc.core.features.chat.animations;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class ChatAnimationsEnum {
    private ChatAnimationsEnum() {}

    public enum ChallengeType {
        MINE_STONE("Miner 100 blocs de stone", Kind.MINE_COUNT, null, Set.of(Material.STONE, Material.DEEPSLATE), 100),
        JUMP("Sautez 10 fois", Kind.JUMP, null, Collections.emptySet(), 10),
        KILL_SKELETONS("Tuez 5 squelettes", Kind.KILL_MOB, EntityType.SKELETON, Collections.emptySet(), 5),
        PLACE_LANTERNS("Placez 10 lanternes", Kind.PLACE_BLOCK, null, Set.of(Material.LANTERN, Material.SOUL_LANTERN), 10),
        KILL_ZOMBIES("Tuez 5 zombies", Kind.KILL_MOB, EntityType.ZOMBIE, Collections.emptySet(), 5),
        MINE_DIAMOND("Miner 10 minerais de diamant", Kind.MINE_MATERIAL, null, Set.of(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE), 10),
        PLACE_TORCHES("Placez 20 torches", Kind.PLACE_BLOCK, null, Set.of(Material.TORCH), 20),
        FISH("Pêchez 5 poissons", Kind.FISH, null, Collections.emptySet(), 5),
        MINE_COAL("Miner 20 minerais de charbon", Kind.MINE_MATERIAL, null, Set.of(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE), 20),
        PLACE_BEACON("Placez 1 beacon", Kind.PLACE_BLOCK, null, Set.of(Material.BEACON), 1);

        private final String desc;
        private final Kind kind;
        private final EntityType entityType;
        private final Set<Material> materials;
        private final int target;

        ChallengeType(String desc, Kind kind, EntityType entityType, Set<Material> materials, int target) {
            this.desc = desc;
            this.kind = kind;
            this.entityType = entityType;
            this.materials = (materials == null) ? Collections.emptySet() : materials;
            this.target = target;
        }

        public String getDescription() { return desc; }
        public Kind getKind() { return kind; }
        public EntityType getEntityType() { return entityType; }
        public Set<Material> getMaterials() { return materials; }
        public int getTarget() { return target; }

        public enum Kind { MINE_COUNT, JUMP, KILL_MOB, PLACE_BLOCK, MINE_MATERIAL, FISH }
    }

    public enum QuizType {
        CREEPER("Quel mob explose quand il s'approche du joueur?", Collections.singletonList("creeper")),
        HEARTS("Combien de coeurs a un joueur par défaut? (nombre)", Collections.singletonList("10")),
        ENDER_DRAGON("Dans quelle dimension trouve-t-on l'Ender Dragon?", Arrays.asList("end", "the end", "l'end")),
        REDSTONE_TOOL("Quel outil est requis pour miner la redstone?", Collections.singletonList("pioche")),
        DIAMOND_BLOCK("Quel matériau drop du diamant? (nom du bloc)", Arrays.asList("diamond_ore", "minerai de diamant", "minerai_de_diamant", "block de diamants")),
        SADDLE("Quel objet permet de monter un cheval?", Arrays.asList("selle", "une selle")),
        END_BOSS("Quel est le nom du boss que l'on trouve dans l'End?", Arrays.asList("ender dragon", "enderdragon", "dragon de l'end")),
        NETHER_PORTAL_BLOCK("Quel bloc est nécessaire pour construire un portail du Nether?", Arrays.asList("obsidian", "obsidienne")),
        AXE_TOOL("Quel outil est le plus rapide pour couper du bois?", Arrays.asList("hache", "une hache")),
        SKELETON_BONE("Quel mob lâche des os?", Arrays.asList("squelette", "skeleton")),
        RAIL_MATERIAL("Quel matériau sert à fabriquer des rails?", Arrays.asList("fer", "iron")),
        FISHING_ITEM("Comment appelle-t-on l'item utilisé pour pêcher?", Arrays.asList("canne a peche", "canne à pêche", "fishing rod", "canne")),
        GLASS_FROM_SAND("Quel bloc s'obtient en fondant du sable?", Arrays.asList("verre", "glass")),
        PILLAGER_HEARTS("Quelle créature transporte des coeurs de navet?", Arrays.asList("pillageur", "raider")),
        TELEPORT_CMD("Quel objet utilise-t-on pour téléporter un joueur dans le nether?", Arrays.asList("portal", "portail", "nether portal", "portail du nether")),
        BONEMEAL("Comment appelle-t-on l'élément qui fait pousser les cultures rapidement?", Arrays.asList("bone meal", "farine d'os", "poudre d'os"));

        private final String question;
        private final List<String> answersLower;

        QuizType(String question, List<String> answers) {
            this.question = question;
            this.answersLower = new ArrayList<>();
            for (String a : answers) this.answersLower.add(a.trim().toLowerCase());
        }

        public String getQuestion() { return question; }
        public boolean matches(String msg) { return answersLower.contains(msg.trim().toLowerCase()); }
        public String getRandomAnswer() {
            if (answersLower.isEmpty()) return "";
            return answersLower.get(new Random().nextInt(answersLower.size()));
        }
    }
}
