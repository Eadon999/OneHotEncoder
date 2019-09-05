import numpy as np
import json
from kmodes.kmodes import KModes

train_data = open(
    r'D:\PersonalGitProject\ClusterDataPreprocessing\data_preprocess\test_mini_train_onehot_data_kmodes.txt').readlines()


# print(train_data)


def extract_feature(train_data):
    data = []
    for i in train_data:
        data_list = i.strip('\n').split(',')[1:]
        data_int_list = list(map(lambda x: int(x), data_list))
        data.append(data_int_list)
    data_array = np.array(data)
    return data


extract_feature(train_data)

# random categorical data
data = extract_feature(train_data)
# print(data)
n_clusters_list = [4, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26]
for n_clusters in n_clusters_list:
    km = KModes(n_clusters=10, init='Cao', n_init=5, verbose=1, max_iter=500)
    clusters = km.fit_predict(data)
    print(km.cost_)
    print(km.labels_)
    result_file = './cluster_{}_kmodes_res.txt'.format(str(n_clusters))
    with open(result_file,'w',encoding='utf-8') as f:
        i = 0
        for lable, sample in zip(km.labels_, data):
            f.write(str(lable) + ':' + json.dumps(sample))
            # i += 1
            # if i == 10000:
            #     break
