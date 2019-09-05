
import jdk.nashorn.internal.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.spark.sql.catalyst.expressions.Lower;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: Don
 * @Date: 2019/9/4 16:00
 * @Description:
 */
public class KmeansPredictor {
    public KmeansPredictor() {

    }

    public static void main(String[] args) {
        List<String> genderMap = readFile(".\\files\\featureOnehotMap\\gender.txt");
        List<String> brandMap = readFile(".\\files\\featureOnehotMap\\brand.txt");
        List<String> cityLevelMap = readFile(".\\files\\featureOnehotMap\\city_level.txt");
        List<String> provinceRegionMap = readFile(".\\files\\featureOnehotMap\\province_region.txt");
        System.out.println(provinceRegionMap);
        System.out.println(cityLevelMap);
        List<String> timeScenesMap = readFile(".\\files\\featureOnehotMap\\time_scenes.txt");
        List<String> provinceRegionBinMap = readFile(".\\files\\featureBinMap\\province_region_bin.txt");
        List<String> cityLevelBinMap = readFile(".\\files\\featureBinMap\\city_level_bin.txt");
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


    public void featureEngineer(HashMap<String, String> userProfile) {
        /**
         * @Description: 用户特征处理为onehot
         * @Param: [userProfile]用户特征键值对 device_id=123,gender="男"
         * @return: void
         */
    }

    public void loadFeatureOnehot() {
        /**
         * @Description: 加载特征对应的onehot向量
         * @Param: []
         * @return: void
         */
    }

    public void loadFeatureBox() {
        /**
         * @Description: 加载分箱特征映射关系
         * @Param: []
         * @return: void
         */
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
