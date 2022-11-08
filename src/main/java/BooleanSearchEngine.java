import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> pageEntryMap = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {

            var doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                var page = doc.getPage(i + 1);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    List<PageEntry> pageEntryList = pageEntryMap.get(entry.getKey());
                    if (pageEntryMap.get(entry.getKey()) == null)  {
                        pageEntryList = new ArrayList<>();
                    }
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i + 1, entry.getValue());
                    pageEntryList.add(pageEntry);
                    pageEntryMap.put(entry.getKey(), pageEntryList);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        Collections.sort(pageEntryMap.get(word.toLowerCase()));
        Collections.reverse(pageEntryMap.get(word.toLowerCase()));
        return pageEntryMap.get(word.toLowerCase());
    }
}
