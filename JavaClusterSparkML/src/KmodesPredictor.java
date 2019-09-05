import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @Auther: Don
 * @Date: 2019/9/5 16:09
 * @Description:
 */


public class KmodesPredictor {
    public static void main(String[] args) {
        ArrayList<Integer> lists2 = new ArrayList<Integer>() {{
            add(1);
            add(2);
        }};
        HashMap<Integer, Integer[]> map = new HashMap<Integer, Integer[]>();
        Integer[] clusterOne = {1, 2, 3, 4, 5};
        Integer[] clusterTwo = {1, 2, 3, 5, 5};
        Integer[] clusterThree = {1, 2, 3, 5, 6};
        map.put(1, clusterOne);
        map.put(2, clusterTwo);
        System.out.println(calcSimilarDist(clusterOne, clusterTwo));
        System.out.println(getCluster(map, clusterThree));
        init();
    }

    public static void init() {
//        从文件加载特征id映射关系字符串
        List<String> genderMap = readFile(".\\files\\featureMapId\\ids_gender.txt");
        List<String> brandMap = readFile(".\\files\\featureMapId\\ids_brand.txt");
        List<String> cityLevelMap = readFile(".\\files\\featureMapId\\ids_city_level.txt");
        List<String> provinceRegionMap = readFile(".\\files\\featureMapId\\ids_province_region.txt");
        System.out.println(provinceRegionMap);
        System.out.println(cityLevelMap);
        List<String> timeScenesMap = readFile(".\\files\\featureMapId\\ids_time_table.txt");
//        从文件加载特征分桶json字符串
        List<String> provinceRegionBinMap = readFile(".\\files\\featureBinMap\\province_region_bin.txt");
        List<String> cityLevelBinMap = readFile(".\\files\\featureBinMap\\city_level_bin.txt");
//        从文件获取特征对应的编码
        JSONObject genderRelation = getMapingRelation(genderMap);
        JSONObject brandRelation = getMapingRelation(brandMap);
        JSONObject cityLeveRelation = getMapingRelation(cityLevelMap);
        JSONObject provinceRegionRelation = getMapingRelation(provinceRegionMap);
        JSONObject timeScenesRelation = getMapingRelation(timeScenesMap);
        String city = (getMapValue(cityLeveRelation, "中心城市").toString());
        String region = (getMapValue(provinceRegionRelation, "东北地区").toString());
        System.out.println(city);
        System.out.println(region);
    }


    public static Integer getCluster(HashMap<Integer, Integer[]> clusterCentroids, Integer[] inputVector) {
        /**
         * @Description: 获取聚类的结果的标签
         * @Param: [clusterCentroids, inputVector] 聚类中心标签和对应的类中心向量，输入样本向量
         * @return: java.lang.Integer
         */
        Integer maxDist = 0;
        Integer nearestCluster = 0;
        for (Map.Entry<Integer, Integer[]> valueMap : clusterCentroids.entrySet()) {
            Integer clusterLable = valueMap.getKey();
            Integer[] centorids = valueMap.getValue();
            Integer simDistance = calcSimilarDist(centorids, inputVector);
            if (simDistance >= maxDist) {
                nearestCluster = clusterLable;
            }
        }
        return nearestCluster;
    }

    public static Integer calcSimilarDist(Integer[] centroids, Integer[] input) {
        /**
         * @Description: 计算两个向量元素的相似个数
         * @Param: [centroids, input] 类中心向量，输入样本的向量
         * @return: java.lang.Integer
         */
        Integer simNum = 0;
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

    public String getTimeSence() {
        /**
         * @Description: 根据访问时间的小时单位，划分对应的场景
         * @Param: []
         * @return: java.lang.String
         */
        return "";
    }

    public static JSONObject getMapingRelation(List<String> mapList) {
        /**
         * @Description: 获取映射关系字典
         * @Param: [mapList]
         * @return: java.util.Map
         */
        String jsonStr = mapList.get(0);
        JSONObject mapTypes = JSON.parseObject(jsonStr);
        return mapTypes;
    }

    public static Object getMapValue(JSONObject mapValues, String key) {
        String keyLower = key.toLowerCase();
        if (mapValues.get(keyLower) == null) {
            // 未获取到对应key的，使用other向量表示
            return mapValues.get("other");
        } else {
            return mapValues.get(keyLower);
        }
    }

    public static List<String> readFile(String filePath) {
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
