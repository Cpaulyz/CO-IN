from py2neo import Graph


class Database(object):
    def __init__(self, uri="bolt://39.97.124.144:7687", user="neo4j", password="123456"):
        self.graph = Graph(uri, auth=(user, password))

    # def __del__(self):
    #     self._driver.close()
    #     print("Driver has been closed.")

    def query(self, query):
        """
        Return:
            result of the query, one record per iteration
        """
        res = self.graph.run(query)
        return res


if __name__ == "__main__":
    # test connection
    driver = Database()
    result = driver.query("match (n) return n limit 10")
    for i in result:
        print(i)
