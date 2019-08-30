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
        with open(path) as f:
            data = f.readlines()
        return data

    def csv_reader(self, path):
        df = pd.read_csv(path)
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
    provice_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\province_region.csv'
    provice_dest = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\province_region_map.txt'
    utils.generate_map(provice_path, provice_dest, 'province', 'region')
