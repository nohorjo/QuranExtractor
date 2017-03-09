package nohorjo.get;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nohorjo.doc.TSVSpreadsheet;

public class CheckTSV {
	private static File quranFile = new File("quran.tsv");
	private static TSVSpreadsheet quran;

	public static void main(String[] args) throws IOException {
		quran = new TSVSpreadsheet(quranFile.toPath());

		for (int i = 0; i < quran.size(); i++) {
			System.out.println("Checking " + i);
			List<String> record = quran.getRecord(i);
			if (!record.get(1).equals("")) {
				for (int j = 2; j < record.size(); j++) {
					if (record.get(j).equals("")) {
						throw new NullPointerException("EMPTY RECORD: " + i + " " + j);
					}
				}
			}
		}
	}
}
