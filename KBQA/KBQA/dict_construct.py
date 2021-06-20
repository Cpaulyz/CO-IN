import os
import shutil

from py2neo import Graph


class DictConstructor:
    def __init__(self, uri="bolt://39.97.124.144:7687", user="neo4j", password="123456"):
        self.graph = Graph(uri, auth=(user, password))

    def construct(self, project_id):
        root = os.getcwd()
        path = root + '/dict/{}/'.format(project_id)
        print(path)
        if os.path.exists(path):
            shutil.rmtree(path)
        os.makedirs(path)
        self.save_cuisine(project_id, path)
        self.save_material(project_id, path)
        self.save_recipe(project_id, path)

    def save_material(self, project_id, path):
        filename = "material.txt"
        cql = "MATCH (m:node) where m.projectId={0} and m.group='食材' return m".format(project_id)
        answer = self.graph.run(cql)
        with open(path + filename, "w", encoding='utf-8') as f:
            for record in answer:
                for node in record:
                    f.write(str(node['name']) + '\n')

    def save_cuisine(self, project_id, path):
        filename = "caixi.txt"
        cql = "MATCH (m:node) where m.projectId={0} and m.group='菜系' return m".format(project_id)
        answer = self.graph.run(cql)
        with open(path + filename, "w", encoding='utf-8') as f:
            for record in answer:
                for node in record:
                    f.write(str(node['name']) + '\n')

    def save_recipe(self, project_id, path):
        recipe_filename = "recipe.txt"
        kouwei_filename = "kouwei.txt"
        gongyi_filename = "gongyi.txt"
        haoshi_filename = "haoshi.txt"
        cql = "MATCH (m:node) where m.projectId={0} and m.group='菜谱' return m".format(project_id)
        answer = self.graph.run(cql)
        f_recipe = open(path + recipe_filename, "w", encoding='utf-8')
        f_kouwei = open(path + kouwei_filename, "w", encoding='utf-8')
        f_gongyi = open(path + gongyi_filename, "w", encoding='utf-8')
        f_haoshi = open(path + haoshi_filename, "w", encoding='utf-8')
        for record in answer:
            for node in record:
                f_recipe.write(node['name'] + '\n')
                if node['properties.口味'] is not None:
                    f_kouwei.write(node['properties.口味'] + '\n')
                if node['properties.工艺'] is not None:
                    f_gongyi.write(node['properties.工艺'] + '\n')
                if node['properties.耗时'] is not None:
                    f_haoshi.write(node['properties.耗时'] + '\n')
        f_recipe.close()
        f_kouwei.close()
        f_gongyi.close()
        f_haoshi.close()


if __name__ == '__main__':
    dc = DictConstructor()
    dc.construct(235)
