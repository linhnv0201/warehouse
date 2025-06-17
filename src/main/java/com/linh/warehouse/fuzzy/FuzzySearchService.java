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

    private final ProductRepository productRepo;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public FuzzySearchService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    private static final Map<String, List<String>> SYNONYMS = Map.ofEntries(
            Map.entry("ao", List.of("ao", "áo", "shirt", "tee", "tshirt", "áo thun", "áo phông")),
            Map.entry("quan", List.of("quan", "quần", "pants", "jeans", "trousers", "short")),
            Map.entry("dam", List.of("dam", "váy", "đầm", "dress", "gown")),
            Map.entry("vay", List.of("vay", "váy", "skirt")),
            Map.entry("bo", List.of("bo", "bò", "jean", "denim", "quần bò")),
            Map.entry("thun", List.of("thun", "cotton", "co giãn", "áo thun")),

            Map.entry("mu", List.of("mu", "mũ", "nón", "hat", "cap")),
            Map.entry("giay", List.of("giay", "giày", "sneaker", "shoes", "boot")),
            Map.entry("dep", List.of("dep", "dép", "sandals", "slipper")),
            Map.entry("khau", List.of("khau", "khẩu trang", "mask")),

            Map.entry("ni", List.of("nỉ", "ni", "fleece", "warm")),
            Map.entry("kaki", List.of("kaki", "khaki")),
            Map.entry("len", List.of("len", "wool", "len tăm")),
            Map.entry("soi", List.of("sợi", "soi", "fiber")),

            Map.entry("trang", List.of("trắng", "trang", "white")),
            Map.entry("den", List.of("đen", "den", "black")),
            Map.entry("xanh", List.of("xanh", "blue", "green"))
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

            List<Product> fuzzyResults = productRepo.findAll().stream()
                    .map(product -> {
                        String name = removeVietnameseDiacritics(product.getName().toLowerCase());
                        String desc = removeVietnameseDiacritics(
                                Optional.ofNullable(product.getDescription()).orElse("").toLowerCase()
                        );

                        double nameScore = scoreByTokens(expandedTokens, name, 0.2);
                        double descScore = scoreByTokens(expandedTokens, desc, 0.1);
                        double finalScore = 0.75 * nameScore + 0.25 * descScore;

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
        List<Product> all = productRepo.findAll();
        Collections.shuffle(all);
        return all.stream().limit(topK).toList();
    }

    private record ScoredProduct(Product product, double score) {}
}
