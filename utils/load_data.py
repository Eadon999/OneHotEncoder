import pandas as pd
import json
from sklearn.preprocessing import OneHotEncoder


class Utils:
    def __init__(self):
        pass

    def csv_chunk_reader(self, path, chunksize):
        reader = pd.read_csv(path, chunksize)
        return reader

    def txt_reader(self, path):
        with open(path, encoding='utf-8-sig') as f:
            data = f.readlines()
        return data

    def get_dict(self, path):
        lines = self.txt_reader(path)
        print(lines[0])
        map_dict = json.loads(lines[0])
        return map_dict

    def csv_reader(self, path):
        df = pd.read_csv(path, error_bad_lines=False)
        return df

    def generate_map(self, path, dest, col_k, col_v):
        df = self.csv_reader(path)
        key = df[col_k]
        value = df[col_v]
        file = open(dest, 'w', encoding='UTF-8')
        map_dict = {}
        for k, v in zip(key, value):
            map_dict[k] = v
        file.write(json.dumps(map_dict, ensure_ascii=False))


if __name__ == '__main__':
    path = ''
    chunksize = 10000
    utils = Utils()
    time_sence_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\time_sence.csv'
    time_sence_dest = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\test_time_sence_map.txt'
    utils.generate_map(time_sence_path, time_sence_dest, 'tag', 'time')
    city_levle_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\city_level.csv'
    city_levle_dest = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\test_city_level_map.txt'
    utils.generate_map(city_levle_path, city_levle_dest, 'city', 'level')
    provice_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\province_region.csv'
    provice_dest = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\test_province_region_map.txt'
    utils.generate_map(provice_path, provice_dest, 'province', 'region')
