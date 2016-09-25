import java.util.Comparator;

/*
 * Document class contains information about a document
 * ->docId is must-have values
 * ->if document structured for ranking algorithm -> contains rank and score
 * ->if document structured for judgement information -> contains relevant values.
 */ 
public class Document implements Comparator<Document>{
	private String docId;
	private int rank = 0;
	private int rel = 0;
	private double score = 0;
	
	public Document() {}
	
	public Document(String docId) {
		this.docId = docId;
	}
	
	/*
	 * Document constructor for the Ranking Algorithm, containing ranking and score
	 */
	public Document(String docId, int rank, double score) {
		this.docId = docId;
		this.rank = rank;
		this.score = score;
	}
	
	/*
	 * Document constructor for the judgement result, with relevant information
	 */
	public Document(String docId, int rel) {
		this.docId = docId;
		this.rel = rel;
	}
	
	public int getRank() {
		return rank;
	}
	
	public int getRelevant() {
		return rel;
	}

	@Override
	public int compare(Document arg0, Document arg1) {
		if (arg0.rel>arg1.rel) return -1;
		if (arg0.rel<arg1.rel) return 1;
		return 0;
	}
}
