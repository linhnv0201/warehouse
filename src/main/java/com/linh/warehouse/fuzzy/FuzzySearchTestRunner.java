package com.linh.warehouse.fuzzy;

import com.linh.warehouse.entity.Product;
import com.linh.warehouse.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FuzzySearchTestRunner {

    private final FuzzySearchService fuzzySearchService;
    private final ProductRepository productRepository;

    public FuzzySearchTestRunner(FuzzySearchService fuzzySearchService, ProductRepository productRepository) {
        this.fuzzySearchService = fuzzySearchService;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void run() {
        List<TestCase> testCases = List.of(
                // ✅ Chính xác / mô tả rõ
                new TestCase("con hổ", List.of("Q0005", "B0002", "Q0001", "A0001")),
                new TestCase("váy công sở", List.of("V0001", "V0007", "V0004", "V0013")),
                new TestCase("quần bò rách", List.of("Q0003", "Q0006")),
                new TestCase("hoodie nỉ", List.of("A0008", "A0014")),
                new TestCase("váy hoa", List.of("V0003", "V0009")),
                new TestCase("áo thể thao nữ", List.of("A0013", "B0003")),
                new TestCase("quần short nữ", List.of("Q0010", "Q0018")),
                new TestCase("tanktop nam", List.of("A0007", "A0020")),
                new TestCase("cardigan", List.of("A0019")),
                new TestCase("áo sơ mi trắng nữ", List.of("A0017")),

                // ✅ Sai chính tả / viết không dấu
                new TestCase("vay cong so", List.of("V0001", "V0007", "V0004", "V0013")),
                new TestCase("quan bo rach", List.of("Q0003", "Q0006")),
                new TestCase("hoodie ni", List.of("A0008", "A0014")),
                new TestCase("ao the thao nu", List.of("A0013", "B0003")),
                new TestCase("ao so mi trang nu", List.of("A0017")),
                new TestCase("con meo", List.of("A0002")), // in hình con mèo

                // ✅ Gần đúng (Jaro-Winkler score kiểm thử)
                new TestCase("hoodi ni", List.of("A0008", "A0014")),
                new TestCase("quần bò rac", List.of("Q0003", "Q0006")),
                new TestCase("váy cong sợ", List.of("V0001", "V0004", "V0007", "V0013")),
                new TestCase("ao thê thao", List.of("A0013", "B0003")),
                new TestCase("vay meow", List.of("A0002")), // fuzzy match với "mèo"

                // ✅ Synonym tiếng Anh
                new TestCase("jogger", List.of("Q0008", "Q0016")),
                new TestCase("sweater", List.of("A0018", "A0019")),
                new TestCase("t-shirt", List.of("A0001", "A0002", "A0004", "A0012")),
                new TestCase("jeans", List.of("Q0001", "Q0003", "Q0015", "Q0019")),
                new TestCase("dress", List.of("V0002", "V0005", "V0010", "V0011")),
                new TestCase("white shirt", List.of("A0017")),
                new TestCase("somi", List.of("A0009", "A0010", "A0015", "A0017")),

                // ✅ Query ngắn / phổ thông (gợi ý gợi ý sản phẩm phổ biến)
                new TestCase("áo", List.of("A0001", "A0002", "A0004", "A0006", "A0009")),
                new TestCase("quần", List.of("Q0001", "Q0003", "Q0012", "Q0014", "Q0015")),
                new TestCase("váy", List.of("V0001", "V0002", "V0003", "V0004")),
                new TestCase("đen", List.of("Q0002", "Q0004", "V0001", "V0010")),
                new TestCase("cotton", List.of("A0001", "A0003", "A0012", "B0007")),
                new TestCase("nỉ", List.of("A0008", "A0014", "Q0016")),
                new TestCase("len", List.of("A0018", "A0019")),

                // ✅ Theo đặc điểm mô tả
                new TestCase("cổ lọ", List.of("A0018")),
                new TestCase("ống bo", List.of("Q0008", "Q0016")),
                new TestCase("in hình con mèo", List.of("A0002")),
                new TestCase("in hình con hổ", List.of("A0001", "Q0001", "Q0005", "B0002")),
                new TestCase("co giãn", List.of("A0012", "A0013", "B0003", "Q0011")),
                new TestCase("tay phồng", List.of("A0010", "A0017", "V0007")),

                // ✅ Một vài mix lạ
                new TestCase("jacket nữ cotton", List.of("A0011", "A0016")),
                new TestCase("váy trắng công sở", List.of("V0001", "V0013", "V0004")),
                new TestCase("quần tây slim fit", List.of("Q0004", "Q0009", "Q0013")),
                new TestCase("vay maxy", List.of("V0003")),
                new TestCase("quần baggy", List.of("Q0019")),
                new TestCase("bò xanh", List.of("Q0001", "Q0015")),
                new TestCase("kaki nam", List.of("Q0007", "Q0012", "Q0014"))
        );



        int passed = 0;
        int total = testCases.size();

        StringBuilder sb = new StringBuilder();
        sb.append("\n--------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-22s | %-30s | %-30s | %-5s\n", "#", "Query", "Expected", "Result", "✔"));
        sb.append("--------------------------------------------------------------\n");

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            List<Product> result = fuzzySearchService.search(tc.query(), 5);
            List<String> foundCodes = result.stream().map(Product::getCode).toList();

            Set<String> expectedSet = new HashSet<>(tc.expected());
            Set<String> foundSet = new HashSet<>(foundCodes);
            boolean match = !Collections.disjoint(expectedSet, foundSet);

            if (match) passed++;

            sb.append(String.format("%-4d | %-22s | %-30s | %-30s | %-5s\n",
                    i + 1,
                    tc.query(),
                    String.join(", ", tc.expected()),
                    String.join(", ", foundCodes),
                    match ? "✅" : "❌"
            ));
        }

        sb.append("--------------------------------------------------------------\n");
        sb.append(String.format("🎯 Accuracy: %d/%d = %.2f%%\n", passed, total, passed * 100.0 / total));
        System.out.println(sb);
    }

    private record TestCase(String query, List<String> expected) {
    }
}
