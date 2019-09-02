import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import scala.Tuple2;

import java.util.List;
import java.util.Map;

public class KmeansCluster {
    private static int oneCount = 0;
    private static int towCount = 0;

    public static void main(String[] args) {
        deeping();
    }


    public static JavaSparkContext init() {
        SparkConf conf = new SparkConf().setAppName("kmeans-SFT").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        sc.setLogLevel("WARN");

        return sc;

    }

    public static void deeping() {

        JavaSparkContext jsc = init();
        String path = "D:\\PersonalGitProject\\ClusterDataPreprocessing\\data_preprocess\\mini_train_onehot_data.txt";
        JavaRDD<String> data = jsc.textFile(path);
//        System.out.println(data.collect());
        JavaRDD<Vector> parsedData = data.map(s -> {
            String[] sarray = s.split(",");
            double[] values = new double[sarray.length];
            for (int i = 0; i < sarray.length; i++) {
                values[i] = Double.parseDouble(sarray[i]);
            }
            return Vectors.dense(values);
        });
//        System.out.println(parsedData.collect());
        parsedData.cache();

        // Cluster the data into two classes using KMeans
        //选择10-20个类别训练
        int numClusters = 15;
        int numIterations = 30;
        KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);

        //=================================================method3
        JavaPairRDD<Integer, Vector> conuntbyk = parsedData.mapToPair(x -> {
            int pd = clusters.predict(x);

            return new Tuple2<Integer, Vector>(pd, x);
        });
        Map<Integer, Long> map = conuntbyk.countByKey();
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

        }

        System.out.println("Cluster centers:");
        for (Vector center : clusters.clusterCenters()) {
            System.out.println(" " + center);
        }


        double cost = clusters.computeCost(parsedData.rdd());
        System.out.println("Cost: " + cost);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(parsedData.rdd());
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        // Save and load model
        //  clusters.save(jsc.sc(), "target/org/apache/spark/JavaKMeansExample/KMeansModel");
        //  KMeansModel sameModel = KMeansModel.load(jsc.sc(),
        //          "target/org/apache/spark/JavaKMeansExample/KMeansModel");
    }

}