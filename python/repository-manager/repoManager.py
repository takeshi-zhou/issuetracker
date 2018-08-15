#coding:utf-8
import configparser
import json
import time
import requests
import os
import mysqlOperation
import uuid
from kafka import KafkaProducer
from kafka import KafkaConsumer

config = configparser.ConfigParser()
config.read('config.conf')
path = config.get('Path', 'path')

class UrlError(Exception):
    def __init__(self, ErrorInfo):
        super().__init__(self)
        self.error_info = ErrorInfo

    def __str__(self):
        return self.error_info

class DownloadError(Exception):
    def __init__(self, ErrorInfo):
        super().__init__(self)
        self.error_info = ErrorInfo

    def __str__(self):
        return self.error_info

def get_vcs_type(project_url):
    dic = {'github.com': 'git'}
    patterns = ('github.com',)
    for pattern in patterns:
        if pattern in project_url:
            return dic[pattern]

def is_project_valid(project_url):
    flag = 0
    while True:
        try:
            response = requests.get(project_url, timeout=15).headers['Status']
        except Exception as e:
            flag += 1
            if flag > 3:
                return False
        else:
            if '200' in response:
                return True
            else:
                return False

def get_project_info(url):
    flag = 0
    while True:
        try:
            header = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0',
                     'Authorization': 'token ' + config.get('GithubToken', 'token')}
            split = url.split('/')
            response = requests.get('https://api.github.com/repos/' + split[3] + '/' + split[4], timeout=15, headers=header).json()
        except Exception as e:
            print(e)
            flag += 1
            if flag > 3:
                return None
        else:
            return response

def download(url):
    if is_project_valid(url):
        # 暂时只适用于github的标准url
        split = url.split('/')
        user = split[3]
        repository = split[4]
        path_postfix = config.get('PathPostfix', get_vcs_type(url))
        if not os.path.exists(path + '/' + path_postfix + '/' + user + '/' + repository):

            if not os.path.exists(path + '/' + path_postfix + '/' + user):
                os.makedirs(path + '/' + path_postfix + '/' + user)

            os.chdir(path + '/' + path_postfix + '/' + user)
            ret = os.system('git clone https://jixixuanf5905:xinylu6261@' + url.replace('https://', ''))
            # ret = subprocess.call(['git', 'clone', 'https://jixixuanf5905:xinylu6261@' + url.replace('https://', '')])

            if ret != 0:
                raise DownloadError('Opps1, download failed! Please ensure the validation of the project.')

            else:
                return 'successful'
        else:
            return 'existed'

    else:
        raise UrlError('The format of url is incorrect!')

def log(string):
    t = time.strftime(r"%Y-%m-%d-%H:%M:%S", time.localtime())
    print("[%s]%s" % (t, string))

def send_failed_msg(project_id):
    producer = KafkaProducer(bootstrap_servers='10.141.221.84:9092', api_version=(0, 9))
    new_msg = {
        'projectId': project_id,
        'language': 'null',
        'VCS-Type': 'null',
        'status': 'failed',
        'description': 'null'
    }
    producer.send('RepoManager', json.dumps(new_msg).encode())
    producer.close()

log('start consumer')
consumer = KafkaConsumer('ProjectManager',
                         bootstrap_servers=['10.141.221.84:9092'],
                         group_id='test-consumer-group',
                         api_version=(0, 9),
                         max_poll_records=10)
consumer.poll(10)
consumer.seek_to_end()
for msg in consumer:
    try:
        recv = "%s:%d:%d: key=%s value=%s" % (msg.topic, msg.partition, msg.offset, msg.key, msg.value)
        log(recv)
        json_data = json.loads(msg.value.decode())

    except Exception as e:
        log(e.__str__())

    else:
        if 'command' in json_data and json_data['command'] == 'stop-consumer' :
            consumer.close()
            break
        else:
            url = json_data['url']
            project_id = json_data['projectId']
            try:
                download_result = download(url)
            except Exception as e:
                send_failed_msg(project_id)
            else:
                if download_result != 'existed':
                    project_info = get_project_info(url)
                    if project_info is None:  # 项目信息获取失败
                        send_failed_msg(project_id)
                    else:
                        try:
                            description = '' if project_info['description'] is None else project_info['description']
                            language = '' if project_info['language'] is None else project_info['language']
                            repository_id = project_info['id']
                            split = url.split('/')
                            user = split[3]
                            repository = split[4]
                            path_postfix = config.get('PathPostfix', get_vcs_type(url))
                            local_addr = path_postfix + '/' + user + '/' + repository
                            repo_id = uuid.uuid1().__str__()
                            mysqlOperation.insert_into_mysql(
                                tablename='repository',
                                params={
                                    'description': description,
                                    'language': language,
                                    'repository_id': repository_id,
                                    'local_addr': local_addr,
                                    'url': url,
                                    'uuid': repo_id
                                }
                            )
                        except Exception as e:
                            send_failed_msg(project_id)
                        else:
                            producer = KafkaProducer(bootstrap_servers='10.141.221.84:9092', api_version=(0, 9))
                            producer.send('CompleteDownload', json.dumps(
                                {'repoId': repo_id, 'projectId': project_id, 'local_addr':local_addr, 'message': 'Download Completed'}).encode())
                            new_msg = {
                                'projectId': project_id,
                                'language': language,
                                'VCS-Type': get_vcs_type(url),
                                'status': 'Downloaded',
                                'description': description,
                                'repoId': repo_id
                            }
                            producer.send('RepoManager', json.dumps(new_msg).encode())
                            producer.close()
                else:
                    result = mysqlOperation.get_data_from_mysql(
                        tablename='repository',
                        params={'url':url},
                        fields=['language', 'description', 'uuid']
                    )
                    producer = KafkaProducer(bootstrap_servers='10.141.221.84:9092', api_version=(0, 9))
                    new_msg = {
                        'projectId': project_id,
                        'language': result[0][0],
                        'VCS-Type': get_vcs_type(url),
                        'status': 'Downloaded',
                        'description': result[0][1],
                        'repoId': result[0][2]
                    }
                    producer.send('RepoManager', json.dumps(new_msg).encode())
                    producer.close()
