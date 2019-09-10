import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @Auther: Don
 * @Date: 2019/9/5 16:09
 * @Description:Kmodes聚类算法，返回用户的聚类标签
 */


public class KmodesPredictor {
    //从文件加载特征对应编码的json字符串
    private static List<String> genderMap = readFile("./files/featureMapId/ids_gender.txt");
    private static List<String> brandMap = readFile("./files/featureMapId/ids_brand.txt");
    private static List<String> cityLevelMap = readFile("./files/featureMapId/ids_city_level.txt");
    private static List<String> provinceRegionMap = readFile("./files/featureMapId/ids_province_region.txt");
    private static List<String> timeScenesMap = readFile("./files/featureMapId/ids_time_table.txt");
    //聚类中心向量json字符串
    private static List<String> clusterCentroidsMap = readFile("./files/clusterCentroid/cluster_centroids.txt");
    //从文件加载特征分桶的json字符串
    private static List<String> provinceRegionBinMap = readFile("./files/featureBinMap/province_region_bin.txt");
    private static List<String> cityLevelBinMap = readFile("./files/featureBinMap/city_level_bin.txt");
    //从json字符串获取特征对应的编码JsonObject
    private static JSONObject genderIdRelation = getMapingRelation(genderMap);
    private static JSONObject brandIdRelation = getMapingRelation(brandMap);
    private static JSONObject cityLevelIdRelation = getMapingRelation(cityLevelMap);
    private static JSONObject provinceRegionIdRelation = getMapingRelation(provinceRegionMap);
    private static JSONObject timeScenesIdRelation = getMapingRelation(timeScenesMap);
    //从json字符串获取特征分桶对应的编码JsonObject
    private static JSONObject provinceRegionBinRelation = getMapingRelation(provinceRegionBinMap);
    private static JSONObject cityLevelBinRelation = getMapingRelation(cityLevelBinMap);
    private static HashMap<Integer, Integer[]> clusterCentroids = getClucterCentroidsMap();

