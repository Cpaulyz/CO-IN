class QuestionParser:

    @staticmethod
    def build_entitydict(args):
        """
        构建实体节点
        :param args:
        :return:
        """
        entity_dict = {}
        for arg, types in args.items():
            for type in types:
                if type not in entity_dict:
                    entity_dict[type] = [arg]
                else:
                    entity_dict[type].append(arg)
        print(entity_dict)
        return entity_dict

    @staticmethod
    def parser_main(res_classify, project_id):
        """
        解析主函数
        :param project_id:
        :param res_classify:
        :return:
        """
        args = res_classify['args']
        entity_dict = QuestionParser.build_entitydict(args)
        question_types = res_classify['question_types']
        sqls = []
        for question_type in question_types:
            tmp_dict = {}
            if str(question_type).startswith("food_query"):
                tmp_dict['recipe'] = entity_dict.get('recipe')
            elif question_type == "food_constraint":
                tmp_dict['工艺'] = entity_dict.get('工艺')
                tmp_dict['口味'] = entity_dict.get('口味')
                tmp_dict['耗时'] = entity_dict.get('耗时')
            elif question_type == 'ingredients' or question_type == 'ingredients_prime' or question_type == 'ingredients_sub':
                tmp_dict['recipe'] = entity_dict.get('recipe')
            elif question_type == "ingredients_num":
                tmp_dict['recipe'] = entity_dict.get('recipe')
                tmp_dict['material'] = entity_dict.get('material')
            elif question_type == "ingredients_include":
                tmp_dict['recipe'] = entity_dict.get('recipe')
                tmp_dict['material'] = entity_dict.get('material')
            elif question_type == "food_belong":
                tmp_dict['recipe'] = entity_dict.get('recipe')
            elif question_type == "cuisine_food":
                tmp_dict['菜系'] = entity_dict.get('菜系')
            elif question_type == "desc_cuisine":
                tmp_dict['菜系'] = entity_dict.get('菜系')
            elif question_type == "desc_recipe":
                tmp_dict['recipe'] = entity_dict.get('recipe')
            elif question_type == "desc_material":
                tmp_dict['material'] = entity_dict.get('material')
            elif question_type == 'cuisine_query':
                tmp_dict['keep'] = entity_dict.get('keep')
            elif question_type == "unknown":
                pass
            sql = QuestionParser.sql_transfer(question_type, tmp_dict, project_id)
            sqls += sql
        return {'question_type': question_type, 'sql': sqls}

    @staticmethod
    def sql_transfer(question_type, entities_dict, project_id):
        if not entities_dict or question_type == 'unknown':
            return []
        print("sql_transfer ", end='')
        print(entities_dict)
        # 查询语句
        sql = []
        # 查询菜谱做法
        if question_type == 'food_query_zuofa':
            sql = [
                "MATCH (m:node) where m.name = '{0}' and m.projectId={1} return m".format(i, project_id)
                for i in entities_dict['recipe']]
        # 查询菜谱口味
        elif question_type == 'food_query_kouwei':
            sql = [
                "MATCH (m:node) where m.name = '{0}' and m.projectId={1} return m".format(i, project_id)
                for i in entities_dict['recipe']]
        # 查询菜谱工艺
        elif question_type == 'food_query_gongyi':
            sql = [
                "MATCH (m:node) where m.name = '{0}' and m.projectId={1} return m".format(i, project_id)
                for i in entities_dict['recipe']]
        # 查询菜谱耗时
        elif question_type == 'food_query_haoshi':
            sql = [
                "MATCH (m:node) where m.name = '{0}' and m.projectId={1} return m".format(i, project_id)
                for i in entities_dict['recipe']]
        # 查询符合约束的菜
        elif question_type == 'food_constraint':
            tmp = []
            if entities_dict['工艺'] is not None:
                tmp.append("m.`properties.工艺`='{0}'".format(entities_dict['工艺'][0]))
            if entities_dict['口味'] is not None:
                tmp.append("m.`properties.口味`='{0}'".format(entities_dict['口味'][0]))
            if entities_dict['耗时'] is not None:
                tmp.append("m.`properties.耗时`='{0}'".format(entities_dict['耗时'][0]))
            sql = [
                "MATCH (m:node) where m.projectId={0} and ".format(project_id) + " and ".join(tmp) + " return m"]
        # 食材查询
        elif question_type == 'ingredients':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where n.name='{0}' and m.projectId={1} and (r.name='主食材' or r.name='辅料') return m, n".format(
                    i, project_id)
                for i in entities_dict['recipe']]
        # 主食材查询
        elif question_type == 'ingredients_prime':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where n.name='{0}' and m.projectId={1} and r.name='主食材' return m, n".format(
                    i, project_id)
                for i in entities_dict['recipe']]
        # 辅料查询
        elif question_type == 'ingredients_sub':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where n.name='{0}' and m.projectId={1} and r.name='辅料' return m, n".format(
                    i, project_id)
                for i in entities_dict['recipe']]
        # 查询所需食材的量
        elif question_type == 'ingredients_num':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where m.name='{0}' and m.projectId={1} and n.name='{2}' return m,r,n".format(
                    i, project_id, j)
                for i in entities_dict['recipe'] for j in entities_dict['material']
            ]
        # 某个菜是否用了某个东西
        elif question_type == 'ingredients_include':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where m.name='{0}' and m.projectId={1} and n.name='{2}' return m,r,n".format(
                    i, project_id, j)
                for i in entities_dict['recipe'] for j in entities_dict['material']]
        # 查询菜的菜系
        elif question_type == 'food_belong':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where n.name='{0}' and m.projectId={1} and r.name='属于'  return m, n".format(
                    i, project_id)
                for i in entities_dict['recipe']]
        # 查询菜系有哪些菜 && 描述菜系
        elif question_type == 'cuisine_food' or question_type == 'desc_cuisine':
            sql = [
                "MATCH (m:node)-[r:relation]-(n:node) where n.name='{0}' and m.projectId={1} and r.name='属于'  return m, n".format(
                    i, project_id)
                for i in entities_dict['菜系']]
        # 描述菜谱
        elif question_type == 'desc_recipe':
            sql = [
                "MATCH (m:node) where m.group='菜谱' and m.name='{0}' and m.projectId={1} return m".format(
                    i, project_id)
                for i in entities_dict['recipe']]
        # 描述食材
        elif question_type == 'desc_material':
            sql = [
                "MATCH (m:node) where m.group='食材' and m.name='{0}' and m.projectId={1} return m".format(
                    i, project_id)
                for i in entities_dict['material']]
        # 查询菜系
        elif question_type == 'cuisine_query':
            sql = ["MATCH (m:node) where m.group='菜系' and m.projectId={0} return m".format(project_id)]
        return sql
