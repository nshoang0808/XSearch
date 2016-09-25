import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RankingAlgo {
	public static Map<Integer, Query> ReadRankingFile(String fileName) {
		Map<Integer, Query> queries = new HashMap<Integer, Query>();
		BufferedReader fileInput;
		try {
			fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			do {
				//Read the whole line and break it down later.
				String x = fileInput.readLine();
				if (x == null) break;
				
				//Get values from each line, containing the query id, doc id, rank, and score
				String[] entries = x.split(" ");
				int queryId = Integer.parseInt(entries[0]);
				String docId = entries[2];
				int rank = Integer.parseInt(entries[3]);
				double score = Double.parseDouble(entries[4]);
				
				//If in this query, the document is irrelevant, move to next document.
				if (!Main.isRelevant(queryId, docId)) continue;
				
				Query query;
				if (!queries.containsKey(queryId)) {
					query = new Query(queryId);
					queries.put(queryId, query);
				} else query = queries.get(queryId);
				
				//else add it to the query document list.
				query.add(docId, rank, score);
				
			} while(true);

			fileInput.close();
			
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
