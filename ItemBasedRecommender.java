package recommender;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * 基于物品推荐
 * @progect Mahout_0.12.2
 * @file ItemBasedRecommender.java
 * @author holif
 * @date 2016年10月6日
 */
public class ItemBasedRecommender {

	public static void main(String[] args) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName("localhost");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setDatabaseName("mahout");
		MySQLJDBCDataModel dataModel = new MySQLJDBCDataModel(dataSource, "preftable", "user", "item", "pref", null);
		try {
			ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
			Recommender r = new GenericItemBasedRecommender(dataModel, similarity);
			
			LongPrimitiveIterator iter = dataModel.getUserIDs();
		
			while(iter.hasNext()){
				long usid = iter.nextLong();
				//第三个参数includeKnownItems表示是否推荐已知的物品
				List<RecommendedItem> list = r.recommend(usid, 3, false);
				System.out.print("usid["+usid+"]");
				for (RecommendedItem recommendedItem : list) {
					System.out.print(recommendedItem);
				}
				System.out.println();
			}
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
}