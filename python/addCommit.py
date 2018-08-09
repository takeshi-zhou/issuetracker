#coding:utf-8

import json
import MysqlOperation
from kafka import KafkaProducer
from kafka import KafkaConsumer
import configparser
import re
import os
import uuid
import pymysql
from datetime import datetime
import pytz

def local_to_utc(time_str, utc_format='%a %b %d %H:%M:%S %Y %z'):
    if int(time_str[-5:]) // 100 >= 0 :
        timezone = pytz.timezone('Etc/GMT+' + str(int(time_str[-5:]) // 100))
    else:
        timezone = pytz.timezone('Etc/GMT' + str(int(time_str[-5:]) // 100))
    local_format = "%Y-%m-%d %H:%M:%S"
    utc_dt = datetime.strptime(time_str, utc_format)
    local_dt = utc_dt.replace(tzinfo=timezone).astimezone(pytz.utc)
    return local_dt.strftime(local_format)

config = configparser.ConfigParser()
config.read('config.conf')
path = config.get('TestPath', 'path')

consumer = KafkaConsumer('CompleteDownload',
                         bootstrap_servers=['10.141.221.84:9092'],
                         api_version=(0, 9),
                         max_poll_records=10)

for msg in consumer:
    json_data = json.loads(msg.value.decode())
    repo_id = json_data['repoId']
    project_id = json_data['projectId']
    project_url = MysqlOperation.get_data_from_mysql('project', {'uuid': project_id}, ['url'])
    new_path = path + '/' + project_url[0][0].split('/')[3] + '/' + project_url[0][0].split('/')[4]
    os.chdir(new_path)
    os.system('git log > commit_log.log')
    commit_pattern = 'commit ([\w\d]{40,40})'
    date_pattern = 'Date:   (.*)'
    description_pattern = 'x,\.,\.extxax\?D\n([\s\S]{1,}?) ,,\|\.\.timmoc'
    data_del_description = ''

    with open(new_path + '/commit_log.log', 'r', encoding='UTF-8') as f:

        temp = f.readlines()
        description_data = ''
        for index in range(len(temp)):
            if temp[index][0] != ' ':
                data_del_description += temp[index]

            if index == 0:
                description_data += ' ,,|..timmoc'
            elif temp[index][0:7] == 'commit ':
                description_data += ' ,,|..timmoc'
            elif index == len(temp) - 1:
                description_data += temp[index]
                description_data += '\n ,,|..timmoc'
            elif temp[index][0:8] == 'Date:   ':
                description_data += 'x,.,.extxax?D\n'
            else:
                description_data += temp[index]

        description_ret = re.findall(description_pattern, description_data)
        commit_sha_ret = re.findall(commit_pattern, data_del_description)
        date_ret = re.findall(date_pattern, data_del_description)
        date_list = []
        for date in date_ret:
            date_list.append(local_to_utc(date))
        uuids = []
        repo_id_list = []
        for _ in range(len(description_ret)):
            uuids.append(uuid.uuid1().__str__())
            repo_id_list.append(repo_id)
        MysqlOperation.insert_into_mysql(
            tablename='commit',
            params={
                'uuid':uuids,
                'commit_id':commit_sha_ret,
                'message':description_ret,
                'commit_time':date_list,
                'repo_id':repo_id_list
            },
            mode='multiple'
        )
