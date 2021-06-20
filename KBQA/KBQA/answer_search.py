from py2neo import Graph


class AnswerSearcher(object):
    def __init__(self, uri="bolt://39.97.124.144:7687", user="neo4j", password="123456"):
        self.graph = Graph(uri, auth=(user, password))
        self.recipe_properties_list = {
            '做法': 'properties.做法',
            '口味': 'properties.口味',
            '工艺': 'properties.工艺',
            '耗时': 'properties.耗时'
        }
        self.material_properties_list = {
            '简介': 'properties.简介',
            '功效': 'properties.功效',
            '营养价值': 'properties.营养价值'
        }

    def search_main(self, dict):
        """
        执行cypher查询，并返回相应结果和相关的nodeId
        """
        answers = []
        nodes = []
        question_type = dict['question_type']
        sqls = dict['sql']
        for sql_ in sqls:
            answer = self.graph.run(sql_)
            print('answer:' + str(answer))
            pretty_answer, tmp_nodes = self.answer_prettify(question_type, answer)
            answers.append(pretty_answer)
            nodes += tmp_nodes
        res = {
            'answer': '\\n'.join(answers),
            'nodes': nodes
        }
        return res

    def answer_prettify(self, question_type, answer):
        """
        将查询结果转为自然语言
        @:returns 0:答案 1:list[{'src': ?, 'target': ?},...]
        """
        res = {}
        if not answer:
            res['desc'] = ''
            res['nodes'] = []
            return res
        nodes = []
        desc = ''
        # 查询菜谱做法：___的做法是____
        if question_type == 'food_query_zuofa':
            for record in answer:
                for node in record:
                    desc += str(node['name']) + '的做法是：\\n' + str(node[self.recipe_properties_list['做法']])
                    nodes.append({'src': node['nodeId']})
        # 查询菜谱口味：___的口味是____
        elif question_type == 'food_query_kouwei':
            for record in answer:
                for node in record:
                    desc += str(node['name']) + '的口味是' + str(node[self.recipe_properties_list['口味']])
                    nodes.append({'src': node['nodeId']})
        # 查询菜谱工艺：___的工艺是____
        elif question_type == 'food_query_gongyi':
            for record in answer:
                for node in record:
                    desc += str(node['name']) + '的工艺是' + str(node[self.recipe_properties_list['工艺']])
                    nodes.append({'src': node['nodeId']})
        # 查询菜谱耗时：做___需要____
        elif question_type == 'food_query_haoshi':
            for record in answer:
                for node in record:
                    desc += '做' + str(node['name']) + '需要' + str(node[self.recipe_properties_list['耗时']])
                    nodes.append({'src': node['nodeId']})
        # 查询食材/主食材/辅料
        # 1. ___的食材/主食材/辅料有___
        # 2. 我不知道___的食材/主食材/辅料有哪些
        elif question_type == 'ingredients' or question_type == 'ingredients_prime' or question_type == 'ingredients_sub':
            if question_type == 'ingredients':
                query_type = '食材'
            elif question_type == 'ingredients_prime':
                query_type = '主食材'
            elif question_type == 'ingredients_sub':
                query_type = '辅料'
            recipe = ""
            tmp = []
            for record in answer:
                tmp.append(record[0]['name'])
                recipe = record[1]['name']
                nodes.append({'src': record[1]['nodeId'], 'target': record[0]['nodeId']})
            if len(tmp) == 0:
                desc = '我不知道' + str(recipe) + '的' + query_type + '有哪些'
            else:
                desc = str(recipe) + '的' + query_type + '有'
                desc += "、".join(tmp)
        # 查询所需食材的量
        # 1. ___需要____的____
        # 2. ___需要用到____，但小助手不知道具体的需要多少
        elif question_type == 'ingredients_num':
            tmp = []
            for record in answer:
                n = {}
                index = 0
                for node in record:
                    if index == 0:
                        n['src'] = node['nodeId']
                        tmp.append(str(node['name']))
                    elif index == 1:
                        tmp.append(node['properties.用量'])
                    elif index == 2:
                        n['target'] = [node['nodeId']]
                        tmp.append(str(node['name']))
                    index += 1
                nodes.append(n)
                if tmp[1] is None:
                    desc = tmp[0] + '需要用到' + tmp[2] + '，但小助手不知道具体的需要多少'
                else:
                    desc = tmp[0] + '需要' + str(tmp[1]) + '的' + tmp[2]
        # 某个菜是否用了某个东西
        # 1. ___需要____的____
        # 2. ___需要用到____，但小助手不知道具体的需要多少
        # 3. 没有用到这种食材
        elif question_type == 'ingredients_include':
            tmp = []
            for record in answer:
                n = {}
                index = 0
                for node in record:
                    if index == 0:
                        n['src'] = node['nodeId']
                        tmp.append(str(node['name']))
                    elif index == 1:
                        tmp.append(node['properties.用量'])
                    elif index == 2:
                        n['target'] = [node['nodeId']]
                        tmp.append(str(node['name']))
                    index += 1
                nodes.append(n)
                if tmp[1] is None:
                    desc = tmp[0] + '需要用到' + tmp[2] + ',但小助手不知道具体的需要多少'
                else:
                    desc = tmp[0] + '需要' + str(tmp[1]) + '的' + tmp[2]
            if len(desc) == 0:
                desc = '没有用到这种食材'
        # 查询菜的菜系：___属于_____
        elif question_type == 'food_belong':
            recipe = ""
            tmp = []
            for record in answer:
                tmp.append(record[0]['name'])
                recipe = record[1]['name']
                nodes.append({'src': record[1]['nodeId'], 'target': record[0]['nodeId']})
            if len(tmp) != 0:
                desc = str(recipe) + '属于' + "、".join(tmp)
        # 查询菜系有哪些菜：___有___、____
        elif question_type == 'cuisine_food' or question_type == 'desc_cuisine':
            cuisine = ""
            tmp = []
            for record in answer:
                tmp.append(record[0]['name'])
                cuisine = record[1]['name']
                nodes.append({'src': record[1]['nodeId'], 'target': record[0]['nodeId']})
            if len(tmp) != 0:
                desc = str(cuisine) + '有' + "、".join(tmp)
        # 描述菜谱
        elif question_type == 'desc_recipe':
            for record in answer:
                for node in record:  # node是一条条记录
                    nodes.append({'src': node['nodeId']})
                    desc = '小助手将为你介绍' + str(node['name']) + '\\n\\n'
                    for key in self.recipe_properties_list.keys():  # 遍历查，如果没有就不介绍
                        if node[self.recipe_properties_list[key]] is not None:
                            if key =='做法':
                                desc += str(key) + ':\\n' + str(node[self.recipe_properties_list[key]]) + '\\n\\n'
                            else:
                                desc += str(key) + ':' + str(node[self.recipe_properties_list[key]]) + '\\n\\n'
        # 描述食材
        elif question_type == 'desc_material':
            for record in answer:
                for node in record:  # node是一条条记录
                    nodes.append({'src': node['nodeId']})
                    desc = '小助手将为你介绍' + str(node['name']) + '\\n\\n'
                    for key in self.material_properties_list.keys():  # 遍历查，如果没有就不介绍
                        if node[self.material_properties_list[key]] is not None:
                            desc += str(key) + ':\\n' + str(node[self.material_properties_list[key]]) + '\\n\\n'
        elif question_type == "food_constraint":
            tmp = []
            for record in answer:
                tmp.append(record[0]['name'])
                nodes.append({'src': record[0]['nodeId']})
            if len(tmp) > 0:
                desc = "有" + "、".join(tmp)
            else:
                desc = "小助手找不到符合条件的菜 :("
        # 查询菜系
        elif question_type == 'cuisine_query':
            tmp = []
            for record in answer:
                for node in record:  # node是一条条记录
                    nodes.append({'src': node['nodeId']})
                    tmp.append(node['name'])
            if len(tmp) > 0:
                desc = '本菜谱有以下菜系：' + "、".join(tmp)
            else:
                desc = '暂时找不到菜系:('
        print('nodes: ', end='')
        print(nodes)
        return desc, nodes
