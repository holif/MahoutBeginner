package recommender;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

/**
 * 评测基于用户的推荐
 * 
 * @progect Mahout_0.12.2
 * @file EvaluateUserBasedRecommender.java
 * @author holif
 * @date 2016年10月24日
 */
public class EvaluateUserBasedRecommender {

	public static void main(String[] args) throws IOException, TasteException {
		// 强制每次选择相同的随机值 生成可重复的结果
		RandomUtils.useTestSeed();
		DataModel model = new FileDataModel(new File("d:/item.csv"));
		// 使用平均值来评分
		// RecommenderEvaluator evaluator = new
		// AverageAbsoluteDifferenceRecommenderEvaluator();
		// 使用平方根来评分
		RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(DataModel dataModel) throws TasteException {
				UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
				return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
			}
		};
		// 训练95%的数据 测试5%  score代表估计值与实际值的偏差大小
		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.95, 1.0);

		System.out.println(score);
	}
}
