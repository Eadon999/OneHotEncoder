from utils.load_data import Utils

path = r'D:\PersonalGitProject\ClusterDataPreprocessing\original_data\20190830cluster_train.csv'
utils = Utils()
df = utils.csv_reader(path)
device_id = utils.txt_reader(
    r'D:\PersonalGitProject\ClusterDataPreprocessing\data_preprocess\train_data_shuffle_20W.txt')
id_list = []
for line in device_id:
    id = line.split(':')[0]
    id_list.append(id)

tmp_list = list(set(id_list))
tmp_list.sort(key=id_list.index)
print(len(tmp_list), len(set(tmp_list)))
# l1 = ['b', 'c', 'd', 'b', 'c', 'a', 'a']
# l2 = list(set(l1))
# l2.sort(key=l1.index)

# selected_data = df[df['device_id'].isin(id_list)]
# selected_data.to_csv('./train_Top9998_source_data.csv', encoding='gb2312')