    public static void main(String[] args) {
        int iteration = 1;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iteration; i++) {
            KmodesPredictor kmodes = new KmodesPredictor();
            HashMap<String, String> userProfile = new HashMap<>();
            userProfile.put("gender", "0");
            userProfile.put("brand", "Huawei");
            userProfile.put("city", "许昌");
            userProfile.put("province", "河南");
            userProfile.put("timeHour", "23");
            int clusterResult = kmodes.kmodesPredictor(userProfile);
            System.out.println("聚类结果为：" + clusterResult);
            // 抽取用户属性特征映射id组成的向量
//        Integer[] featureVec = extractUserProfile(userProfile, 5);
//        for (int i = 0; i < featureVec.length; i++) {
//            System.out.println("用户特征向量为：" + featureVec[i]);
//        }
            // 得出聚类标签
//        int predCluster = kmodesCluster(clusterCentroids, featureVec);
//        System.out.println("聚类结果为：" + predCluster);
        }
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;
        long costTimeS = costTime / 1000;
        System.out.println("运行时间为：" + costTime + "ms " + costTimeS + "s");
    }

    public int kmodesPredictor(HashMap<String, String> userProfile) {
        Integer[] featureVec = extractUserProfile(userProfile, 5);
        return kmodesCluster(clusterCentroids, featureVec);
    }

    private static Integer[] extractUserProfile(HashMap<String, String> userProfile, int featureNum) {
        // 性别：“0”是男性，“1”是女性
        String gender = userProfile.get("gender");
        String brand = userProfile.get("brand");
        String city = userProfile.get("city");
        String province = userProfile.get("province");
//        用户请求时间的hour
        String timeHour = userProfile.get("timeHour");
//        获取用户城市对应的等级和省份对应的区域
        String timeScenes = getTimeSence(timeHour);
        Object cityLevel = getMapValue(cityLevelBinRelation, city);
        Object provinceRegion = getMapValue(provinceRegionBinRelation, province);

        /**
         * 获取用户特征对应的编码id
         */
        Object timeScenesId = getMapValue(timeScenesIdRelation, timeScenes);
        Object genderId = getMapValue(genderIdRelation, gender);
        Object provinceRegionId = getMapValue(provinceRegionIdRelation, (String) provinceRegion);
        Object cityLevelId = getMapValue(cityLevelIdRelation, (String) cityLevel);
        Object brandId = getMapValue(brandIdRelation, brand);

//        System.out.println("性别：" + gender + " 手机品牌：" + brand + " 城市：" + city + " 省份：" + province + " 时间：" + timeHour);
//        System.out.println("城市等级：" + cityLevel + " 省份区域：" + provinceRegion + " 时间场景：" + timeScenes);
//        System.out.println(" 时间场景Id：" + timeScenesId + "性别Id：" + genderId + " 省份区域Id：" + provinceRegionId + " 城市等级Id：" + cityLevelId + " 手机品牌Id：" + brandId);
        Integer[] featureArray = new Integer[featureNum];
//        特征向量循序必须与训练时一致：时间、性别、省份、城市、手机品牌
        featureArray[0] = Integer.parseInt(String.valueOf(timeScenesId));
        featureArray[1] = Integer.parseInt(String.valueOf(genderId));
        featureArray[2] = Integer.parseInt(String.valueOf(provinceRegionId));
        featureArray[3] = Integer.parseInt(String.valueOf(cityLevelId));
        featureArray[4] = Integer.parseInt(String.valueOf(brandId));
        return featureArray;
    }

    private static HashMap<Integer, Integer[]> getClucterCentroidsMap() {
        HashMap<Integer, Integer[]> clusterMap = new HashMap<Integer, Integer[]>();
        JSONObject clustersJsonObj = getMapingRelation(clusterCentroidsMap);
        for (String key : clustersJsonObj.keySet()) {
            String[] tmpValue = clustersJsonObj.get(key).toString().replaceAll("\\[|\\]", "").split(",");
            int tmpValueLength = tmpValue.length;
            Integer[] valueIntArr = new Integer[tmpValueLength];
            for (int i = 0; i < tmpValueLength; i++) {
                valueIntArr[i] = Integer.parseInt(tmpValue[i]);
            }
            clusterMap.put(Integer.parseInt(key), valueIntArr);
        }
        return clusterMap;
    }

    private static int kmodesCluster(HashMap<Integer, Integer[]> clusterCentroids, Integer[] inputVector) {
        /**
         * @Description: 获取聚类的结果的标签
         * @Param: [clusterCentroids, inputVector] 聚类中心标签和对应的类中心向量，输入用户属性向量
         * @return: java.lang.Integer
         */
        int maxDist = 0;
        int nearestCluster = 0;
        for (Map.Entry<Integer, Integer[]> valueMap : clusterCentroids.entrySet()) {
            Integer clusterLable = valueMap.getKey();
            Integer[] centorids = valueMap.getValue();
            int simDistance = calcSameFeatureNum(centorids, inputVector);
//            System.out.println("与cluster：" + clusterLable + " 距离为：" + simDistance);
            if (simDistance > maxDist) {
                maxDist = simDistance;
                nearestCluster = clusterLable;
            }
        }
        return nearestCluster;
    }

    private static int calcSameFeatureNum(Integer[] centroids, Integer[] input) {
        /**
         * @Description: 计算两个向量元素的相似个数
         * @Param: [centroids, input] 类中心向量，输入样本的向量
         * @return: java.lang.Integer
         */
        int simNum = 0;
        if (Arrays.equals(centroids, input)) {
//            完全一样直接返回，增加效率
            return simNum;
        }
        for (int i = 0; i < input.length; i++) {
            if (centroids[i].equals(input[i])) {
                simNum++;
            }
        }
        return simNum;
    }

    private static String getTimeSence(String time) {
        /**
         * @Description: 根据访问时间的小时单位，划分对应的场景
         * @Param: []
         * @return: java.lang.String
         */
        int timeInt = Integer.valueOf(time);
        if (timeInt >= 7 && timeInt <= 9) {
            return "wakup";
        } else if (timeInt >= 12 && timeInt <= 13) {
            return "lunch";
        } else if (timeInt == 14) {
            return "rest";
        } else if (timeInt > 14 && timeInt <= 18) {
            return "working";
        } else if (timeInt > 18 && timeInt <= 20) {
            return "dinner";
        } else if (timeInt >= 21 && timeInt <= 24) {
            return "night";
        } else {
            return "other";
        }
    }

    private static JSONObject getMapingRelation(List<String> mapList) {
        /** 获取映射关系字典
         * @Description:
         * @Param: [mapList]
         * @return: com.alibaba.fastjson.JSONObject
         */
        String jsonStr = mapList.get(0);
        JSONObject mapTypes = JSON.parseObject(jsonStr);
        return mapTypes;
    }

    private static Object getMapValue(JSONObject mapValues, String key) {
        String keyLower = key.toLowerCase();
        if (mapValues.get(keyLower) == null) {
            // 未获取到对应key的，使用other向量表示
            return mapValues.get("other");
        } else {
            return mapValues.get(keyLower);
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
