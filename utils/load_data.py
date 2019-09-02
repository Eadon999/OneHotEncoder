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
        map_dict = json.loads(lines[0])
        return map_dict

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
    provice_path = r'D:\PersonalGitProject\ClusterDataPreprocessing\map_data\time_sence.csv'
    provice_dest = r'D:\PersonalGitProject\ClusterDataPreprocessing\feature_map_dict\time_sence_map.txt'
    utils.generate_map(provice_path, provice_dest, 'tag', 'time')
