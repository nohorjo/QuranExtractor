package nohorjo.search;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

import nohorjo.doc.TSVSpreadsheet;

public class SearchTSV {
	private static File quranFile = new File("quran.tsv");
	private static TSVSpreadsheet quran;
	private static List<String> trans = Arrays
			.asList("1 - Sahih International\t2 - Dr. Ghali\t3 - Transliteration\t4 - Muhsin Khan\t5 - Yusuf Ali\t6 - Pickthall\t7 - Shakir\n"
					.split("\t"));

	public static void main(String[] args) throws IOException {
		quran = new TSVSpreadsheet(quranFile.toPath());
		try (Scanner sc = new Scanner(System.in)) {
			int iTrans = -1;
			while (true) {
				System.out.println("Enter desired translation:");
				System.out.println(trans);
				sc.hasNextLine();
				try {
					iTrans = Integer.parseInt(sc.nextLine());
					if (iTrans < 1 || iTrans > 7) {
						throw new InvalidParameterException();
					}
					break;
				} catch (NumberFormatException | InvalidParameterException e) {
					System.err.println("Enter a valid number!");
				}
			}
			String message = "Enter chapter:verse, search term or regex:";
			System.out.println(message);
			while (sc.hasNextLine()) {
				String search = sc.nextLine();
				if (search.matches("\\d*:\\d*")) {
					String[] chapterVerse = search.split(":");
					printVerse(chapterVerse[0], chapterVerse[1], iTrans);

				} else {
					System.out.println("Searching for " + search);
					searchFor(search, iTrans);
				}
				System.out.println(message);
			}
		}
	}

	private static void printVerse(String chapter, String verse, int iTrans) {
		for (List<String> record : quran) {
			if (record.get(0).equals(chapter) && record.get(1).equals(verse)) {
				System.out.println(record.get(iTrans + 1));
				return;
			}
		}
		System.err.println("Verse not found!");
	}

	private static void searchFor(String search, int iTrans) {
		int found = 0;
		search = search.toLowerCase();
		for (List<String> record : quran) {
			boolean matches = false;

			try {
				String cell = record.get(iTrans + 1).toLowerCase();
				try {
					if (cell.matches(".*\\b" + search + "\\b.*")) {
						matches = true;
					}
				} catch (PatternSyntaxException e) {
				}
				try {
					if (!matches && cell.matches(search)) {
						matches = true;
					}
				} catch (PatternSyntaxException e) {
				}
				if (matches) {
					found++;
					System.out.println(record.get(0) + "\t" + record.get(1) + "\t" + cell);
				}
			} catch (IndexOutOfBoundsException e) {
			}
		}
		System.out.println("Done! Found " + found + " entries\n");
	}
}
