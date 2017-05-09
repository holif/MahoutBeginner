import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * 计算推荐结果列表
 * @progect Mahout_0.12.1
 * @file Step4.java
 * @author root
 * @date 2017年5月7日
 */
public class Step4 {

    public static class Mapp extends Mapper<LongWritable, Text, IntWritable, Text> {
        private final static IntWritable k = new IntWritable();
        private final static Text v = new Text();

        private final static Map<Integer, List<Cooccurrence>> cooccurrenceMatrix = new HashMap<Integer, List<Cooccurrence>>();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = Pattern.compile("[\t,]").split(value.toString());

            String[] v1 = tokens[0].split(":");
            String[] v2 = tokens[1].split(":");

            if (v1.length > 1) {// cooccurrence
                int itemID1 = Integer.parseInt(v1[0]);
                int itemID2 = Integer.parseInt(v1[1]);
                int num = Integer.parseInt(tokens[1]);

                List<Cooccurrence> list = null;
                if (!cooccurrenceMatrix.containsKey(itemID1)) {
                    list = new ArrayList<Cooccurrence>();
                } else {
                    list = cooccurrenceMatrix.get(itemID1);
                }
                list.add(new Cooccurrence(itemID1, itemID2, num));
                cooccurrenceMatrix.put(itemID1, list);
            }

            if (v2.length > 1) {// userVector
                int itemID = Integer.parseInt(tokens[0]);
                int userID = Integer.parseInt(v2[0]);
                double pref = Double.parseDouble(v2[1]);
                k.set(userID);
                for (Cooccurrence co : cooccurrenceMatrix.get(itemID)) {
                    v.set(co.getItemID2() + "," + pref * co.getNum());
                    context.write(k, v);
                }

            }
        }
    }

    public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
        private final static Text v = new Text();

        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<String, Double> result = new HashMap<String, Double>();
            Iterator<Text> l = values.iterator();
            while (l.hasNext()) {
                String[] str = l.next().toString().split(",");
                if (result.containsKey(str[0])) {
                    result.put(str[0], result.get(str[0]) + Double.parseDouble(str[1]));
                } else {
                    result.put(str[0], Double.parseDouble(str[1]));
                }
            }
            Iterator<String> iter = result.keySet().iterator();
            while (iter.hasNext()) {
                String itemID = iter.next();
                double score = result.get(itemID);
                v.set(itemID + "," + score);
                context.write(key, v);
            }
        }
    }

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "Step4");

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Mapp.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}

class Cooccurrence {
    private int itemID1;
    private int itemID2;
    private int num;

    public Cooccurrence(int itemID1, int itemID2, int num) {
        super();
        this.itemID1 = itemID1;
        this.itemID2 = itemID2;
        this.num = num;
    }

    public int getItemID1() {
        return itemID1;
    }

    public void setItemID1(int itemID1) {
        this.itemID1 = itemID1;
    }

    public int getItemID2() {
        return itemID2;
    }

    public void setItemID2(int itemID2) {
        this.itemID2 = itemID2;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

}