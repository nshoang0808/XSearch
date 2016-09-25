import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/*
 * Class for reading input of judgement file
 * Return a list of queries in map form.
 */
public class JudgementFile {
	public static Map<Integer, Query> ReadJudgementFile(String fileName) {
		Map<Integer, Query> queries = new HashMap<Integer, Query>();
		
		BufferedReader fileInput;
		try {
			fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			do {
				String x = fileInput.readLine();
				if (x == null) break;
				
				//Get values from each line, containing the query id, doc id, and relevant information.
				String[] entries = x.split(" ");
				int queryId = Integer.parseInt(entries[0]);
				String docId = entries[2];
				int rel = Integer.parseInt(entries[3]);
				
				
				Query query;
				if (rel > 0) {
					if (!queries.containsKey(queryId)) {
						query = new Query(queryId);
						queries.put(queryId, query);
					} else query = queries.get(queryId);
					query.add(docId, rel);
				}
				
			} while(true);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return queries;
	}
}
