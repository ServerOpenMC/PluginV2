package fr.openmc.core.commands.fun;

import ai.onnxruntime.*;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.ia.Tokenizer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IACommand {

    private final OrtEnvironment env;
    private OrtSession session;
    private Tokenizer tokenizer;

    @SuppressWarnings("ALL")
    @SneakyThrows
    public IACommand() {
        this.env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();

        File dataFolder = OMCPlugin.getInstance().getDataFolder();
        File modelFile = new File(dataFolder, "ia/gpt2.onnx");
        File vocabFile = new File(dataFolder, "ia/vocab.json");
        File mergesFile = new File(dataFolder, "ia/merges.txt");
        File tempVersionFile = new File(dataFolder, "ia/versiontemp.json");
        File versionFile = new File(dataFolder, "ia/version.json");

        String url = "https://github.com/Olivesigne/OpenMCIAStructure/releases/download/FullIA/";

        URL urlco = new URL(url + "gpt2.onnx");
        URLConnection connection = urlco.openConnection();
        int fileSize = connection.getContentLength();

        String content = new String(Files.readAllBytes(Paths.get(dataFolder + "/ia/version.json")), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(content);
        String version = json.getString("version");

        String versionGit = null;
        if (versionFile.exists()) {
            downloadIA(url + "version.json", tempVersionFile);
            String tempContent = new String(Files.readAllBytes(Paths.get(dataFolder + "/ia/versiontemp.json")), StandardCharsets.UTF_8);
            JSONObject tempJson = new JSONObject(tempContent);
            versionGit = tempJson.getString("version");

            Bukkit.getLogger().info("Version locale ia : " + version + ", Version distante ia : " + versionGit);
        }

        if (modelFile.exists()) {
            Bukkit.getLogger().info("Taille du modèle dans la release en octets : " + fileSize);
            Bukkit.getLogger().info("Taille du modèle déjà stocké en octets : " + modelFile.length());
        }

        if (fileSize == -1) {
            Bukkit.getLogger().warning("Impossible de récupérer la taille du fichier distant. Téléchargement forcé.");
        }

        Bukkit.getLogger().info("Chargement de l'IA...");
        if (!modelFile.exists()) {
            downloadIA(url + "gpt2.onnx", modelFile);
            downloadIA(url + "merges.txt", mergesFile);
            downloadIA(url + "vocab.json", vocabFile);
            downloadIA(url + "version.json", versionFile);
        } else if (modelFile.length() != fileSize || version != versionGit) {
            Bukkit.getLogger().info("Le fichier du modèle IA va être mis à jour ou réparé !");
            downloadIA(url + "gpt2.onnx", modelFile);
            downloadIA(url + "merges.txt", mergesFile);
            downloadIA(url + "vocab.json", vocabFile);
            downloadIA(url + "version.json", versionFile);
        } else {
            Bukkit.getLogger().info("Le modèle IA est déjà à jour !");
        }
        Bukkit.getLogger().info("IA chargée !");

        if (!modelFile.exists()) {
            Bukkit.getLogger().info("§cErreur : le modèle IA n’a pas été chargé correctement.");
            return;
        }

        tempVersionFile.delete();
        this.session = env.createSession(modelFile.getAbsolutePath(), opts);
        this.tokenizer = new Tokenizer(vocabFile.getAbsolutePath(), mergesFile.getAbsolutePath());
    }

    @Command("iachat")
    @Description("Pose une question à une IA !")
    @SuppressWarnings("ALL")
    public void onIA(Player player, @Named("question") String question) {
        if (question.length() == 0) {
            player.sendMessage("§cUtilisation : /ia <question>");
            return;
        }

        player.sendMessage("§7🤖 Je réfléchis à ta question : §e" + question);

        new Thread(() -> {
            try {
                String prompt = "Question : " + question + "\nRéponse : ";

                List<Long> generatedIds = new ArrayList<>();
                for (long id : tokenizer.tokenize(prompt)) {
                    generatedIds.add(id);
                }

                int maxGeneratedTokens = 50;
                for (int i = 0; i < maxGeneratedTokens; i++) {
                    long[] inputArray = new long[generatedIds.size()];
                    long[] attentionMask = new long[generatedIds.size()];
                    for (int j = 0; j < generatedIds.size(); j++) {
                        inputArray[j] = generatedIds.get(j);
                        attentionMask[j] = 1L;
                    }

                    Map<String, OnnxTensor> inputs = new HashMap<>();
                    inputs.put("input_ids", OnnxTensor.createTensor(env, LongBuffer.wrap(inputArray), new long[]{1, inputArray.length}));
                    inputs.put("attention_mask", OnnxTensor.createTensor(env, LongBuffer.wrap(attentionMask), new long[]{1, inputArray.length}));

                    OrtSession.Result result = session.run(inputs);
                    float[][][] logits = (float[][][]) ((OnnxTensor) result.get(0)).getValue();
                    float[] lastLogits = logits[0][logits[0].length - 1];

                    int nextTokenId = argMax(lastLogits);
                    generatedIds.add((long) nextTokenId);

                    if (nextTokenId == 50256) {
                        break;
                    }

                    for (OnnxTensor tensor : inputs.values()) tensor.close();
                    result.close();
                }

                long[] finalTokenIds = generatedIds.stream().mapToLong(Long::longValue).toArray();
                String fullOutput = tokenizer.decode(finalTokenIds);

                String response;
                int index = fullOutput.indexOf("Réponse :");
                if (index != -1) {
                    response = fullOutput.substring(index + "Réponse :".length()).trim();
                } else {
                    response = fullOutput.trim();
                }

                response = new String(response.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

                player.sendMessage("§aRéponse de l'IA : §f" + cutText(removeAccents(response)));
                player.sendMessage("§c❗ Ceci est l'ia en cours d'entraînement. Elle n'est pas au point !§f");

            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage("§cUne erreur est survenue lors de la génération.");
            }
        }).start();
    }

    public String removeAccents(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case 'é': case 'è': case 'ê': case 'ë': sb.append('e'); break;
                case 'à': case 'á': case 'ä': case 'â': sb.append('a'); break;
                case 'ç': sb.append('c'); break;
                case 'î': case 'ï': sb.append('i'); break;
                case 'ô': case 'ö': sb.append('o'); break;
                case 'ù': case 'ü': sb.append('u'); break;
                default: sb.append(c); break;
            }
        }
        return sb.toString();
    }

    private int argMax(float[] array) {
        int maxIndex = 0;
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @SuppressWarnings("deprecation")
    private void downloadIA(String urlStr, File file) throws IOException {
        file.getParentFile().mkdirs();

        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        int fileSize = connection.getContentLength();

        try (InputStream in = connection.getInputStream()) {
            long start = System.currentTimeMillis();

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            try (var out = Files.newOutputStream(file.toPath())) {
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    if (totalRead % (512 * 1024) < 8192 || totalRead == fileSize) {
                        long now = System.currentTimeMillis();
                        double elapsedSeconds = (now - start) / 1000.0;
                        double speed = totalRead / elapsedSeconds;
                        double timeRemaining = (fileSize - totalRead) / speed;

                        double progress = (double) totalRead / fileSize;
                        int barLength = 20;
                        int filledLength = (int) (barLength * progress);
                        String bar = "█".repeat(filledLength) + "-".repeat(barLength - filledLength);

                        Bukkit.getLogger().info(String.format(
                                "Téléchargement %s : [%s] %.1f%% - %.1f s restantes",
                                file.getName(),
                                bar,
                                100.0 * progress,
                                timeRemaining
                        ));
                    }
                }
            }
        }
    }

    private String cutText(String text) {
        Pattern pattern = Pattern.compile("https://[^\\s)]+");
        Matcher matcher = pattern.matcher(text);

        String result;

        if (matcher.find()) {
            int endOfLink = matcher.end();
            result = text.substring(0, endOfLink);
        } else {
            int pointIndex = text.indexOf(".");
            result = (pointIndex != -1) ? text.substring(0, pointIndex) : text;
        }

        return result;
    }
}
