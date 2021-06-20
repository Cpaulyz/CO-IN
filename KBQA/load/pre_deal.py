# 用于数据预处理、清洗数据
import json

import re

new_fp = open('D:\\JavaSpace\\SECIII\\result_final.json', 'w', encoding='utf8')
keep_word = ['三道工序轻松做人气美食', '川菜', '解密舌尖美味', '鲁菜', '川菜经典荤菜', '私房菜', '怡汝私房节日宴客菜',
             '飞禽走兽篇', '年夜饭吉利压轴大菜', '让王源落泪的美食', '闽菜', '冬季养生菜', '闽南', '粤菜', '无油版',
             '百姓家的冬季养生菜', '扬州', '苏菜', '江苏', '北京', '浙菜', '湘菜', '徽菜', '甜蜜的湖南', '预热年菜',
             '家宴上的一道主打菜', '改变', '鱼类', '粤菜', '北京', '预热年菜', '不加一滴酱油，炒出漂亮光泽', '皖北土味儿',
             '微波烹饪精典', '山东', '满口香', '辽宁', '正宗山西面食', '面食之都的美食~正宗山西面食', '山西暖胃烫面', '山西',
             '山西面食之粗粮风暴再袭来', '山西丫头我教大家传统面芸芸小厨', '穿上衣服的面条，山西特色面食', '湖北', '芸芸小厨', '云南',
             '食欲大增滴开胃菜', '传统北京风味菜', '炸酱面，花样做', '节庆家宴小炒', '夏日小凉菜', '东北', '飞禽走兽',
             '陕西', '本帮小凉菜', '上海', '四叶小馆', '解密舌尖美味', '新疆', '宁夏', '每日新品', '高压锅版', '假期零食', '台湾',
             '番茄配方', '麦兜妈妈滴经典菜', '发评论，赢海尔智能烤箱试用报告二', '快手早餐', '简单步骤做出传统面条', '春天的故事',
             '江西', '山西丫头我教大家传统面', '附让牛肉嫩嫩的小秘密', '烤箱版', '梅州']  # 【】里不是食谱的保留词
real_word = ['肉', '菜', '鱼', '鸡', '虾', '瓜', '猪', '豆']
filter = ['――“', '”', ';']


def deal_name(name: str):
    quote = re.findall(r"【(.+?)】", name) + re.findall(r"{(.+?)}", name) + re.findall(r"\[(.+?)\]", name) + \
            re.findall(r"《(.+?)》", name) + re.findall(r"〖(.+?)〗", name)
    # print(quote)
    res = ""
    if len(quote) > 0 and quote[0] not in keep_word:
        quote = quote[0]
        res = quote
        print(quote)
    else:
        words = re.split('【|】|\[|\]|-|—|{|}|《|》|·|、|，|（|）|｝|#', name)
        tmp = re.findall(r"(.+?)-+(.+?)", name) + re.findall(r"(.+?)—+(.+?)", name)
        # print(tmp)
        # print(words)
        reverse_word = reversed(words)
        for i in reverse_word:
            if i not in quote and i != '' and (i in real_word or '的' not in i):
                res = i
                print(i)
                break
        # print(tmp)
    print('===================================================')
    return res


def removePunctuation(text):
    punctuation = '!,;:?"\'、，；―“”：【】[]{}()（）'
    text = re.sub(r'[{}]+'.format(punctuation), ' ', text)
    text = text.replace("\u202c","")
    text = text.replace("：","")
    text = text.replace("――“","")
    text = text.replace("”","")
    text = text.replace(";","")
    return text.strip()


if __name__ == '__main__':
    fp = open('D:\\JavaSpace\\SECIII\\result.json', 'r', encoding='utf8')
    fp_final = open('D:\\JavaSpace\\SECIII\\result_final.json', 'w', encoding='utf8')
    f = fp.read()
    json_data = json.loads(f)
    new_data = []
    name_map = {}
    for caixi in json_data:
        for key in caixi.keys():
            new_caixi = {}
            new_item = []
            print("===============================")
            print(key)
            print('有{}种菜'.format(len(caixi[key])))
            for item in caixi[key]:
                # 添加菜谱
                cai = item['菜谱']
                name = cai['名称']
                if '的' in name or '【' in name or '-' in name or '—' in name or '{' in name or '）' in name or "#" in name \
                        or "《" in name or '[' in name:
                    print(name)
                    new_name = deal_name(name)
                    if new_name == "":
                        new_name = name
                else:
                    new_name = name
                new_name = removePunctuation(new_name)
                if new_name not in name_map.keys():
                    name_map[new_name] = 1
                    cai['名称'] = new_name
                    new_item.append(item)
            new_caixi[key] = new_item
            new_data.append(new_caixi)
    json.dump(new_data, fp_final,ensure_ascii=False)
    fp.close()
    fp_final.close()
