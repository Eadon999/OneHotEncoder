import os
import json

onehot_data_root = "../onehot_data_map"
onehot_files = os.listdir(onehot_data_root)


def write2file(onehot_files):
    for f in onehot_files:
        if f == 'province_region.txt':
            print(f)
            f_path = os.path.join(onehot_data_root, f)

            data_dict = json.loads(open(f_path, encoding='utf-8').readlines()[0])
            id_dict = parse_dict(data_dict)
            with open('ids_' + f, 'w', encoding='utf-8') as f:
                f.write(json.dumps(id_dict, ensure_ascii=False))
                # pass


def parse_dict(dict_data):
    id_dict = {}
    for ids, key in enumerate(dict_data.keys()):
        id_dict[key] = ids
        print(ids, key)
    return id_dict


if __name__ == '__main__':
    write2file(onehot_files)
