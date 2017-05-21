package recommender;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * 基于用户推荐
 * @progect Mahout_0.12.2
 * @file UserBasedRecommender.java
 * @author holif
 * @date 2016年10月6日
 */
public class UserBasedRecommender {

	public static void main(String[] args) {
		// 基于数据库的数据
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName("localhost");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setDatabaseName("mahout");

		MySQLJDBCDataModel dataModel = new MySQLJDBCDataModel(dataSource, "preftable", "user", "item", "pref", null);
	
		try {
			UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(dataModel);
			NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(2, userSimilarity, dataModel);
			Recommender r = new GenericUserBasedRecommender(dataModel, neighbor, userSimilarity);
			LongPrimitiveIterator iter = dataModel.getUserIDs();
			
			while(iter.hasNext()){
				long usid = iter.nextLong();
				List<RecommendedItem> list = r.recommend(usid, 3);//为用户推荐3个物品
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