package databaseTesting;

import java.io.IOException;
import java.util.List;

import Database.ReadCSV;

public class ReadCSVTest {
	public static void main(String args[]) throws IOException {
		ReadCSV pawnFile= new ReadCSV("PieceStartLocation.csv");
		
		while(true) {
			List<String> CSVData = pawnFile.next();
			if(CSVData == null) {
				break;
			}
			for(String x : CSVData) {
				System.out.print(x+" ");
			}
			System.out.println();
		}
		pawnFile.close();

		
	}
}
