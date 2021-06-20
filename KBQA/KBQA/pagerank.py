from py2neo import Graph


class PageRank:
    def __init__(self, uri="bolt://39.97.124.144:7687", user="neo4j", password="123456"):
        self.graph = Graph(uri, auth=(user, password))

    def rank(self, project_id, topN=5):
        id_str = str(project_id)
        exist = 'CALL gds.graph.exists("myGraph{}")'.format(project_id)
        drop = 'CALL gds.graph.drop("myGraph{}")'.format(project_id)
        create = "CALL gds.graph.create.cypher(\"myGraph" + id_str + "\"," + \
                 "\"MATCH (n:node{projectId:" + id_str + "}) RETURN id(n) AS id\"," + \
                 "\"MATCH (a:node{projectId:" + id_str + "})-[:relation]->" + \
                 "(b:node{projectId:" + id_str + "}) RETURN id(a) AS source, id(b) AS target\"" + \
                 ") YIELD graphName, nodeCount, relationshipCount, createMillis;"
        rank = "CALL gds.pageRank.stream(\"myGraph" + id_str + "\") " + \
               "YIELD nodeId,score " + \
               "RETURN gds.util.asNode(nodeId).name AS name,gds.util.asNode(nodeId).group AS group,nodeId,score " + \
               "ORDER BY group DESC, score DESC, name ASC"
        res = self.graph.run(exist)
        for record in res:
            if record[1]:
                self.graph.run(drop)
        self.graph.run(create)
        res = self.graph.run(rank)
        tmp = {}
        for record in res:
            if record[1] not in tmp.keys():
                tmp[record[1]] = []
            if len(tmp[record[1]]) < topN:
                tmp[record[1]].append({
                    'rank': len(tmp[record[1]])+1,
                    'name': record[0],
                    'nodeId': record[2],
                    'score': record[3],
                })
            # print(record)
        rank_result = []
        for key in tmp.keys():
            rank_result.append({
                'group': key,
                'items': tmp[key]
            })
        # print(rank_result)
        self.graph.run(drop)
        return rank_result


if __name__ == '__main__':
    pr = PageRank()
    pr.rank(5)
