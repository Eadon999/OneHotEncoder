import os
import json
import numpy as np
import time
import sklearn
from sklearn.manifold import TSNE
from sklearn.datasets import load_digits

"""
tsne 是无监督降维技术，labels 选项可选；
X是由N 个样本组成的二维矩阵，每个样本由 D 维数据构成（N行D列）；
no_dims 的默认值为 2；（压缩后的维度） 
tsne 函数实现，X∈RN×D⇒RN×no_dimesX∈RN×D⇒RN×no_dimes（mappedX）
init_dims：注意，在运行 tsne 函数之前，会自动使用 PCA 对数据预处理，将原始样本集的维度降低至 init_dims 维度（默认为 30）。因此用户可以自行设置一下init-dims的数量。或者提前使用matlab中的PCA检测一下哪个维度能够保持你的样本数据的正确率。
perplexity：高斯分布的perplexity，默认为 30； 此参数不了解，但是教程中说，越是高密度的样本，其值越大。一般推荐5-50
"""

# Random state.
RS = 20190904

import matplotlib.pyplot as plt
import matplotlib.patheffects as PathEffects
import matplotlib

# We import seaborn to make nice plots.
import seaborn as sns


class TsneVisable:
    def scatter(self, X, labels, category_num, preplexity, result_root):
        '''
        :param X:original X
        :param labels:
        :param class_num: the number of classes
        :return:
        '''
        print('reduce dimmension start')
        s_time = time.time()
        reduce_dimmension_X = TSNE(random_state=RS, perplexity=preplexity).fit_transform(X)
        print('reduce dimmension finish, cost:{}'.format(time.time() - s_time))
        sns.set_style('darkgrid')
        sns.set_palette('muted')
        sns.set_context("notebook", font_scale=1.5,
                        rc={"lines.linewidth": 2.5})

        # We choose a color palette with seaborn.
        palette = np.array(sns.color_palette("hls", category_num))
        print('color setting finish!')

        # We create a scatter plot.
        # f = plt.figure(figsize=(8, 8))
        f = plt.figure(figsize=(8, 8))
        ax = plt.subplot(aspect='equal')
        sc = ax.scatter(reduce_dimmension_X[:, 0], reduce_dimmension_X[:, 1], lw=0, s=40,
                        c=palette[labels.astype(np.int)])
        print('scatter finish!')
        # plt.savefig('digits_tsne-generated.png', dpi=120)
        # plt.show()
        # plt.xlim(-25, 25)
        # plt.ylim(-25, 25)
        plt.xlim(-15, 15)
        plt.ylim(-15, 15)
        ax.axis('off')
        ax.axis('tight')

        # We add the labels for each category.
        txts = []
        for i in range(category_num):
            # Position of each label.
            xtext, ytext = np.median(reduce_dimmension_X[labels == i, :], axis=0)
            txt = ax.text(xtext, ytext, str(i), fontsize=24)
            txt.set_path_effects([
                PathEffects.Stroke(linewidth=5, foreground="w"),
                PathEffects.Normal()])
            txts.append(txt)
        pic_name = "cluster_{}&plexity_{}_tsne_pic.png".format(str(category_num), str(preplexity))
        pic_path = os.path.join(result_root, 'category_{}'.format(str(category_num)))
        if not os.path.exists(pic_path):
            os.mkdir(pic_path)
        pic_path = os.path.join(pic_path, pic_name)
        plt.savefig(pic_path, dpi=120)
        plt.close()

        return f, ax, sc, txts

    def extract_label_value(self, path, test_num):
        with open(path) as f:
            lines_list = f.readlines()[0:test_num]
        label_list = []
        value_list = []
        for line in lines_list:
            slist = line.split(':')
            label, value = int(slist[0]), slist[1]
            value = json.loads(value)
            label_list.append(label)
            value_list.append(value)
        label_arrs = np.array(label_list)
        value_arrs = np.array(value_list)
        return value_arrs, label_arrs


if __name__ == '__main__':
    tnser = TsneVisable()
    cluster_num = 8
    preplexity = 40
    test_data_num = 10000
    cluster_num_list = [8, 10, 12, 14, 16, 18, 20]
    tsne_path_root = r'D:\PersonalGitProject\ClusterDataPreprocessing\visable_result'
    for cluster_num in cluster_num_list:
        data_path = r'D:\PersonalGitProject\KmeansResult\Iterations600\numCluster_{}.txt'.format(cluster_num)
        X, y = tnser.extract_label_value(data_path, test_data_num)
        print('data processing finish!')
        # [10, 15, 30, 40, 45, 50, 55]
        preplexity_list = [10, 15, 30, 40, 45, 50, 55]  # 40最优
        for preplexity in preplexity_list:
            tnser.scatter(X, y, cluster_num, preplexity, tsne_path_root)
