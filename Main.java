import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
	
	/*A map stores the relevance between a query and a document designated by the judgement file*/
	private static Map<Integer, Query> judgeQueries;
	private static PrintWriter pw;
	
	/*
	 * Calculate the mean score for NDCG@20, P@5, P@10, Recall@10, F1@10, AP of each method
	 * Then print it out to the output file
	 */
	public static void printMeanScore(String methodName, Map<Integer, Query> method) {
		double meanNDCG = 0;
		double meanP5 = 0;
		double meanP10 = 0;
		double meanRecall = 0;
		double meanF1 = 0;
		double meanAP = 0;
		
		for(Map.Entry<Integer, Query> x : method.entrySet()) {
			meanNDCG += NDCG(20, x.getValue());
			meanP5 += precision(5, x.getValue());
			meanP10 += precision(10, x.getValue());
			meanRecall += recall(10, x.getValue());
			meanF1 += f1(10, x.getValue());
			meanAP += averPrecision(x.getValue());
		}
		
		pw.printf("%s NDCG@20 %.4f\n", methodName, meanNDCG/method.size());
		pw.printf("%s P@5 %.4f\n", methodName, meanP5/method.size());
		pw.printf("%s P@10 %.4f\n", methodName, meanP10/method.size());
		pw.printf("%s Recall@10 %.4f\n", methodName, meanRecall/method.size());
		pw.printf("%s F1@10 %.4f\n", methodName, meanF1/method.size());
		pw.printf("%s AP %.4f\n", methodName, meanAP/method.size());
	}
	
	/*
	 * Calculating IDCG@N for a query with N = pos.
	 */
	public static double IDCG (int pos, Query query) {
		
		List<Document> docs = judgeQueries.get(query.getId()).getListDocs();
		Collections.sort(docs, new Document());
		
		double result = 0;
		for(int i=0; i<query.numRelDocs(); i++) {			//for all relevant documents in query:
			double relevant = (double) docs.get(i).getRelevant();
			
			//If this is the first query then just add the relevant values to result
			if (i == 0) result += relevant;
			
			//Else use logarithm formula.
			else result += relevant/(Math.log(i+1)/Math.log(2));
		}
		return result;
		
	}
	
	/*
	 * Calculating NCDG@N for a query with N = pos.
	 */
	public static double NDCG (int pos, Query query) {
		double idcg = IDCG(pos, query);
		if (idcg == 0.0) return 0;							//IDCG is 0 only when there is no retrieved
															//documents or relevant documents
		double result = 0;
		for(int i=0; i<query.numRelDocs(); i++) {			//for all relevant documents in query:
			double relevant = (double) judgeQueries.get(query.getId()).get(i).getRelevant();
			
			//if the first query answer is a relevant document:
			if (query.get(i).getRank() == 1) result += relevant;
			
			//else use logarithm formula.
			else result += relevant/(Math.log(query.get(i).getRank())/Math.log(2));
		}
		return result/idcg;
	}
	
	/*
	 * Calculating average precision for a query.
	 */
	public static double averPrecision(Query query) {
		double sum = 0;
		for(int i=0; i<query.numRelDocs(); i++) {			//for all relevant documents in query:
			sum += (double)(i+1)/query.get(i).getRank();  	//p@N = (relevant documents retrieved to N = i+1)/N
		}
		return (double)sum/query.numRelDocs();
	}
	
	/*
	 * Calculating the F-harmonic (F1@N) for a query with N = pos.
	 */
	public static double f1(int pos, Query query) {
		double p = precision(pos, query);
		double r = recall(pos, query);
		
		if (r+p == 0) return 0;
		return(2.0*r*p/(r+p));							//Harmonic mean.
	}
	
	/*
	 * Calculating precision@N for a query with N = pos.
	 */
	public static double precision(int pos, Query query) {
		int x = 0; //x is the number of relevant documents up to position pos
		while (x < query.numRelDocs() && query.get(x).getRank() <= pos) x++;
		return (double)x/(double)pos;
	}
	
	/*
	 * Calculating recall@N for a query with N = pos.
	 */
	public static double recall(int pos, Query query) {
		int x = 0;
		while (x < query.numRelDocs() && query.get(x).getRank() <= pos) x++;
		return (double)x/(double)judgeQueries.get(query.getId()).numRelDocs();
	}
	
	/*
	 * isRelevant check if the query with queryId and document with docId is relevant.
	 */
	public static boolean isRelevant(int queryId, String docId) {
		return judgeQueries.get(queryId).contains(docId);
	}
	
	
	/*
	 * (EXTRA CREDIT) Paired t-test to find the difference between 2 different method of information retrieval.
	 */
	/*public static double t-test(Map<Integer, Query> method1, Map<Integer, Query> method2) {
		for(int i=1; i<=1000; i++) {
			pw1.println(recall(i, method1.get(450)));
		}
	}
	*/
	
	public static void main(String[] args) throws FileNotFoundException {
		
		/*
		 * Pre-process all given input file into hashmaps.
		 * Each hashmap maps a queryId to a class Query.
		 */
		judgeQueries = JudgementFile.ReadJudgementFile("qrels");
		
		Map<Integer, Query> bm25 = RankingAlgo.ReadRankingFile("bm25.trecrun");
		Map<Integer, Query> ql = RankingAlgo.ReadRankingFile("ql.trecrun");
		Map<Integer, Query> sdm = RankingAlgo.ReadRankingFile("sdm.trecrun");
		Map<Integer, Query> stress = RankingAlgo.ReadRankingFile("stress.trecrun");

		pw = new PrintWriter(new File("output.metrics"));
		
		//print 4 method to the given file:
		printMeanScore("bm25.trecrun", bm25);
		printMeanScore("ql.trecrun", ql);
		printMeanScore("sdm.trecrun", sdm);
		printMeanScore("stress.trecrun", stress);
		
		//Gather data for plot of query 450:
		PrintWriter pw1 = new PrintWriter(new File("query450.txt"));
		for(int i=1; i<=1000; i++) {
			pw1.println(recall(i, sdm.get(450)));
		}

		pw1.close();
		pw.close();
	}

}
