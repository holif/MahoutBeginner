import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * 对物品组合列表进行计数，建立物品的同现矩阵
 * @progect Mahout_0.12.1
 * @file Step2.java
 * @author root
 * @date 2017年5月7日
 */
public class Step2 {
	
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static Text k = new Text("");
        private final static IntWritable v = new IntWritable(1);
  
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = Pattern.compile("[\t,]").split(value.toString());
            for (int i = 1; i < tokens.length; i++) {
                String itemID = tokens[i].split(":")[0];
                for (int j = 1; j < tokens.length; j++) {
                    String itemID2 = tokens[j].split(":")[0];
                    k.set(itemID + ":" + itemID2);

                    context.write(k, v);
                }
            }
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            
            Iterator<IntWritable> l =values.iterator();
            
            while (l.hasNext()) {
                sum += l.next().get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "Step2");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}
}
