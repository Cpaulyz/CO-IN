import os
import sys

from flask import Flask, jsonify

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
sys.path.append(rootPath)

from KBQA.chatbot_graph import *
from KBQA.pagerank import *

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False

question_instance = {}  # 每一个project对应一个instance
pageRank = PageRank()


def text_response(text, success, nodes=[]):
    tmp = {
        'text': text,
        'nodes': nodes,
        'success': success
    }
    return jsonify(tmp)


# 提问
@app.route('/question/<project_id>/<question>', methods=['GET'])
def kbqa(project_id, question):
    if project_id not in question_instance.keys():
        res = text_response('小助手尚未初始化', False)
    else:
        bot = question_instance[project_id]
        tmp_res = bot.chat_main(question)
        nodes = []
        ans = ""
        if 'answer' in tmp_res.keys():
            ans = tmp_res['answer']
        if 'nodes' in tmp_res.keys():
            nodes = tmp_res['nodes']
        res = text_response(ans, True, nodes)
    print(res)
    return res


# 初始化项目
@app.route('/question/init/<project_id>', methods=['GET'])
def init_project(project_id):
    try:
        question_instance[project_id] = ChatBotGraph(project_id)
        print(text_response('初始化成功', True))
        return text_response('初始化成功', True)
    except:
        print(text_response('初始化失败，未知错误', False))
        return text_response('初始化失败，未知错误', False)


# 验证项目是否初始化
@app.route('/question/verify/<project_id>', methods=['GET'])
def verify_project(project_id):
    if project_id not in question_instance.keys():
        res = text_response('', False)
    else:
        res = text_response('', True)
    return res


# 热门查询
@app.route('/question/hot/<project_id>', methods=['GET'])
def hot_question(project_id):
    if project_id not in question_instance.keys():
        res = []
    else:
        bot = question_instance[project_id]
        res = bot.hot_question()
    print(res)
    return jsonify(res)


# 中心识别
@app.route('/centrality/<project_id>', methods=['GET'])
def centrality(project_id):
    res = pageRank.rank(project_id)
    return jsonify(res)


# 心跳
@app.route('/health', methods=['GET'])
def health():
    return jsonify(True)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
