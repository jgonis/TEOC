package textCompare;


/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A text file comparer. Receives two text file names as command line arguments
 * and compares them line by line (ignoring spaces). An error is displayed in
 * case of a comparison failure. A "success" message is displayed in case of
 * successful comparison.
 */
public class TextComparer {

	/**
	 * The command line Text Comparer program.
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java TextComparer <file name> <file name>");
			return;
		}

		try (BufferedReader reader1 = new BufferedReader(new FileReader(args[0]));
				BufferedReader reader2 = new BufferedReader(new FileReader(args[1]))) {
			if (!compareText(reader1, reader2))
				System.exit(-1);

		} catch (IOException ioe) {
			System.err.println("IO error while trying to deal with files");
			return;
		}
		
		System.out.println("Comparison ended successfully");
	}

	static boolean compareText(BufferedReader source1, BufferedReader source2) throws IOException {
		if(source1 == null || source2 == null)
			return false;
		
		String line1, line2;
		int count = 0;

		while ((line1 = source1.readLine()) != null) {

			line1 = line1.replaceAll("\\s", "");
			line2 = source2.readLine();

			if (line2 == null) {
				System.out.println("Second file is shorter (only " + count + " lines)");
				System.exit(-1);
			} else {
				line2 = line2.replaceAll("\\s", "");
				if (!line1.equals(line2)) {
					System.out.println("Comparison failure in line " + count + ":");
					System.out.println(line1);
					System.out.println(line2);
					return false;
				}
			}
			count++;
		}

		if (source2.readLine() != null) {
			System.out.println("First file is shorter (only " + count + " lines)");
			return false;
		}

		return true;
	}
}
