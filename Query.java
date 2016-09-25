import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Query class contains a list of Document that is RELEVANT to this query
 */
public class Query {
	private List<Document> docs = new ArrayList<Document>();
	private int queryId;
	private int relDocs = 0;
	
	//Set of document ID to quick check for relevant documents.
	public Set<String> docSet = new HashSet<String>();
	
	public Query(int queryId) {
		this.queryId = queryId;
	}
	public Query(List<Document> documents) {
		this.docs = documents;
	}
	
	/*
	 * Returns the document of index i in the list
	 */
	public Document get(int i) {
		return docs.get(i);
	}
	
	/*
	 * Returns this queryId (number).
	 */
	public int getId() {
		return queryId;
	}
	
	/*
	 * Return true if this query is relevant to the "doc" (because it contains in the list).
	 */
	public boolean contains(String doc) {
		return docSet.contains(doc);
	}
	
	/*
	 * Returns list of relevant documents.
	 */
	public List<Document> getListDocs() {
		return docs;
	}
	
	/*
	 * Method add() add a document into the list.
	 * NOTE: This "add" actually belongs to the query result of Ranking Algorithms,
	 * and therefore does not contain relevant information, only its rank and score.
	 */
	public void add(String doc, int rank, double score) {
		docs.add(new Document(doc, rank, score));
		docSet.add(doc);
	}
	
	/*
	 * Method add() add a document into the list.
	 * NOTE: This "add" actually belongs to the judgement file
	 * and therefore does not contain ranking information, only its relevant information.
	 */
	public void add(String doc, int rel) {
		docs.add(new Document(doc, rel));
		docSet.add(doc);
		if (rel>0) relDocs++;
	}
	
	/*
	 * Returns total number of relevant documents.
	 */
	public int numRelDocs() {
		if (relDocs != 0) return relDocs;			//If it is query of judgement file, only when relevant info >0
		return docs.size();							//else return the size of the query.
	}
}
