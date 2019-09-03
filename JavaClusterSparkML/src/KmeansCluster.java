import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmeansCluster {
    private static int oneCount = 0;
    private static int towCount = 0;

    public static void main(String[] args) {
        String dataPath;
        if (args.length > 0) {
            dataPath = args[0];
        } else {
            dataPath = "D:\\PersonalGitProject\\ClusterDataPreprocessing\\data_preprocess\\data.txt";
        }
        training(dataPath);
    }


    private static JavaSparkContext init() {
        SparkConf conf = new SparkConf().setAppName("kmeans-SF").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        sc.setLogLevel("WARN");

        return sc;

    }

    private static void writer(String line, String filePath, Boolean append) throws IOException {
        FileWriter out = new FileWriter(filePath, append);
        out.write(line);
        out.close();
    }

    private static void training(String filePath) {
        Map<String, String> datamap = new HashMap<>();
        JavaSparkContext jsc = init();
        JavaRDD<String> data = jsc.textFile(filePath);
        JavaRDD<Vector> parsedData = data.map(s -> {
            String[] sarray = s.split(",");
            double[] values = new double[sarray.length];
            String[] keys = new String[sarray.length];
            for (int i = 1; i < sarray.length; i++) {
                values[i] = Double.parseDouble(sarray[i]);
            }
            writer(sarray[0] + ":" + Vectors.dense(values) + "\n", "device_id.txt", true);
            return Vectors.dense(values);
        });

//        JavaRDD<String> dataDeviceid = data.map(s -> {
//            String[] sarray = s.split(",");
//            writer(sarray + "\n", "device_id.txt", true);
//            return "";
//        });
        parsedData.cache();
//        parsedData.collect();

        // Cluster the data into n classes using KMeans
        //选择10-20个类别训练
        int numClusters = 15;
        int numIterations = 600;
        int[] numClusterList = {8, 10, 12, 14, 16, 18, 20};
        for (int numCluster : numClusterList) {
            //model
            System.out.println("+++++++++++++++numCluster：" + numCluster + "++++++++++++++++++");
            KMeansModel clusterModel = KMeans.train(parsedData.rdd(), numCluster, numIterations);
            String resultFilePath = "numCluster_"+numCluster + ".txt";
            JavaPairRDD<Integer, Vector> conuntbyk = parsedData.mapToPair(x -> {
                int pd = clusterModel.predict(x);
                writer(String.valueOf(pd) + ':' + x + '\n', resultFilePath, true); //预测结果写入文件
                return new Tuple2<Integer, Vector>(pd, x);
            });

            Map<Integer, Long> map = conuntbyk.countByKey();

            for (Map.Entry<Integer, Long> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            }

//            System.out.println("Cluster centers:");
//            for (Vector center : clusters.clusterCenters()) {
//                System.out.println(" " + center);
//            }


            double cost = clusterModel.computeCost(parsedData.rdd());
            System.out.println("Cost: " + cost);

            // Evaluate clustering by computing Within Set Sum of Squared Errors
            double WSSSE = clusterModel.computeCost(parsedData.rdd());
            System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

            // Save and load model
            //  clusters.save(jsc.sc(), "target/org/apache/spark/JavaKMeansExample/KMeansModel");
            //  KMeansModel sameModel = KMeansModel.load(jsc.sc(),
            //          "target/org/apache/spark/JavaKMeansExample/KMeansModel");
        }
    }

}