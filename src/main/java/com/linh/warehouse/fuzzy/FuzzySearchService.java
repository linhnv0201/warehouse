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
            Map.entry("ao", List.of("áo", "shirt", "top", "tee", "tshirt", "áo thun", "áo phông", "áo ngắn tay", "áo tay dài", "áo in hình", "áo cotton")),
            Map.entry("quan", List.of("quần", "pants", "trousers", "jeans", "short", "quần vải", "quần dài", "quần tây", "quần ống rộng", "quần ống suông")),
            Map.entry("vay", List.of("váy", "đầm", "dress", "gown", "skirt", "váy liền", "váy công sở", "váy dự tiệc", "váy suông", "váy xòe", "váy ôm", "váy yếm")),
            Map.entry("short", List.of("quần short", "short", "quần đùi", "quần ngắn", "short jeans",
                                                "quần lưng cao", "quần lưng thấp", "quần short thể thao", "quần short nữ")),
            Map.entry("hoodie", List.of("hoodie", "áo hoodie", "áo khoác mũ", "áo nỉ có mũ", "áo trùm đầu", "hoodie nỉ", "hoodie oversize")),
            Map.entry("bo", List.of("bò", "jean", "denim", "quần bò", "áo bò", "váy bò", "vải denim", "skinny jean", "baggy jeans", "jeans trắng", "quần jean", "short jean")),
            Map.entry("somi", List.of("áo sơ mi", "sơ mi", "shirt", "áo sơ mi cổ tàu", "áo sơ mi tay phồng", "sơ mi denim", "áo sơ mi trắng")),
            Map.entry("thun", List.of("thun", "cotton", "co giãn", "áo thun", "áo phông", "t-shirt", "áo cổ tròn", "áo thun in hình")),
            Map.entry("the thao", List.of("thể thao", "gym", "jogger", "legging", "tanktop", "tập gym", "tập thể dục", "chạy bộ", "training")),
            Map.entry("ni", List.of("nỉ", "ni", "fleece", "warm", "giữ ấm", "chống lạnh", "áo khoác nỉ")),
            Map.entry("kaki", List.of("kaki", "khaki", "quần kaki", "áo kaki", "chất kaki")),
            Map.entry("len", List.of("len", "wool", "len tăm", "áo len", "cardigan", "len mềm", "áo giữ ấm", "sweater")),
            Map.entry("khoac", List.of("áo khoác", "jacket", "bomber", "khoác gió", "windbreaker", "khoác dạ", "khoác 2 lớp", "khoác hoodie")),
            Map.entry("mu", List.of("mũ", "nón", "hat", "cap", "mũ lưỡi trai", "nón rộng vành", "nón bucket", "mũ len")),
            Map.entry("giay", List.of("giày", "giay", "shoes", "sneaker", "boot", "giày thể thao", "giày cao gót", "giày da", "giày lười")),
            Map.entry("dep", List.of("dép", "dep", "slipper", "sandals", "dép lê", "dép quai hậu", "dép bản to")),
            Map.entry("khau", List.of("khẩu trang", "mask", "face mask", "che mặt", "khau trang")),
            Map.entry("trang", List.of("trắng", "white", "màu trắng")),
            Map.entry("den", List.of("đen", "black", "màu đen")),
            Map.entry("xanh", List.of("xanh", "blue", "green", "màu xanh")),
            Map.entry("do", List.of("đỏ", "do", "red", "màu đỏ")),
            Map.entry("vang", List.of("vàng", "vang", "yellow", "màu vàng")),
            Map.entry("nu", List.of("nữ", "nữ tính", "women", "lady", "girl")),
            Map.entry("nam", List.of("nam", "men", "man", "boy", "nam tính")),

            Map.entry("cotton", List.of("cotton", "vải cotton", "chất cotton", "co giãn", "thoáng mát", "vải thun")),
            Map.entry("satin", List.of("satin", "lụa", "bóng", "mượt", "vải mềm", "dự tiệc")),
            Map.entry("linen", List.of("linen", "vải lanh", "nhẹ", "mát", "vải tự nhiên", "chống nóng")),

            Map.entry("form rong", List.of("form rộng", "oversize", "dáng rộng", "unisex", "thùng thình")),
            Map.entry("form om", List.of("form ôm", "slim fit", "body fit", "dáng ôm sát", "tight")),
            Map.entry("form suong", List.of("form suông", "dáng suông", "regular fit", "dáng thẳng")),

            Map.entry("con ho", List.of("con hổ", "tiger", "in hổ", "hoạ tiết con hổ")),
            Map.entry("hoa", List.of("hoa", "hoạ tiết hoa", "hoa nhí", "hoa văn")),
            Map.entry("trơn", List.of("trơn", "không họa tiết", "basic", "đơn giản")),

            Map.entry("maxi", List.of("maxi", "váy dài", "dài chấm gót", "váy maxi", "dáng dài")),
            Map.entry("body", List.of("body", "ôm sát", "váy ôm", "gợi cảm")),
            Map.entry("baby", List.of("babydoll", "cổ sen", "dáng xòe nhẹ", "dễ thương", "form baby doll")),

            Map.entry("polo", List.of("polo", "áo cổ bẻ", "áo có cổ", "áo polo")),
            Map.entry("tanktop", List.of("tanktop", "áo ba lỗ", "không tay", "tập gym", "chạy bộ")),
            Map.entry("jogger", List.of("jogger", "quần jogger", "ống bo", "quần thể thao", "bo gấu")),
            Map.entry("legging", List.of("legging", "quần ôm sát", "quần tập gym", "quần thể thao nữ")),
            Map.entry("bra", List.of("bra", "bralette", "áo lót", "áo trong", "không gọng")),

            Map.entry("con meo", List.of("con mèo", "mèo", "cat", "in mèo", "hình mèo")),

            Map.entry("phong cach", List.of("phong cách", "style", "casual", "công sở", "dự tiệc", "trẻ trung", "cổ điển", "năng động"))

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
