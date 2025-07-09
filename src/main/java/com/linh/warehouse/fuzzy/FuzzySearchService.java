package com.linh.warehouse.fuzzy;

import com.linh.warehouse.entity.Product;
import com.linh.warehouse.repository.ProductRepository;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class FuzzySearchService {

    private final ProductRepository productRepository;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public FuzzySearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private static final Map<String, List<String>> SYNONYMS = Map.ofEntries(
            Map.entry("ao", List.of("ao", "shirt", "top", "tee", "tshirt", "ao thun", "ao phong")),
            Map.entry("quan", List.of("quan", "pants", "jeans", "short", "quan vai", "quan tay")),
            Map.entry("vay", List.of("vay", "dam", "dress", "skirt", "vay lien", "vay xoe")),
            Map.entry("short", List.of("short", "quan short", "quan dui")),
            Map.entry("hoodie", List.of("hoodie", "ao hoodie", "ao khoac mu")),
            Map.entry("bo", List.of("bo", "jean", "denim", "quan bo", "ao bo", "vay bo")),
            Map.entry("somi", List.of("somi", "so mi", "shirt", "ao so mi")),
            Map.entry("thun", List.of("thun", "cotton", "ao thun", "ao phong")),
            Map.entry("the thao", List.of("the thao", "gym", "jogger", "legging", "tanktop")),
            Map.entry("ni", List.of("ni", "fleece", "warm", "ao ni")),
            Map.entry("kaki", List.of("kaki", "khaki", "quan kaki", "ao kaki")),
            Map.entry("len", List.of("len", "wool", "ao len", "cardigan")),
            Map.entry("khoac", List.of("khoac", "jacket", "bomber", "ao khoac")),
            Map.entry("mu", List.of("mu", "non", "hat", "cap")),
            Map.entry("giay", List.of("giay", "shoes", "sneaker", "boot")),
            Map.entry("dep", List.of("dep", "slipper", "sandals")),
            Map.entry("khau", List.of("khau", "mask", "face mask")),
            Map.entry("trang", List.of("trang", "white")),
            Map.entry("den", List.of("den", "black")),
            Map.entry("xanh", List.of("xanh", "blue", "green")),
            Map.entry("do", List.of("do", "red")),
            Map.entry("vang", List.of("vang", "yellow")),
            Map.entry("nu", List.of("nu", "lady", "girl")),
            Map.entry("nam", List.of("nam", "men", "boy")),

            Map.entry("cotton", List.of("cotton", "vai cotton")),
            Map.entry("satin", List.of("satin", "lua")),
            Map.entry("linen", List.of("linen", "vai lanh")),

            Map.entry("form rong", List.of("form rong", "oversize")),
            Map.entry("form om", List.of("form om", "slim fit")),
            Map.entry("form suong", List.of("form suong", "regular fit")),

            Map.entry("con ho", List.of("con ho", "tiger")),
            Map.entry("hoa", List.of("hoa", "hoa tiet")),
            Map.entry("tron", List.of("tron", "basic")),

            Map.entry("maxi", List.of("maxi", "vay dai")),
            Map.entry("body", List.of("body", "om sat")),
            Map.entry("baby", List.of("babydoll", "form baby")),

            Map.entry("polo", List.of("polo", "ao co", "ao polo")),
            Map.entry("tanktop", List.of("tanktop", "ao ba lo")),
            Map.entry("jogger", List.of("jogger", "quan jogger")),
            Map.entry("legging", List.of("legging", "quan om")),
            Map.entry("bra", List.of("bra", "ao lot", "bralette")),

            Map.entry("con meo", List.of("con meo", "meo", "cat")),
            Map.entry("phong cach", List.of("phong cach", "style", "casual"))
    );



    public static String removeVietnameseDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized)
                .replaceAll("")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");
    }

    public List<Product> search(String query, int topK) {
        try {
            if (query == null || query.isBlank()) {
                return fallbackProducts(topK); // nếu không nhập gì thì fallback
            }

            String queryClean = removeVietnameseDiacritics(query.toLowerCase());
            String[] originalTokens = queryClean.split("\\s+");

            Set<String> expandedTokens = new HashSet<>();
            for (String token : originalTokens) {
                expandedTokens.add(token);

                List<String> synonyms = SYNONYMS.getOrDefault(token, List.of());
                synonyms.stream()
                        .map(t -> removeVietnameseDiacritics(t.toLowerCase()))
                        .forEach(expandedTokens::add);
            }

            List<Product> fuzzyResults = productRepository.findAll().stream()
                    .map(product -> {
                        String name = removeVietnameseDiacritics(product.getName().toLowerCase());
                        String desc = removeVietnameseDiacritics(
                                Optional.ofNullable(product.getDescription()).orElse("").toLowerCase()
                        );

                        double nameScore = scoreByTokens(expandedTokens, name, 0.2);
                        double descScore = scoreByTokens(expandedTokens, desc, 0.1);
//                        double finalScore = 0.75 * nameScore + 0.25 * descScore;
                        double finalScore = name.length() < 20
                                ? (0.25 * nameScore + 0.75 * descScore)
                                : (0.75 * nameScore + 0.25 * descScore);

                        return new ScoredProduct(product, finalScore);
                    })
                    .filter(sp -> sp.score > 0.1)
                    .sorted(Comparator.comparingDouble(ScoredProduct::score).reversed())
                    .limit(topK)
                    .map(ScoredProduct::product)
                    .toList();

            // Nếu không tìm được kết quả fuzzy nào, fallback
            if (fuzzyResults.isEmpty()) {
                return fallbackProducts(topK);
            }

            return fuzzyResults;
        } catch (Exception e) {
            e.printStackTrace();
            return fallbackProducts(topK); // nếu có lỗi vẫn fallback
        }
    }


    private double scoreByTokens(Set<String> tokens, String text, double boostIfContains) {
        String[] words = text.split("\\s+");

        return tokens.stream()
                .mapToDouble(token -> {
                    double bestScore = 0.0;

                    // So sánh từng token với từng từ trong đoạn text
                    for (String word : words) {
                        double sim = similarity.apply(token, word);
                        if (word.contains(token)) sim += boostIfContains;
                        bestScore = Math.max(bestScore, Math.min(sim, 1.0));
                    }

                    // Nếu token quá ngắn, thử so sánh với toàn bộ đoạn văn
                    if (token.length() <= 2) {
                        double simFull = similarity.apply(token, text);
                        if (text.contains(token)) simFull += boostIfContains;
                        bestScore = Math.max(bestScore, Math.min(simFull, 1.0));
                    }

                    return bestScore;
                })
                .average()
                .orElse(0.0);
    }

    private List<Product> fallbackProducts(int topK) {
        List<Product> all = productRepository.findAll();
        Collections.shuffle(all);
        return all.stream().limit(topK).toList();
    }

    private record ScoredProduct(Product product, double score) {}
}
