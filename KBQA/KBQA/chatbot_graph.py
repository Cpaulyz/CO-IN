from KBQA.answer_search import *
from KBQA.question_classifier import *
from KBQA.question_parser import *

'''问答类'''


class ChatBotGraph:
    parser = QuestionParser()
    searcher = AnswerSearcher()

    def __init__(self, project_id):
        self.classifier = QuestionClassifier(project_id)
        self.project_id = project_id

    def chat_main(self, sent):
        res = {'answer': "", 'nodes': []}
        try:
            res_classify = self.classifier.classify(sent)
            print(res_classify)
            if not res_classify or 'unknown' in res_classify['question_types']:
                res['answer'] = '抱歉，小助手暂时无法回答您的问题。'
                return res
            res_sql = self.parser.parser_main(res_classify, self.project_id)
            print(res_sql)
            final_answers = self.searcher.search_main(res_sql)
            if not final_answers:
                res['answer'] = '抱歉，小助手暂时无法回答您的问题。'
                return res
            else:
                return final_answers
        except:
            res['answer'] = '异常错误。'
            return res

    def hot_question(self):
        return self.classifier.generate_hot_question()


if __name__ == '__main__':
    handler = ChatBotGraph(5)
    while 1:
        question = input('用户:')
        answer = handler.chat_main(question)
        print('小助手:', answer)
