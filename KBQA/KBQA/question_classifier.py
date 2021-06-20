import ahocorasick
import random

from KBQA.dict_construct import *


class QuestionClassifier:
    dc = DictConstructor()

    def __init__(self, project_id):

        self.project_id = project_id

        # 　特征词路径
        root = os.getcwd()
        path = root + '/dict/{}/'.format(project_id)
        # if not os.path.exists(path):
        self.dc.construct(project_id)
        # root = "./KBQA"
        self.recipe_path = (path + 'recipe.txt')
        self.gongyi_path = (path + 'gongyi.txt')
        self.haoshi_path = (path + 'haoshi.txt')
        self.kouwei_path = (path + 'kouwei.txt')
        self.leixing_path = (path + 'leixing.txt')
        # self.yongliang_path = (path + 'yongliang.txt')
        self.caixi_path = (path + 'caixi.txt')
        self.material_path = (path + 'material.txt')
        # self.deny_path = (path + 'deny.txt')
        # 加载特征词
        self.recipe_wds = [i.strip() for i in open(self.recipe_path, encoding='utf-8') if i.strip()]
        self.gongyi_wds = [i.strip() for i in open(self.gongyi_path, encoding='utf-8') if i.strip()]
        self.haoshi_wds = [i.strip() for i in open(self.haoshi_path, encoding='utf-8') if i.strip()]
        self.kouwei_wds = [i.strip() for i in open(self.kouwei_path, encoding='utf-8') if i.strip()]
        # self.yongliang_wds = [i.strip() for i in open(self.yongliang_path, encoding='utf-8') if i.strip()]
        self.caixi_wds = [i.strip() for i in open(self.caixi_path, encoding='utf-8') if i.strip()]
        # self.caixi_wds = ['鲁菜', '川菜', '粤菜', '苏菜', '闽菜', '浙菜', '湘菜', '徽菜']
        self.material_wds = [i.strip() for i in open(self.material_path, encoding='utf-8') if i.strip()]
        # self.deny_words = [i.strip() for i in open(self.deny_path, encoding='utf-8') if i.strip()]
        self.keep_wds = ['菜系']  # 保留词
        self.region_words = set(
            self.recipe_wds + self.gongyi_wds + self.haoshi_wds + self.kouwei_wds + self.caixi_wds +
            self.material_wds + self.keep_wds)

        # 构造领域actree
        self.region_tree = self.build_actree(list(self.region_words))
        # 构建词典
        self.wdtype_dict = self.build_wdtype_dict()
        # 问句疑问词
        self.zuofa_qwds = ['咋做', '怎做', '做法', '怎么做']  # 做法
        self.kouwei_qwds = ['口味', '味道']  # 口味
        self.gongyi_qwds = ['工艺']  # 工艺
        self.haoshi_qwds = ['多久', '耗时']  # 耗时
        self.property_qwds = set(
            self.zuofa_qwds + self.kouwei_qwds + self.gongyi_qwds + self.haoshi_qwds)

        self.ingredient_qwds = ['食材', '主要由', '主要有', '由什么做', '要多少', '用料', '要用多少', '有什么', '用什么', '有哪些', '用了多少', '用多少']
        self.ingredient_prime = ['主食材', '主料']
        self.ingredient_sub = ['辅料', '辅食材']
        self.ingredient_qwds += (self.ingredient_prime + self.ingredient_sub)
        self.belong_qwds = ['所属', '属于', '哪里', '菜系', '什么菜']
        self.include_qwds = ['有哪些', '有什么', '哪些', '有什么']
        self.recommend_qwds = ['推荐']

        print(str(project_id) + ' model init finished ......')

        return

    def classify(self, question):
        """
        分类主函数
        """
        data = {}
        ques_recipe_dict = self.check_medical(question)

        print("recipe_dict : ", ques_recipe_dict)
        if not ques_recipe_dict:
            data['question_types'] = ['unknown']
            return data
        data['args'] = ques_recipe_dict
        # 收集问句当中所涉及到的实体类型
        types = []
        for type_ in ques_recipe_dict.values():
            types += type_
        question_type = ''
        question_types = []

        # 查询菜谱属性
        if self.check_words(self.property_qwds, question) and ('recipe' in types):
            if self.check_words(self.zuofa_qwds, question):
                question_type = 'food_query_zuofa'
                question_types.append(question_type)
            if self.check_words(self.kouwei_qwds, question):
                question_type = 'food_query_kouwei'
                question_types.append(question_type)
            if self.check_words(self.gongyi_qwds, question):
                question_type = 'food_query_gongyi'
                question_types.append(question_type)
            if self.check_words(self.haoshi_qwds, question):
                question_type = 'food_query_haoshi'
                question_types.append(question_type)
        # 查询符合约束的菜
        if 'recipe' not in types and self.check_all_property(types) and self.check_words(self.include_qwds, question):
            question_type = 'food_constraint'
            question_types.append(question_type)
        # 某个菜的主食材/辅料
        if 'recipe' in types and 'material' not in types and self.check_words(self.ingredient_qwds, question):
            question_type = 'ingredients'
            if self.check_words(self.ingredient_prime, question):
                question_type = 'ingredients_prime'
            elif self.check_words(self.ingredient_sub, question):
                question_type = 'ingredients_sub'
            question_types.append(question_type)
        # 查询所需食材的量& 某个菜是否用了某个东西
        if 'recipe' in types and 'material' in types and not self.check_words(self.property_qwds, question):
            if self.check_words(self.ingredient_qwds, question):
                # 查询所需食材的量
                question_type = 'ingredients_num'
            else:
                # 某个菜是否用了某个东西
                question_type = 'ingredients_include'
            question_types.append(question_type)
        # 查询菜的菜系
        if '菜系' not in types and 'recipe' in types and self.check_words(self.belong_qwds, question):
            question_type = 'food_belong'
            question_types.append(question_type)
        # 查询菜系有哪些菜
        if '菜系' in types and self.check_words(self.include_qwds, question):
            question_type = 'cuisine_food'
            question_types.append(question_type)
        if 'keep' in types and self.check_words(self.include_qwds, question):
            question_type = 'cuisine_query'
            question_types.append(question_type)
        if len(question_types) == 0:
            if '菜系' in types:
                question_type = 'desc_cuisine'
            elif 'recipe' in types:
                question_type = 'desc_recipe'
            elif 'material' in types:
                question_type = 'desc_material'
            else:
                question_type = 'unknown'
            question_types.append(question_type)
        # 将多个分类结果进行合并处理，组装成一个字典
        data['question_types'] = question_types

        return data

    def build_wdtype_dict(self):
        """
        构造词对应的类型
        """
        wd_dict = dict()
        for wd in self.region_words:
            wd_dict[wd] = []
            if wd in self.recipe_wds:
                wd_dict[wd].append('recipe')
            elif wd in self.gongyi_wds:
                wd_dict[wd].append('工艺')
            elif wd in self.haoshi_wds:
                wd_dict[wd].append('耗时')
            elif wd in self.kouwei_wds:
                wd_dict[wd].append('口味')
            elif wd in self.caixi_wds:
                wd_dict[wd].append('菜系')
            elif wd in self.material_wds:
                wd_dict[wd].append('material')
            elif wd in self.keep_wds:
                wd_dict[wd].append('keep')
        return wd_dict

    def build_actree(self, wordlist):
        """
        构造actree，加速过滤
        """
        actree = ahocorasick.Automaton()  # 初始化trie树
        for index, word in enumerate(wordlist):
            actree.add_word(word, (index, word))  # 向trie树中添加单词
        actree.make_automaton()  # 将trie树转化为Aho-Corasick自动机
        return actree

    def check_medical(self, question):
        """
        问句过滤
        """
        region_wds = []
        for i in self.region_tree.iter(question):
            wd = i[1][1]  # 匹配到的词
            region_wds.append(wd)
        stop_wds = []
        for wd1 in region_wds:
            for wd2 in region_wds:
                if wd1 in wd2 and wd1 != wd2:
                    stop_wds.append(wd1)
        final_wds = [i for i in region_wds if i not in stop_wds]
        final_dict = {i: self.wdtype_dict.get(i) for i in final_wds}
        return final_dict

    def check_words(self, wds, sent):
        """
        检查句子里是否含有某个单词
        """
        #
        for wd in wds:
            if wd in sent:
                return True
        return False

    def check_all_property(self, types):
        """
        检查是否是某种属性
        """
        for i in types:
            if not (i == '工艺' or i == '耗时' or i == '口味'):
                return False
        return True

    def generate_hot_question(self):
        """
        生产热门查询
        1. x菜有哪些美食
        2. xx怎么做
        3. xx的食材有哪些
        4. xx属于什么菜系
        5. 介绍一下xxx
        6. xxx的菜有哪些
        7. xxx要做多久
        :return: list of str
        """
        res = []
        cai_q = ["{}怎么做", "{}的食材有哪些", "{}要用什么辅料", "{}有哪些主食材", "{}属于什么菜系", "{}要做多久"]
        try_time = 10  # 重试的次数
        index = random.randint(0, len(self.caixi_wds)-1)
        res.append("{0}有哪些美食".format(self.caixi_wds[index]))
        for i in range(len(cai_q)):
            for time in range(try_time):
                index = random.randint(0, len(self.recipe_wds)-1)
                if len(self.recipe_wds[index]) < 5:
                    res.append(cai_q[i].format(self.recipe_wds[index]))
                    break

        index = random.randint(0, len(self.kouwei_wds)-1)
        res.append("{}的菜有哪些".format(self.kouwei_wds[index]))
        index = random.randint(0, len(self.material_wds)-1)
        res.append("介绍一下{}".format(self.material_wds[index]))
        return res


    def recognized(self,sentence):
        tmp = self.check_medical(sentence)
        print(tmp)


if __name__ == '__main__':
    handler = QuestionClassifier(5)
    while 1:
        question = input('input an question:')
        # data = handler.classify(question)
        # print(data)
        handler.recognized(question)
