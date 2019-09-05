import os
import time
import random
import pandas as pd
from utils.load_data import Utils


class GenerateTrainingDataKmodes:
    """
    适用与kmode的训练数据，只是获取每个feature类别的id
    """

    def __init__(self, file_path_dict):
        self.reader = Utils()
        # 特征切分数据字典路径
        self.city_path = file_path_dict.get('city_path')
        self.province_path = file_path_dict.get('province_path')
        self.time_path = file_path_dict.get('time_path')
        # 特征对应的one hot编码数据字典路径
        self.brand_onehot_p = file_path_dict.get('brand_onehot')
        self.city_onehot_p = file_path_dict.get('city_onehot')
        self.time_onehot_p = file_path_dict.get('time_onehot')
        self.gender_onehot_p = file_path_dict.get('gender_onehot')
        self.province_onehot_p = file_path_dict.get('province_onehot')
        self.load_map_data()
        self.load_onthot_data()

    def load_map_data(self):
        # 加载特征对应的分段数据，比如："黑龙江":"东北地区"
        self.city_level = self.reader.get_dict(self.city_path)
        self.province_region = self.reader.get_dict(self.province_path)
        self.time_sence = self.reader.get_dict(self.time_path)

    def load_onthot_data(self):
        self.brand_onehot_d = self.reader.get_dict(self.brand_onehot_p)
        self.city_onehot_d = self.reader.get_dict(self.city_onehot_p)
        self.province_onehot_d = self.reader.get_dict(self.province_onehot_p)
        self.gender_onehot_d = self.reader.get_dict(self.gender_onehot_p)
        self.time_onehot_d = self.reader.get_dict(self.time_onehot_p)

    def get_time_sence(self, time):
        if time >= 7 and time <= 9:
            return 'wakup'
        elif time >= 12 and time <= 13:
            return 'lunch'
        elif time >= 13 and time <= 14:
            return 'rest'
        elif time >= 14 and time <= 16:
            return 'working'
        elif time >= 18 and time <= 20:
            return 'dinner'
        elif time >= 21 and time <= 24:
            return 'night'
        else:
            return 'other'

    def trans_empty_value(self, feature_value, is_int=False):
        """
        处理缺失值和不在字典的值
        :param feature_value: 特征原始值
        :param is_int: 是否为int型
        :return:
        """
        if is_int and not pd.isnull(feature_value):
            # 如果是gender，需要转化为字符串
            value = str(int(feature_value))
        elif not pd.isnull(feature_value) and not is_int:
            value = feature_value
        else:
            # 如果为null或者其他不在字典的值，统一置为other
            value = 'other'
        return value

    def get_feature_onehot(self, row):
        sample_onehot = []
        time_sence = self.trans_empty_value(self.get_time_sence(row['act_time']))
        gender = self.trans_empty_value(row['gender'], is_int=True)
        provi = self.trans_empty_value(row['ip_provi'])
        city = self.trans_empty_value(row['ip_city'])
        brand = self.trans_empty_value(row['brand'])
        time_id = self.get_dict_value(self.time_onehot_d, time_sence)
        gender_id = self.get_dict_value(self.gender_onehot_d, gender)
        provi_region_id = self.get_dict_value(self.province_onehot_d, self.province_region.get(provi, 'other'))
        city_level_id = self.get_dict_value(self.city_onehot_d, self.city_level.get(city, 'other'))
        brand_id = self.get_dict_value(self.brand_onehot_d, brand.lower())  # 手机品牌转为小写
        # print(self.brand_onehot_d, brand, brand_id)
        # print(city, city_level_id)
        # print(gender, gender_id)
        # print(provi, provi_region_id)
        # print(time_sence, gender, provi, city, brand)
        # print(time_id, gender_id, provi_region_id, city_level_id, brand_id)
        sample_onehot.append(time_id)
        sample_onehot.append(gender_id)
        sample_onehot.append(provi_region_id)
        sample_onehot.append(city_level_id)
        sample_onehot.append(brand_id)

        onehot_str = ','.join([str(i) for i in sample_onehot])  # list拼接为字符串
        return onehot_str

    def get_dict_value(self, map_dict, key):
        value = map_dict.get(key, -1)
        if value != -1:
            return value
        else:
            return map_dict.get('other')

    def get_onehot_sample(self, train_data_path, save_path):
        data_df = self.reader.csv_reader(train_data_path)
        data_df = data_df.drop_duplicates()
        print(data_df.shape)

        save_file = open(save_path, 'w+', encoding='utf-8')
        # device_id = open(save_path.replace('train', 'train_deviceID'), 'w+', encoding='utf-8')
        samples = []
        ids = []
        i = 0
        iter = 1
        s_time = time.time()
        for index, row in data_df.iterrows():
            sample_onehor_str = self.get_feature_onehot(row)
            samples.append(row['device_id'] + ',' + sample_onehor_str + '\n')
            # ids.append(row['device_id'] + '\n')
            # 分批写入
            i += 1
            if i == 100000:
                print('iter:{}, time：{}'.format(iter, time.time() - s_time))
                s_time = time.time()
                i = 0
                iter += 1
            #     save_file.writelines(samples)
            #     device_id.writelines(ids)
            #     samples = []
            #     ids = []
            # elif index + 1 == data_df.shape[0]:
            #     print('last group len:{}'.format(len(samples)))
            #     random.shuffle(samples)
            #     save_file.writelines(samples)
            # device_id.writelines(ids)
        random.shuffle(samples)
        save_file.writelines(samples[0:200000])
        # device_id.writelines(ids)
        save_file.close()


if __name__ == '__main__':
    city_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\city_level_map.txt'
    province_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\province_region_map.txt'
    time_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\time_sence_map.txt'
    # 特征对应的one hot编码数据字典路径
    brand_onehot = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_ids\ids_brand.txt'
    city_onehot = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_ids\ids_city_level.txt'
    time_onehot = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_ids\ids_time_table.txt'
    gender_onehot = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_ids\ids_gender.txt'
    province_onehot = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_ids\ids_province_region.txt'
    file_path_dict = {'city_path': city_path, 'province_path': province_path, 'time_path': time_path,
                      'brand_onehot': brand_onehot, 'city_onehot': city_onehot, 'time_onehot': time_onehot,
                      'gender_onehot': gender_onehot, 'province_onehot': province_onehot}
    generater = GenerateTrainingDataKmodes(file_path_dict)
    generater.get_onehot_sample(
        r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\20190830cluster_train.csv',
        'D:\PersonalGitProject\ClusterDataPreprocessing\preprocessed_data/train_data_suffle_20W_kmodes.txt')
    # generater.get_onehot_sample(
    #     r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\original_train_data.csv',
    #     'D:\PersonalGitProject\ClusterDataPreprocessing\preprocessed_data/test_mini_train_data_kmodes.txt')
