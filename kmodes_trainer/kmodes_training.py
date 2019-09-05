import numpy as np
import time
from kmodes.kmodes import KModes
import pickle
from sklearn.externals import joblib
from sklearn2pmml import PMMLPipeline, sklearn2pmml
from sklearn.cluster import KMeans

# print(train_data)
# class KmodesTrainer:
train_data = open(
    r'D:\PersonalGitProject\ClusterDataPreprocessing\preprocessed_data\test_mini_train_data_kmodes.txt',
    encoding='utf-8').readlines()


def extract_feature(train_data):
    data = []
    for i in train_data:
        data_list = i.strip('\n').split(',')[1:]
        data_int_list = list(map(lambda x: int(x), data_list))
        data.append(data_int_list)
    data_array = np.array(data)
    return data_array


# extract_feature(train_data)

# random categorical data
data = extract_feature(train_data)
# print(data)
n_clusters_list = [4, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26]
n_clusters_list = [20]
for n_clusters in n_clusters_list:
    s_time = time.time()
    km = KModes(n_clusters=n_clusters, init='Cao', n_init=5, verbose=1, max_iter=1000)
    clusters = km.fit_predict(data)
    # km = KModes(n_clusters=n_clusters, init='Cao', n_init=5, verbose=0, max_iter=100)
    # print(km.fit_transform(data))
    # print("================")
    # kms = KMeans(n_clusters=n_clusters)
    # print(kms.fit_transform(data))
    # # print(kms.predict(data))
    km.cluster_centroids_
    pipeline_kmeand = PMMLPipeline([
        ("classifier", KMeans(n_clusters=n_clusters))
    ])
    pipeline_kmeand.fit(data)
    sklearn2pmml(pipeline_kmeand, 'test_kmeans_pmml.pmml', debug=True)
    print("==========================================")
    pipelines = PMMLPipeline([
        ("classifier", KModes(n_clusters=n_clusters, n_init=5, verbose=0, max_iter=100))
    ])
    pipelines.fit(data)

    # joblib.dump(km, "train_model.m")
    sklearn2pmml(pipelines, 'test_pmml.pmml', debug=True, with_repr=True)
'''
    print("+++++++++++++++n_clusters:{}+++++++++++++++++++".format(n_clusters))
    print('loss ==============:{}, time spend===========:{}'.format(km.cost_, time.time() - s_time))
    print(km.cluster_centroids_)
    result_file = 'results/1000_iteration/cluster_{}_kmodes_res.txt'.format(str(n_clusters))
    with open(result_file, 'w', encoding='utf-8') as f:
        i = 0
        for lable, sample in zip(km.labels_, data):
            f.write(str(lable) + ':' + str(list(sample)) + '\n')
            # i += 1
            # if i == 10000:
            #     break
    print("+++++++++++++++finished+++++++++++++++++++")
'''
