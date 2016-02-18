import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
	
	//String contains all acceptable character in a word.
	final static String characters = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	//String list of vowel:
	final static String vowels = "aeiou";
	static Set<String> stopwords = new HashSet<String>();
	
/*******************************************/	
	/*
	 * Check if the given character is a vowel
	 */
	static boolean isVowel(char x) {
		return vowels.contains(Character.toString(x));
	}
	
	/*
	 * Check if the given word is a stopword.
	 */
	static boolean isStopword(String w) {
		return stopwords.contains(w);
	}
	
	/*
	 * Check if the given character is alphabetic.
	 */
	static boolean isInAlphabet(char x) {
		return characters.contains(Character.toString(x));
	}
	
	/*
	 * Tokenize given word. 
	 */
	static ArrayList<String> split(String s) {
		ArrayList<String> result = new ArrayList<String>();			//returning ArrayList<String> after Tokenizing.
		String next = "";
		for(int i=0; i<s.length(); i++) {
			char x = s.charAt(i);
			if (isInAlphabet(x)) {									//when x is an acceptable character.
				if (Character.isUpperCase(x)) {
					if((i==0) || (i>0 && s.charAt(i-1) != '.')) {	//if x is not an abbreviations.
						next = next+Character.toLowerCase(x);
					} else {										//if x is uppercase and after a period.
						if (i>1 && Character.isUpperCase(s.charAt(i-2))) {
							next = next+Character.toLowerCase(x);
						} else {
							if (next != "") result.add(next);
							next = Character.toLowerCase(x) + "";
						}
					}
				} else if (i>0 && s.charAt(i-1) == '.') {				//when x is lowercase and after a period
					if (next != "") result.add(next);								//then add the previous word that we are checking if it is an abbreviation.
					next = "" + x;
				} else {
					next = next + x;
				}
			} else if (x != '.'){
				if (next != "") result.add(next);
				next = "";
			}
		}
		if (next != "") result.add(next);
		return result;
	}
	
	/*
	 * PART A: Tokenizing in 3 steps.
	 */
	static ArrayList<String> tokenizer(String words) {
		/*
		 * PART A.1: Retrieve a list of words that follow the split rule.
		 */
		ArrayList<String> listWord = split(words);
		
		/*
		 * PART A.2: Remove all stopping words.
		 */
		int id = 0;
		while(id < listWord.size()) {	
			if (isStopword(listWord.get(id))) {
				listWord.remove(id);
			} else id++;
		}

		/*
		 * PART A.3: First 2 step of Porter stemmer.
		 */
		for (int i=0; i<listWord.size(); i++) {
			String x = listWord.get(i);
			if (x.endsWith("ss") || x.endsWith("us")) continue;
			if (x.endsWith("sses")) {													//the word ends with "sses"
				listWord.set(i, x.substring(0, x.length()-4) + "ss");
				
			} else if (x.endsWith("ies") || x.endsWith("ied")){
				if (x.length() == 4) listWord.set(i, x.substring(0, 3));
				else listWord.set(i, x.substring(0, x.length()-2));
				
			} else if (x.endsWith("s") && x.length()>2) {								//the word ends with 's'
				if (!isVowel(x.charAt(x.length()-2))) {									// if the next-to-last character of the word is not vowel then
					listWord.set(i, x.substring(0, x.length()-1));						//remove last 's'
				}
				
			} else if (x.endsWith("eed") || x.endsWith("eedly")) {						//the word ends with "eed" or "eedly"
				if (x.endsWith("eed")) x = x + "ly";									
				x = x.substring(0, x.length()-5);										//remove either "eed" or "eedly" part in x.
				
				//if the next chars are non-vowel following a vowel:
				if (x.length()>1 && !isVowel(x.charAt(x.length()-1)) && isVowel(x.charAt(x.length()-2))) {
					boolean firstNonVowel = true;										//check if this is the first non-vowel
					for(int j=0; j<x.length()-2; j++) {
						if (!isVowel(x.charAt(j))) {
							firstNonVowel = false;
							break;
						}
					}
					if (firstNonVowel) listWord.set(i, x + "ee");						//if it is the first non-vowel, then replace "ee".
				}
			} else if(x.endsWith("ed") || x.endsWith("edly") || x.endsWith("ing") || x.endsWith("ingly")) {
				if (x.endsWith("ly")) x = x.substring(0, x.length()-2);
				if (x.endsWith("ed")) x = x.substring(0, x.length()-2);
				else x = x.substring(0, x.length()-3);
				
				//if after remove, the word and at "at", "bl", or "iz"
				if (x.endsWith("at") || x.endsWith("bl") || x.endsWith("iz")) {
					x = x + "e";														//add "e" at the end.
				} else if (x.length()>2 && x.charAt(x.length()-1) == x.charAt(x.length()-2)) {
					char t = x.charAt(x.length()-1);
					if (t != 'l' && t != 's' && t != 'z') x = x.substring(0, x.length()-1);
				} else if (x.length() <= 3) x = x + "e";
			}
		}
		return listWord;
	}
	
	/*
	 * PART B: Tokenizing and implement 
	 */
	static List<Map.Entry<String, Integer>> mostFrequentWords(ArrayList<String> listWord) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(int i=0; i<listWord.size(); i++) {
			String x = listWord.get(i);
			if (!map.containsKey(x)){
				map.put(x, 1);
			} else map.replace(x, map.get(x)+1);
		}
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o2.getValue()-o1.getValue();
			}
		});
		return list.subList(0, 200);
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner in1 = new Scanner(new File("p2-input-part-A.txt"));				//Read input part A
		Scanner in2 = new Scanner(new File("p2-input-part-B.txt"));
		Scanner stopwordDatabase = new Scanner(new File("inquery"));			//Read stopword list
		FileWriter out1 = new FileWriter("tokenized.txt");
		FileWriter out2 = new FileWriter("terms.txt");
		
		//Get the set of all stopwords.
		while (stopwordDatabase.hasNext()) {
			stopwords.add(stopwordDatabase.next());
		}
		String words_A = "";
		while (in1.hasNextLine()) {
			words_A = words_A + in1.nextLine();
		}
		ArrayList<String> listWord_A = tokenizer(words_A);
		for (String x: listWord_A) out1.write(x+"\n");
		
		/*
		 * PART B:
		 */
		String words_B = "";
		while (in2.hasNextLine()) {
			words_B = words_B + in2.nextLine();
		}
		ArrayList<String> listWord_B = tokenizer(words_B);
		List<Map.Entry<String, Integer>> mostFrequentList = mostFrequentWords(listWord_B);
		for(Map.Entry<String, Integer> x: mostFrequentList) out2.write(x + "\n");
		
		in1.close();
		in2.close();
		out1.close();
		out2.close();
		stopwordDatabase.close();
	}

}
