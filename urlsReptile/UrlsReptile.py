import random
import time
import requests
import yaml
import pymongo
import random
from pybloom_live import ScalableBloomFilter


# 读取配置
def readConfig():
    with open("config.yaml", "r", encoding="utf-8") as file:
        file_data = file.read()
    config = yaml.safe_load(file_data)
    return config


# 连接数据库
def dbConnection(config):
    # 连接数据库
    client = pymongo.MongoClient(config['host'], config['port'])
    reptile_test = client[config['dbName']]
    return reptile_test


# 配置请求头
def getHeaders(config):
    headers = {
        "Cookie": config['cookie'],
        "User-Agent": config['user_agent']
    }
    return headers


# 配置参数
def getParams(config, fakeid):
    begin = "0"
    params = {
        "action": "list_ex",
        "begin": begin,  # 决定展示的起始页
        "count": "1",  # 决定一页link的量
        "fakeid": fakeid,
        "type": "9",
        "query": "",
        "token": config['token'],
        "lang": "zh_CN",
        "f": "json",
        "ajax": "1"
    }
    return params


# 设置布隆过滤器
def getBloomFilter():
    # 设置可自动扩容的布隆过滤器
    bloom = ScalableBloomFilter(initial_capacity=200, error_rate=0.001)
    # 锻炼过滤器
    with open("app_msg_list.txt", "r") as urls:
        line = urls.readline()
        while line:
            # 开始锻炼
            bloom.add(line.strip())  # 为了防止有换行出现要用strip
            # print(line.strip())
            line = urls.readline()
    return bloom


# 设置代理
def getIPProxy(reptile_test):
    # 从mongodb中拿取代理池
    proxies = {}
    ''' 免费代理暂时不可用
    proxyCollection = reptile_test[config['pName']
    result = proxyCollection.find({}, {'_id': 0})  # 0代表不要，1代表要
    for a in result:  # 往proxies中注入数据
        proxies["http"] = a.get("iPAddress") + ":" + a.get("iPPort")
        proxies["https"] = a.get("iPAddress") + ":" + a.get("iPPort")
        break
    '''
    return proxies


def main():
    # 读取配置
    config = readConfig()
    # 连接数据库
    reptile_test = dbConnection(config)
    # 设置请求
    url = "https://mp.weixin.qq.com/cgi-bin/appmsg"
    headers = getHeaders(config)
    # 设置布隆过滤器
    bloom = getBloomFilter()
    # 设置代理
    proxies = getIPProxy(reptile_test)
    # 连接到存放urls的集合
    urlsCollection = reptile_test[config['uName']]

    # 开始爬取
    i = 1
    for fakeid in config['fakeid']:
        '''
        begin = i * 5
        params["begin"] = str(begin)
        '''
        # 配置参数
        params = getParams(config, fakeid)

        # 随机暂停几秒，避免过快请求被逮到
        time.sleep(random.randint(5, 15))
        requests.packages.urllib3.disable_warnings()
        session = requests.session()
        # session.trust_env = False
        resp = session.get(url, headers=headers, params=params, proxies=proxies, timeout=10, verify=False)

        # 微信流量控制
        if resp.json()['base_resp']['ret'] == 200013:
            print(f"流量控制，stop at {params['begin']}")
            time.sleep(3600)
            continue

        # 如果返回的内容中为空则结束
        if len(resp.json()['app_msg_list']) == 0:
            print("all article parsed")
            break

        msg = resp.json()
        if "app_msg_list" in msg:
            for item in msg["app_msg_list"]:
                # 使用布隆过滤器
                if bloom.add(item['link']):
                    print("链接已爬过")
                    continue
                # info = "{}".format(item['link'])
                # 写入文件
                with open("app_msg_list.txt", "a", encoding='utf-8') as f:
                    f.write(item['link'] + '\n')
                # 存入数据库
                doc = {'aid': str(item["aid"]), 'tittle': item['title'], 'link': item['link'],
                       'create_time': time.strftime("%Y-%m-%d %H:%M", time.localtime(int(item['create_time'])))
                       # 把时间戳转换为时间
                       }
                urlsCollection.insert_one(doc)
            print(f"第{i}个公众号爬取成功\n")
            # print("\n".join(item['link'].split(",")))
            print("\n---------------------------------------------------------------------------------\n")
        # 翻页
        i += 1


if __name__ == "__main__":
    main()
