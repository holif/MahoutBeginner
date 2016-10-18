package xyz.baal.mahout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * 基于物品的推荐程序
 * @progect Mahout_0.9
 * @file RecItemBasedExample.java
 * @author baalhuo
 * @date 2016年10月18日
 */
public class RecItemBasedExample {

	public static void main(String[] args) throws IOException, TasteException {

		DataModel model = new FileDataModel(new File("item.csv"));
		ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
		Recommender r = new GenericItemBasedRecommender(model, similarity);

		LongPrimitiveIterator iter = model.getUserIDs();
		//为每个用户推荐一件物品
		while (iter.hasNext()) {
			long usid = iter.nextLong();
			List<RecommendedItem> list = r.recommend(usid, 1);
			System.out.print("usid[" + usid + "]");
			for (RecommendedItem recommendedItem : list) {
				System.out.print(recommendedItem);
			}
			System.out.println();
		}
	}
}