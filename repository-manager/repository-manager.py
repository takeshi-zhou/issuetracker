import configparser
import json
import time
import requests
import os
import MysqlOperation
import uuid
from kafka import KafkaProducer
from kafka import KafkaConsumer

config = configparser.ConfigParser()
config.read('config.conf')
path = config.get('TestPath', 'path')

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
        except Exception:
            flag += 1
            if flag > 3:
                return None
        else:
            return response

def download(url, projectId):
    if is_project_valid(url):
        # 暂时只适用于github的标准url
        split = url.split('/')
        user = split[3]
        repository = split[4]
        if not os.path.exists(path + '/' + user + '/' + repository):

            if not os.path.exists(path + '/' + user):
                os.makedirs(path + '/' + user)

            os.chdir(path + '/' + user)
            ret = os.system('git clone https://jixixuanf5905:xinylu6261@' + url.replace('https://', ''))
            # ret = subprocess.call(['git', 'clone', 'https://jixixuanf5905:xinylu6261@' + url.replace('https://', '')])

            if ret != 0:
                raise DownloadError('Opps1, download failed! Please ensure the validation of the project.')

            else:
                project_info = get_project_info(url)
                if project_info is None:
                    raise DownloadError('Opps2, download failed! Please ensure the validation of the project.')
                else:
                    description = '' if project_info['description'] is None else project_info['description']
                    language = '' if project_info['language'] is None else project_info['language']
                    repository_id = project_info['id']
                    local_addr = 'github' + '/' + user + '/' + repository
                    params = {
                        'description': description,
                        'language': language,
                        'repository_id': repository_id,
                        'local_addr': local_addr,
                        'url': url,
                        'uuid': uuid.uuid1().__str__()
                    }
                    MysqlOperation.insert_into_mysql(
                        tablename='repository',
                        params=params
                    )
                    return params


    else:
        raise UrlError('The format of url is incorrect!')

def log(string, mode='redirect', index=1):
    t = time.strftime(r"%Y-%m-%d-%H:%M:%S", time.localtime())
    if mode == 'test':
            print("[%s]%s" % (t, string))
    else:
        with open(config.get('KafkaLogPath', 'path') + 'ConsumerLog-' + str(index) + '.log', 'a', encoding='UTF-8') as f:
            print("[%s]%s" % (t, string), file=f)


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
        log(recv, mode='test')
        json_data = json.loads(msg.value.decode())

    except Exception as e:
        log(e.__str__())

    else:
        if 'command' in json_data and json_data['command'] == 'stop-consumer' :
            consumer.close()
            break
        else:
            url = json_data['url']
            projectId = json_data['projectId']
            MysqlOperation.update_mysql(tablename='project', params={'uuid':projectId, 'download_status':'Downloading'})
            try:
                project_info = download(url, projectId)
            except Exception as e:
                producer = KafkaProducer(bootstrap_servers='10.141.221.84:9092', api_version=(0, 9))
                new_msg = {
                    'projectId':projectId,
                    'language':'null',
                    'VCS-Type':'null',
                    'status':'failed',
                    'description':'null'
                }
                producer.send('RepoManager', json.dumps(new_msg).encode())
                producer.close()
            else:
                producer = KafkaProducer(bootstrap_servers='10.141.221.84:9092', api_version=(0, 9))
                producer.send('CompleteDownload', json.dumps({'message':'Download Completed'}).encode())
                new_msg = {
                    'projectId':projectId,
                    'language':project_info['language'],
                    'VCS-Type':get_vcs_type(url),
                    'status':'successful',
                    'description':project_info['description']
                }
                producer.send('RepoManager', json.dumps(new_msg).encode())
                producer.close()
