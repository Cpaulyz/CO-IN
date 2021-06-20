import requests
from bs4 import BeautifulSoup
import json
import time

headers = {
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding": "gzip, deflate, br",
    "Accept-Language": "zh-CN,zh;q=0.9",
    "Cache-Control": "max-age=0",
    "Connection": "keep-alive",
    "Host": "home.meishichina.com",
    "sec-ch-ua": "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"",
    "sec-ch-ua-mobile": "?0",
    "Sec-Fetch-Dest": "document",
    "Sec-Fetch-Mode": "navigate",
    "Sec-Fetch-Site": "none",
    "Sec-Fetch-User": "?1",
    "Upgrade-Insecure-Requests": "1",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15"
}
useful_headers = {
    "authority": "www.meishichina.com",
    "method": "GET",
    "path": "/YuanLiao/HuangDouYa/useful/",
    "scheme": "https",
    "accept": "text/html,application/xhml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding": "gzip, deflate, br",
    "accept-language": "zh-CN,zh;q=0.9",
    "cache-control": "max-age=0",
    "referer": "https://www.meishichina.com/YuanLiao/HuangDouYa/",
    "sec-ch-ua": "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"",
    "sec-ch-ua-mobile": "?0",
    "sec-fetch-dest": "document",
    "sec-fetch-mode": "navigate",
    "sec-fetch-site": "same-origin",
    "sec-fetch-user": "?1",
    "upgrade-insecure-requests": "1",
    "user-agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15"
}


cuisine_arr = []
cuisine_name_arr = []

page = requests.Session().get("https://home.meishichina.com/recipe-type.html")
bs = BeautifulSoup(page.content.decode(), 'html.parser')
check = False
for item in bs.find_all("a"):
	if item.get_text() == "川菜":
		check = True
	if check:
		cuisine_arr.append(item.get("href"))
		cuisine_name_arr.append(item.text)
	if item.get_text() == "赣菜":
		break
print(cuisine_arr)
print(cuisine_name_arr)


all_recipe_name = []
result = []
for index in range(len(cuisine_arr)):		# 所有菜系
	cuisine_site = cuisine_arr[index]
	cuisine_name = cuisine_name_arr[index]
	every_cuisine = []
	page_num = 1
	while True:
		print("page " + str(page_num) + " starts.")
		page_site = cuisine_site + "hot/page/" + str(page_num) + "/"		# 最热那栏
		page = requests.Session().get(page_site, headers = headers)
		bs = BeautifulSoup(page.content.decode(), 'html.parser')

		frame = bs.find_all("div", class_='ui_newlist_1 get_num')[0].ul.contents
		for i in range(len(frame)):		# 正常一页十个菜，有的最后一页不满十个
			if i % 2 == 1:
				name = frame[i].div.contents[1].get("title")
				if name in all_recipe_name:		# 重复名字的菜只保留第一次
					continue
				else:
					all_recipe_name.append(name)
				every_recipe = {"菜谱": {"名称": name}, "食材": {"主料": [], "辅料": []}}		# 每个菜
				data_id = frame[i].attrs["data-id"]
				recipe_site = "https://home.meishichina.com/recipe-" + str(data_id) + ".html"
				recipe_page = requests.get(recipe_site, headers=headers)
				recipe_bs = BeautifulSoup(recipe_page.content.decode(), 'html.parser')


				ingredient = recipe_bs.find_all("fieldset", class_="particulars")
				main_ingredient = ingredient[0].div.ul.contents		# 主料
				try:
					accessory = ingredient[1].div.ul.contents			# 辅料
				except:
					accessory =	[]		# 有的菜把配料全放在主料里了[https://home.meishichina.com/recipe-131011.html]
				for j in range(len(main_ingredient)):
					if j % 2 == 1:
						a = main_ingredient[j].span.a
						material_attribute = {}
						if a == None:
							name = main_ingredient[j].span.b.text
						else:
							dosage = main_ingredient[j].contents[3].text
							name = a.b.text
							url = a.get("href") + "useful/"
							useful = requests.get(url, headers=useful_headers)
							useful_bs = BeautifulSoup(useful.content.decode(), 'html.parser')
							try:
								all_text = useful_bs.find_all("div", class_="category_usebox mt10 clear")[0].div.contents
								if len(all_text) != 1:		# 有的即使能跳转但也没有营养功效那一页
									material_attribute = {"用量": dosage, "简介": all_text[3].text, "营养价值": all_text[7].text, "功效": all_text[11].text}
							except:		# 跳不到useful那页
								material_attribute = {}
						every_recipe["食材"]["主料"].append({name: material_attribute})
				for j in range(len(accessory)):
					if j % 2 == 1:
						a = accessory[j].span.a
						material_attribute = {}
						if a == None:
							name = accessory[j].span.b.text
						else:
							dosage = accessory[j].contents[3].text
							name = a.b.text
							url = a.get("href") + "useful/"
							useful = requests.get(url, headers=useful_headers)
							useful_bs = BeautifulSoup(useful.content.decode(), 'html.parser')
							try:
								all_text = useful_bs.find_all("div", class_="category_usebox mt10 clear")[
									0].div.contents
								if len(all_text) != 1:  # 有的即使能跳转但也没有营养功效那一页
									material_attribute = {"用量": dosage, "简介": all_text[3].text, "营养价值": all_text[7].text, "功效": all_text[11].text}
							except:  	# 跳不到useful那页
								material_attribute = {}
						every_recipe["食材"]["辅料"].append({name: material_attribute})


				recipe_attribute = recipe_bs.find_all("div", class_="recipeCategory_sub_R mt30 clear")[0].ul.contents
				try:
					every_recipe["菜谱"]["口味"] = recipe_attribute[1].span.a.text
				except:
					every_recipe["菜谱"]["口味"] = ""
				try:
					every_recipe["菜谱"]["工艺"] = recipe_attribute[3].span.a.text
				except:
					every_recipe["菜谱"]["工艺"] = ""
				try:
					every_recipe["菜谱"]["耗时"] = recipe_attribute[5].span.a.text
				except:
					every_recipe["菜谱"]["耗时"] = ""


				method = recipe_bs.find_all("div", class_="recipeStep")[0].ul.contents
				method_arr = []
				for j in range(len(method)):
					if j % 2 == 1:
						text = method[j].text.strip()[len(str(j)):]
						method_arr.append(text)
				every_recipe["菜谱"]["做法"] = method_arr


				print(every_recipe)
				every_cuisine.append(every_recipe)

		loop = bs.find_all('div', class_='ui-page-inner')
		if "下一页" in loop[0].text:
			page_num += 1
			time.sleep(30)
			if page_num % 40 == 0:
				time.sleep(60)
			if len(every_cuisine) >= 100:
				break
		else:
			break
	result.append({cuisine_name: every_cuisine})

	print(cuisine_name + " done.")
	with open(cuisine_name + '.json', 'w+') as file:
		json.dump(every_cuisine, file, ensure_ascii=False)
	time.sleep(20)
with open('result.json', 'w+') as file:
	json.dump(result, file, ensure_ascii=False)