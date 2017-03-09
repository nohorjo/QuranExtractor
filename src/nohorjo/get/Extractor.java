package nohorjo.get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nohorjo.file.FileUtils;
import nohorjo.http.HttpOperation;

public class Extractor {

	private static int chapterNum = 1;
	private static int verseNum = 1;
	private static HttpOperation http = new HttpOperation();
	private static File quran = new File("quran.tsv");

	public static void main(String[] args) throws IOException {
		try (FileInputStream fis = new FileInputStream(quran)) {
			String[] last = FileUtils.readLastNLines(quran, 1).split("\t");
			chapterNum = Integer.parseInt(last[0]);
			verseNum = Integer.parseInt(last[1]) + 1;
			System.out.println("Resuming from chapter " + chapterNum + " verse " + verseNum);
		} catch (FileNotFoundException e) {
			quran.createNewFile();
			FileUtils.appendToFile(quran,
					"Chapter\tVerse\tSahih International\tDr. Ghali\tTransliteration\tMuhsin Khan\tYusuf Ali\tPickthall\tShakir\n");
		}
		try {
			while (true) {
				String html = http.doGet("https://quran.com/" + chapterNum + "/" + verseNum,
						"options=%7B%22content%22%3A%5B16%2C17%2C18%2C19%2C20%2C56%2C21%5D%7D");
				Matcher m = Pattern.compile("<small class=.*</small>").matcher(html);
				String verseClean = "";
				if (m.find()) {
					String verseDirty = m.group();

					for (String className : new String[] { "sahih-international", "dr.-ghali", "transliteration",
							"muhsin-khan", "yusuf-ali", "pickthall", "shakir" }) {
						String trans = "N/A";
						try {
							int i1 = verseDirty.indexOf('>', verseDirty.indexOf(className)) + 1;
							int i2 = verseDirty.indexOf('<', i1);
							trans = verseDirty.substring(i1, i2);
						} catch (Exception e) {
						}
						System.out.println(trans);
						verseClean += trans + "\t";
					}
					System.out.println();
					FileUtils.appendToFile(quran, chapterNum + "\t" + verseNum++ + "\t" + verseClean + "\n");
				} else {
					verseNum = 1;
					chapterNum++;
					System.out.println("-----~~~~~=====#####=====~~~~~-----");
					FileUtils.appendToFile(quran, chapterNum + "\t0\t\n");
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println();
		}
	}
}