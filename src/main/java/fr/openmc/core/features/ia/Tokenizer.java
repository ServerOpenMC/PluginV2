package fr.openmc.core.features.ia;

import ai.onnxruntime.*;
import org.json.JSONObject;

import java.io.*;
import java.nio.LongBuffer;
import java.util.*;

public class Tokenizer {
    private Map<String, Integer> vocab;
    private List<String> merges;

    public Tokenizer(String vocabPath, String mergesPath) throws IOException {
        vocab = loadVocab(vocabPath);
        merges = loadMerges(mergesPath);
    }

    private Map<String, Integer> loadVocab(String vocabPath) throws IOException {
        Map<String, Integer> vocab = new HashMap<>();

        StringBuilder jsonContent = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(vocabPath));
        String line;
        while ((line = br.readLine()) != null) {
            jsonContent.append(line);
        }
        br.close();

        JSONObject json = new JSONObject(jsonContent.toString());
        for (String key : json.keySet()) {
            if (isValid(key)) {
                vocab.put(key, json.getInt(key));
            } else {
                System.out.println("Clé ignorée (caractères invalides) : " + key);
            }
        }

        vocab.put(" ", 0);

        return vocab;
    }

    private boolean isValid(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }

    private List<String> loadMerges(String mergesPath) throws IOException {
        List<String> merges = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(mergesPath));
        String line;

        while ((line = br.readLine()) != null) {
            merges.add(line);
        }

        br.close();
        return merges;
    }

    public long[] tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String[] chars = text.split("");

        for (String c : chars) {
            tokens.add(c);
        }

        for (String merge : merges) {
            String[] mergeParts = merge.split(" ");
            String pair = mergeParts[0] + " " + mergeParts[1];

            for (int i = 0; i < tokens.size() - 1; i++) {
                String currentPair = tokens.get(i) + " " + tokens.get(i + 1);
                if (currentPair.equals(pair)) {
                    tokens.set(i, mergeParts[0] + mergeParts[1]);
                    tokens.remove(i + 1);
                    break;
                }
            }
        }

        long[] tokenIds = new long[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            tokenIds[i] = vocab.getOrDefault(token, vocab.get(" "));
        }

        return tokenIds;
    }

    public String decode(long[] tokenIds) {
        StringBuilder sb = new StringBuilder();
        for (long id : tokenIds) {
            String tok = getTokenFromId(id);
            if (tok.startsWith("Ġ")) {
                sb.append(" ").append(tok.substring(1));
            } else if (tok.equals("")) {
                continue;
            } else {
                sb.append(tok);
            }
        }
        String result = sb.toString()
                .replace(" .", ".")
                .replace(" ,", ",")
                .replace(" ?", "?")
                .replace(" !", "!")
                .replace(" :", ":")
                .replace(" ;", ";")
                .replaceAll("\\s+", " ")
                .trim();

        return result;
    }

    private String getTokenFromId(long id) {
        for (Map.Entry<String, Integer> entry : vocab.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return "<UNK>";
    }

    public long getPadTokenId() {
        Integer padToken = vocab.get(" ");
        long padTokenId = (padToken != null) ? padToken : 0L;

        if (padToken == null) {
            System.out.println("Le token d'espace n'est pas défini. Utilisation d'un ID arbitraire (0).");
        }

        return padTokenId;
    }
}
