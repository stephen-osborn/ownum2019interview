
/**Ownum Interview Code Sample
 * Stephen Osborn
 * 11/20/19
 * ASSUMPTIONS:
 *  - The desired text is located in the same directory as this file and is named "passage.txt"
 *  - A passage never contains consecutive spaces
 *  - Word counting is case-insensitive; results will be presented in lowercase
 *  - A sentence ends with a period UNLESS it is trailing text at the end of a passage without a period
 *  - Time efficiency is important; this solution should run within O(n) time, where n = word count
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

class WordCount {
    public static void main(String[] args) throws IOException {
        String fileName = "passage.txt";
        BufferedReader passageReader;
        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
        List<String> sentences = new ArrayList<>();

        try {
            passageReader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.printf("Could not read file: %s%n", fileName);
            return;
        }

        // Read the first line of the passage
        String line = passageReader.readLine();
        StringBuilder sentenceBuilder = new StringBuilder();
        while (line != null) {
            line = line.trim();

            // Count the words in this line
            for (String word : line.split(" ")) {
                // Add the current word to the current sentence.
                sentenceBuilder.append(word);
                if (word.endsWith(".")) {
                    // We have reached the end of a sentence.
                    sentences.add(sentenceBuilder.toString());
                    sentenceBuilder = new StringBuilder();
                } else {
                    // We are mid-sentence; add a space between words.
                    sentenceBuilder.append(' ');
                }

                // Make the word lowercase and strip anything that isn't a letter (i.e
                // punctuation)
                word = word.toLowerCase().replaceAll("[^a-z]", "");

                if (wordCounts.containsKey(word)) {
                    // We have seen this word before. Increment its counter.
                    wordCounts.put(word, wordCounts.get(word) + 1);
                } else {
                    // This is the first occurence of this word. Initialize its counter to 1.
                    wordCounts.put(word, 1);
                }
            }

            // Read the next line, which could be null
            line = passageReader.readLine();
        }
        // We have read all of the passage; close the file stream
        passageReader.close();
        // Add any trailing text as a sentence
        if (sentenceBuilder.length() > 0) {
            sentences.add(sentenceBuilder.toString());
        }

        // Find the desired output metrics.
        // In order to keep this solution's runtime within O(n), I will not sort the
        // whole wordCounts set.
        int totalCount = 0;
        List<Entry<String, Integer>> topTen = new ArrayList<Entry<String, Integer>>();
        for (Entry<String, Integer> wordCount : wordCounts.entrySet()) {
            // Increment the total word count
            totalCount += wordCount.getValue();

            // Load the first ten words into topTen
            if (topTen.size() < 10) {
                topTen.add(wordCount);
                // Since topTen is a fixed length (<10), we can sort it and remain within O(n)
                Collections.sort(topTen, new SortWordCounts());
            } else {
                // For the remaining words, determine whether they should be in the topTen
                if (wordCount.getValue() > topTen.get(9).getValue()) {
                    topTen.set(9, wordCount);
                    // Since topTen is a fixed length (<10), we can sort it and remain within O(n)
                    Collections.sort(topTen, new SortWordCounts());
                }
            }
        }
        // Find the last sentence that contains the top word.
        // From what I could find online, Java lacks a querying library similar to C#'s
        // LINQ. Instead, we will iterate manually backwards through the sentences list
        String lastSentence = null;
        for (int i = sentences.size() - 1; i >= 0 && lastSentence == null; i--) {
            if (sentences.get(i).toLowerCase().indexOf(topTen.get(0).getKey()) >= 0) {
                lastSentence = sentences.get(i);
            }
        }

        // Print output metrics
        System.out.printf("Total word count:%n    %d words%n", totalCount);
        System.out.println("Top 10 words counted:");
        for (Entry<String, Integer> wordCount : topTen) {
            System.out.printf("    %d - %s%n", wordCount.getValue(), wordCount.getKey());
        }
        System.out.println("Last sentence using the top word:");
        System.out.printf("    \"%s\"%n", lastSentence);
    }
}

// Custom comparator for comparing the entries of the wordCounts hashmap
class SortWordCounts implements Comparator<Entry<String, Integer>> {
    public int compare(Entry<String, Integer> entryA, Entry<String, Integer> entryB) {
        return entryB.getValue() - entryA.getValue();
    }
}