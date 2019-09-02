import os
import json
from numpy import array
from numpy import argmax
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import OneHotEncoder
from utils.load_data import Utils


class FeatureProcesser:
    def get_province_map(self):
        province = """东北地区	黑龙江	吉林	辽宁					
    华北地区	北京	天津	河北	山西	内蒙古			
    华中地区	河南	湖北	湖南					
    华东地区	山东	江苏	安徽	上海	浙江	江西	福建	台湾
    华南地区	广东	广西	海南	香港	澳门			
    西北地区	陕西	甘肃	宁夏	青海	新疆			
    西南地区	四川	贵州	云南	重庆	西藏"""
        province_list = province.split('\n')
        province_map = {}
        for line in province_list:
            province_k_v = line.split('\t')
            for i, v in enumerate(province_k_v):
                region = province_k_v[0]
                if v != '' and i != 0:
                    province_map[v] = region
        region_file = open("././province_region_map.txt", 'w+', encoding='UTF-8')
        for k, v in province_map.items():
            region_file.write(k + ':' + v + '\n')
        region_file.close()

    def get_onehot_encode(self, source_data, feature_name, destination):
        # 类别转为对应id
        label_encoder = LabelEncoder()
        integer_encoded = label_encoder.fit_transform(source_data)
        # onehot编码
        onehot_encoder = OneHotEncoder(sparse=False)
        integer_encoded = integer_encoded.reshape(len(integer_encoded), 1)
        onehot_encoded = onehot_encoder.fit_transform(integer_encoded)
        map_file = open(os.path.join(destination, feature_name + '.txt'), 'w', encoding='UTF-8')
        map_dict = {}
        for k, v in zip(source_data, onehot_encoded):
            k = k.lower()
            map_dict[k] = v.tolist()
        map_file.write(json.dumps(map_dict, ensure_ascii=False))


if __name__ == '__main__':
    utils = Utils()
    encoder = FeatureProcesser()
    brand_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\brand_online.csv'
    city_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\city_level.csv'
    province_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\province_region.csv'
    time_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\time_table.csv'
    gender = ['0', '1', 'other']  # 0:男性，1：女性，2：其他
    brand_data = utils.csv_reader(brand_path)['brand']
    city_data = utils.csv_reader(city_path)['level']
    time_data = utils.csv_reader(time_path)['tag']
    province_data = utils.csv_reader(province_path)['region']
    # encoder.get_onehot_encode(brand_data, 'brand', './')
    # encoder.get_onehot_encode(city_data, 'city_level', './')
    # encoder.get_onehot_encode(time_data, 'time_table', './')
    # encoder.get_onehot_encode(gender, 'gender', './')
    encoder.get_onehot_encode(province_data, 'province_region', './')
