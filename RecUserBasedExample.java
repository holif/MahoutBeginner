package xyz.baal.mahout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * 基于用户的推荐程序
 * @progect Mahout_0.9
 * @file RecUserBasedExample.java
 * @author baalhuo
 * @date 2016年10月18日
 */
public class RecUserBasedExample {

	public static void main(String[] args) throws IOException, TasteException {
		// 加载数据
		DataModel model = new FileDataModel(new File("item.csv"));
		// 相似性度量
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		// 用户邻域
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		// 生成推荐引擎
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		// 为用户1 推荐2个物品
		List<RecommendedItem> recommendations = recommender.recommend(1, 2);

		for (RecommendedItem recommendation : recommendations) {
			System.out.println(recommendation);
		}
	}
}