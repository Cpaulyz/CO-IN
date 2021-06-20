import csv
import json
import os

# 用于导出数据
# match ()-[r]->() set r.relationId=toInteger(r.relationId) return r
# match (n) set n.nodeId=toInteger(n.nodeId) return n

textSize = 20
radius = 5


class DataLoader:
    # 菜谱
    caipu = '菜谱'
    caipu_property = ['名称', '口味', '工艺', '耗时']
    # 食材
    shicai = '食材'
    zhuliao = '主料'
    fuliao = '辅料'
    shicai_property = ['简介', '营养价值', '功效']
    yongliang = '用量'

    def __init__(self):
        self.projectId = 5
        self.nodeId = 1
        self.relationId = 1
        self.id = 1

        self.caixi_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', ':LABEL', 'color', 'figure', 'radius:int',
             'textSize']]
        self.caipu_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', 'properties.口味', 'properties.工艺', 'properties.耗时',
             'properties.做法', ':LABEL', 'color', 'figure', 'radius:int', 'textSize']]
        self.shicai_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', 'properties.简介', 'properties.营养价值',
             'properties.功效',
             ':LABEL', 'color', 'figure', 'radius:int', 'textSize']
        ]
        self.belong_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int']
        ]
        self.zhushicai_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int', 'properties.用量']
        ]
        self.fuliao_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int', 'properties.用量']
        ]
        self.chosen_caixi = ['川菜', '鲁菜', '粤菜', '苏菜', '浙菜', '闽菜', '湘菜', '徽菜']  # 选择要导入的菜系类别
        self.caixi_count = 10  # 每个菜系要导入的菜的数量
        self.shicai_map = {}

    def reset_list(self):
        self.nodeId = 1
        self.relationId = 1
        self.shicai_map = {}
        self.caixi_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', ':LABEL', 'color', 'figure', 'radius:int',
             'textSize']]
        self.caipu_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', 'properties.口味', 'properties.工艺', 'properties.耗时',
             'properties.做法', ':LABEL', 'color', 'figure', 'radius:int', 'textSize']]
        self.shicai_list = [
            ['id:ID', 'nodeId:int', 'projectId:int', 'group', 'name', 'properties.简介', 'properties.营养价值',
             'properties.功效',
             ':LABEL', 'color', 'figure', 'radius:int', 'textSize']
        ]
        self.belong_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int']
        ]
        self.zhushicai_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int', 'properties.用量']
        ]
        self.fuliao_list = [
            [':START_ID', 'projectId:int', 'relationId:int', 'name', ':END_ID', ':TYPE', 'width:int', 'properties.用量']
        ]


    def addCaixi(self, caixi):
        tmp = [self.id, self.nodeId, self.projectId, '菜系', caixi, 'node', '', '', radius, textSize]
        self.nodeId += 1
        self.id += 1
        self.caixi_list.append(tmp)

        print(tmp)
        return tmp[0]

    def addCaipu(self, cai):
        tmp = [self.id, self.nodeId, self.projectId, '菜谱']
        self.nodeId += 1
        self.id += 1
        for p in self.caipu_property:
            if p in cai.keys():
                tmp.append(cai[p])
            else:
                tmp.append('')
        if '做法' in cai.keys():
            zuofa_tmp = ""
            for i in range(len(cai['做法'])):
                zuofa_tmp += str(i + 1) + '、'
                zuofa_tmp += cai['做法'][i]
                zuofa_tmp += '\\n'
            tmp.append(zuofa_tmp)
        else:
            tmp.append('')
        tmp.append('node')
        tmp.append('')
        tmp.append('')
        tmp.append(radius)
        tmp.append(textSize)
        self.caipu_list.append(tmp)

        print(tmp)
        return tmp[0]

    def addShicai(self, shicai):
        for name in shicai.keys():
            if name in self.shicai_map.keys():
                return self.shicai_map[name]
            tmp = [self.id, self.nodeId, self.projectId, '食材', name]
            for p in self.shicai_property:
                if p in shicai[name].keys():
                    tmp_str = shicai[name][p].replace('\n', '\\n')
                    tmp.append(tmp_str)
                else:
                    tmp.append('')
            tmp.append('node')
            tmp.append('')
            tmp.append('')
            tmp.append(radius)
            tmp.append(textSize)
            self.shicai_list.append(tmp)
            self.shicai_map[name] = tmp[0]
            self.nodeId += 1
            self.id += 1
            print(tmp)
            return tmp[0]

    def addBelong(self, src, tar):
        self.belong_list.append([src, self.projectId, self.relationId, '属于', tar, 'relation', 1])
        self.relationId += 1

    def addZhushicai(self, src, tar, shicai):
        tmp = [src, self.projectId, self.relationId, '主食材', tar, 'relation', 1]
        for name in shicai.keys():
            if '用量' in shicai[name].keys():
                tmp.append(shicai[name]['用量'])
            else:
                tmp.append("")
        self.zhushicai_list.append(tmp)
        self.relationId += 1

    def addFuliao(self, src, tar, shicai):
        tmp = [src, self.projectId, self.relationId, '辅料', tar, 'relation', 1]
        for name in shicai.keys():
            if '用量' in shicai[name].keys():
                tmp.append(shicai[name]['用量'])
            else:
                tmp.append("")
        self.fuliao_list.append(tmp)
        self.relationId += 1

    def view(self, path):
        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    print("===============================")
                    print(key)
                    print('有{}种菜'.format(len(caixi[key])))
                    for item in caixi[key]:
                        print(item['菜谱'])

    def load(self, path, projectId):
        self.reset_list()
        self.projectId = projectId
        self.nodeId = 1
        self.relationId = 1
        root = os.getcwd()
        root = root + '/export/'
        caixi_file = open(root + 'caixi{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        caipu_file = open(root + 'caipu{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        shicai_file = open(root + 'shicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        zhushicai_file = open(root + 'zhushicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        fuliao_file = open(root + 'fuliao{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        belong_file = open(root + 'belong{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')

        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    if key not in self.chosen_caixi:
                        continue
                    print('==================')
                    print(key)
                    caixi_id = self.addCaixi(key)
                    cnt = 0
                    for item in caixi[key]:
                        # 添加菜谱
                        cai = item['菜谱']
                        print('read...', end='')
                        print(cai)
                        cai_id = self.addCaipu(cai)
                        self.addBelong(cai_id, caixi_id)
                        # 添加食材
                        sc = item['食材']
                        if '主料' in sc.keys():
                            for shicai in sc['主料']:
                                shicai_id = self.addShicai(shicai)
                                self.addZhushicai(cai_id, shicai_id, shicai)
                        if '辅料' in sc.keys():
                            for shicai in sc['辅料']:
                                shicai_id = self.addShicai(shicai)
                                self.addFuliao(cai_id, shicai_id, shicai)
                        cnt += 1
                        if cnt > self.caixi_count:
                            break
            # break
        writer = csv.writer(caixi_file)
        writer.writerows(self.caixi_list)
        writer = csv.writer(caipu_file)
        writer.writerows(self.caipu_list)
        writer = csv.writer(shicai_file)
        writer.writerows(self.shicai_list)
        writer = csv.writer(zhushicai_file)
        writer.writerows(self.zhushicai_list)
        writer = csv.writer(fuliao_file)
        writer.writerows(self.fuliao_list)
        writer = csv.writer(belong_file)
        writer.writerows(self.belong_list)
        caixi_file.close()
        caipu_file.close()
        shicai_file.close()
        zhushicai_file.close()
        fuliao_file.close()
        belong_file.close()

    def load_mintai(self, path, projectId):
        self.reset_list()
        self.projectId = projectId
        self.nodeId = 1
        self.relationId = 1
        root = os.getcwd()
        root = root + '/export/'
        caixi_file = open(root + 'caixi{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        caipu_file = open(root + 'caipu{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        shicai_file = open(root + 'shicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        zhushicai_file = open(root + 'zhushicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        fuliao_file = open(root + 'fuliao{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        belong_file = open(root + 'belong{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')

        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    if key not in ['台湾美食', '闽菜']:
                        continue
                    print('==================')
                    print(key)
                    caixi_id = self.addCaixi(key)
                    cnt = 0
                    for item in caixi[key]:
                        # 添加菜谱
                        cai = item['菜谱']
                        print('read...', end='')
                        print(cai)
                        cai_id = self.addCaipu(cai)
                        self.addBelong(cai_id, caixi_id)
                        # 添加食材
                        sc = item['食材']
                        if '主料' in sc.keys():
                            for shicai in sc['主料']:
                                shicai_id = self.addShicai(shicai)
                                self.addZhushicai(cai_id, shicai_id, shicai)
                        if '辅料' in sc.keys():
                            for shicai in sc['辅料']:
                                shicai_id = self.addShicai(shicai)
                                self.addFuliao(cai_id, shicai_id, shicai)
                        cnt+=1
                        if cnt>50:
                            break

        writer = csv.writer(caixi_file)
        writer.writerows(self.caixi_list)
        writer = csv.writer(caipu_file)
        writer.writerows(self.caipu_list)
        writer = csv.writer(shicai_file)
        writer.writerows(self.shicai_list)
        writer = csv.writer(zhushicai_file)
        writer.writerows(self.zhushicai_list)
        writer = csv.writer(fuliao_file)
        writer.writerows(self.fuliao_list)
        writer = csv.writer(belong_file)
        writer.writerows(self.belong_list)
        caixi_file.close()
        caipu_file.close()
        shicai_file.close()
        zhushicai_file.close()
        fuliao_file.close()
        belong_file.close()

    def load_jiangzhehu(self, path, projectId):
        self.reset_list()
        self.projectId = projectId
        self.nodeId = 1
        self.relationId = 1
        root = os.getcwd()
        root = root + '/export/'
        caixi_file = open(root + 'caixi{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        caipu_file = open(root + 'caipu{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        shicai_file = open(root + 'shicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        zhushicai_file = open(root + 'zhushicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        fuliao_file = open(root + 'fuliao{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        belong_file = open(root + 'belong{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')

        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    if key not in ['苏菜', '浙菜', '淮扬菜']:
                        continue
                    print('==================')
                    print(key)
                    caixi_id = self.addCaixi(key)
                    cnt = 0
                    for item in caixi[key]:
                        # 添加菜谱
                        cai = item['菜谱']
                        print('read...', end='')
                        print(cai)
                        cai_id = self.addCaipu(cai)
                        self.addBelong(cai_id, caixi_id)
                        # 添加食材
                        sc = item['食材']
                        if '主料' in sc.keys():
                            for shicai in sc['主料']:
                                shicai_id = self.addShicai(shicai)
                                self.addZhushicai(cai_id, shicai_id, shicai)
                        if '辅料' in sc.keys():
                            for shicai in sc['辅料']:
                                shicai_id = self.addShicai(shicai)
                                self.addFuliao(cai_id, shicai_id, shicai)
                        cnt += 1
                        if cnt > 30:
                            break

        writer = csv.writer(caixi_file)
        writer.writerows(self.caixi_list)
        writer = csv.writer(caipu_file)
        writer.writerows(self.caipu_list)
        writer = csv.writer(shicai_file)
        writer.writerows(self.shicai_list)
        writer = csv.writer(zhushicai_file)
        writer.writerows(self.zhushicai_list)
        writer = csv.writer(fuliao_file)
        writer.writerows(self.fuliao_list)
        writer = csv.writer(belong_file)
        writer.writerows(self.belong_list)
        caixi_file.close()
        caipu_file.close()
        shicai_file.close()
        zhushicai_file.close()
        fuliao_file.close()
        belong_file.close()

    def load_gangao(self, path, projectId):
        self.reset_list()
        self.projectId = projectId
        self.nodeId = 1
        self.relationId = 1
        root = os.getcwd()
        root = root + '/export/'
        caixi_file = open(root + 'caixi{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        caipu_file = open(root + 'caipu{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        shicai_file = open(root + 'shicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        zhushicai_file = open(root + 'zhushicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        fuliao_file = open(root + 'fuliao{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        belong_file = open(root + 'belong{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')

        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    if key not in ['香港美食', '澳门美食']:
                        continue
                    print('==================')
                    print(key)
                    cnt = 0
                    caixi_id = self.addCaixi(key)
                    for item in caixi[key]:
                        # 添加菜谱
                        cai = item['菜谱']
                        print('read...', end='')
                        print(cai)
                        cai_id = self.addCaipu(cai)
                        self.addBelong(cai_id, caixi_id)
                        # 添加食材
                        sc = item['食材']
                        if '主料' in sc.keys():
                            for shicai in sc['主料']:
                                shicai_id = self.addShicai(shicai)
                                self.addZhushicai(cai_id, shicai_id, shicai)
                        if '辅料' in sc.keys():
                            for shicai in sc['辅料']:
                                shicai_id = self.addShicai(shicai)
                                self.addFuliao(cai_id, shicai_id, shicai)
                        cnt += 1
                        if cnt > 50:
                            break

        writer = csv.writer(caixi_file)
        writer.writerows(self.caixi_list)
        writer = csv.writer(caipu_file)
        writer.writerows(self.caipu_list)
        writer = csv.writer(shicai_file)
        writer.writerows(self.shicai_list)
        writer = csv.writer(zhushicai_file)
        writer.writerows(self.zhushicai_list)
        writer = csv.writer(fuliao_file)
        writer.writerows(self.fuliao_list)
        writer = csv.writer(belong_file)
        writer.writerows(self.belong_list)
        caixi_file.close()
        caipu_file.close()
        shicai_file.close()
        zhushicai_file.close()
        fuliao_file.close()
        belong_file.close()

    def load_mypick(self, path, projectId):
        self.reset_list()
        self.projectId = projectId
        self.nodeId = 1
        self.relationId = 1
        root = os.getcwd()
        root = root + '/export/'
        caixi_file = open(root + 'caixi{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        caipu_file = open(root + 'caipu{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        shicai_file = open(root + 'shicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        zhushicai_file = open(root + 'zhushicai{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        fuliao_file = open(root + 'fuliao{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')
        belong_file = open(root + 'belong{}.csv'.format(projectId), mode='w', encoding='utf8', newline='')

        with open(path, 'r', encoding='utf8') as fp:
            f = fp.read()
            json_data = json.loads(f)
            for caixi in json_data:
                for key in caixi.keys():
                    print('==================')
                    print(key)
                    caixi_id = self.addCaixi(key)
                    for item in caixi[key]:
                        # 添加菜谱
                        cai = item['菜谱']
                        if cai['名称'] in ['黄焖鸡', '台湾卤肉饭', '红烧狮子头', '咸水鸭', '海蛎煎']:
                            print('read...', end='')
                            print(cai)
                            cai_id = self.addCaipu(cai)
                            self.addBelong(cai_id, caixi_id)
                            # 添加食材
                            sc = item['食材']
                            if '主料' in sc.keys():
                                for shicai in sc['主料']:
                                    shicai_id = self.addShicai(shicai)
                                    self.addZhushicai(cai_id, shicai_id, shicai)
                            if '辅料' in sc.keys():
                                for shicai in sc['辅料']:
                                    shicai_id = self.addShicai(shicai)
                                    self.addFuliao(cai_id, shicai_id, shicai)
        writer = csv.writer(caixi_file)
        writer.writerows(self.caixi_list)
        writer = csv.writer(caipu_file)
        writer.writerows(self.caipu_list)
        writer = csv.writer(shicai_file)
        writer.writerows(self.shicai_list)
        writer = csv.writer(zhushicai_file)
        writer.writerows(self.zhushicai_list)
        writer = csv.writer(fuliao_file)
        writer.writerows(self.fuliao_list)
        writer = csv.writer(belong_file)
        writer.writerows(self.belong_list)
        caixi_file.close()
        caipu_file.close()
        shicai_file.close()
        zhushicai_file.close()
        fuliao_file.close()
        belong_file.close()

if __name__ == '__main__':
    dataLoader = DataLoader()
    path = 'D:\\JavaSpace\\SECIII\\result_final.json'
    dataLoader.load(path, 5)
    dataLoader.load_mintai(path, 4)
    dataLoader.load_jiangzhehu(path, 3)
    dataLoader.load_gangao(path, 128)
    dataLoader.load_mypick(path, 133)
    # dataLoader.view('D:\\JavaSpace\\SECIII\\result_final.json')
