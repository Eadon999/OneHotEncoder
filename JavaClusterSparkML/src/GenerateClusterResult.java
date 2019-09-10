import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: Don
 * @Date: 2019/9/6 18:54
 * @Description:
 */
public class GenerateClusterResult {
    public static void main(String[] args) {
        if (args.length == 0) {
            String userFile = "D:\\PersonalGitProject\\ClusterSparkML\\src\\query_clicked_user_profile.txt";
            String output = "D:\\PersonalGitProject\\ClusterSparkML\\src\\kmodes_user_cluster_res.txt";
            clusterResToFile(userFile, output);
        }else {
            String userFile = args[0];
            String output = args[1];
            clusterResToFile(userFile, output);
        }
    }

    public static void clusterResToFile(String userFeatureFile, String outputFile) {
        /**
         * @Description:
         * @Param: [userFeatureFile, outputFile] 有点击声音集的活跃用户属性特征数据文件，保存聚类结果文件
         * @return: void
         */
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        String date = ft.format(dNow);
        KmodesPredictor kmodeModel = new KmodesPredictor();
        HashMap<String, String> userProfile = new HashMap<>();
        //文件内用户特征顺序：device_id,act_time,gender,ip_provi,ip_city,brand
        List<String> userFeatures = readFile(userFeatureFile);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userFeatures.size(); i++) {
            String[] features = userFeatures.get(i).split(",");
            userProfile.put("timeHour", features[1]);
            userProfile.put("gender", features[2]);
            userProfile.put("province", features[3]);
            userProfile.put("city", features[4]);
            userProfile.put("brand", features[5]);
//            System.out.println(features.toString());
            int clusterResult = kmodeModel.kmodesPredictor(userProfile);
            stringBuilder.append(features[0]);
            stringBuilder.append(",");
            stringBuilder.append(clusterResult);
            stringBuilder.append(",");
            stringBuilder.append(date);
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder);
        writeToFile(outputFile, stringBuilder.toString());
    }


    private static void writeToFile(String path, String lines) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(lines);
            out.close();
        } catch (IOException e) {
        }
    }

    private static List<String> readFile(String filePath) {
        /**
         * @Description: 获取映射关系字典
         * @Param: []
         * @return: java.lang.String
         */
        List<String> list = new ArrayList<>();
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                list.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }
}
