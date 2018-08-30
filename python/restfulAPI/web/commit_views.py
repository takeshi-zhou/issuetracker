#encoding:utf-8

from flask import jsonify, request

from python.restfulAPI.forms.commit_form import CommitForm, CheckoutForm, CheckoutMasterForm
from python.restfulAPI.libs.error import OperationError
import os
from python.restfulAPI.libs import mysqlOperation
from python.restfulAPI.config import config
from python.restfulAPI.libs.tool import calculate_start
from python.restfulAPI.web.error_views import not_found
from . import web

@web.route('/commit', methods=['GET'])
def get_commit():
    form = CommitForm(request.args)
    if form.validate():

        repo_id = form.repo_id.data
        page = form.page.data
        per_page = form.per_page.data
        is_whole = form.is_whole.data

        try:
            commit_ret = mysqlOperation.get_data_from_mysql(
                tablename='commit',
                params={'repo_id':repo_id},
                fields=['*'],
                order_field='commit_time',
                start=calculate_start(page, per_page) - 1,
                num=None if is_whole else per_page
            )

            data = []
            for commit in commit_ret:
                dic = dict()
                dic['uuid'] = commit[0]
                dic['commit_id'] = commit[1]
                dic['message'] = commit[2]
                dic['developer'] = commit[3]
                dic['commit_time'] = str(commit[4])
                dic['repo_id'] = commit[5]
                data.append(dic)
                del dic
        except:
            return not_found()
        else:
            return jsonify(data=data)

    else:
        raise OperationError('Invalid parameters')

@web.route('/commit/checkout', methods=['GET'])
def checkout():
    form = CheckoutForm(request.args)
    if form.validate():
        repo_id = form.repo_id.data
        commit_id = form.commit_id.data
        try:
            repo_info = mysqlOperation.get_data_from_mysql(
                tablename = 'repository',
                params = {'uuid':repo_id},
                fields = ['local_addr']
            )
            local_addr = repo_info[0][0]
            project_path = config.REPO_PATH + '/' +  local_addr
            os.chdir(project_path)

            os.system('git checkout ' + commit_id)
        except:
            raise OperationError('Internal error')
        else:
            message = {
                'status': 'Successful'
            }
            return jsonify(data=message)
    else:
        raise OperationError('Invalid parameters')

@web.route('/commit/checkout-master/<repo_id>', methods=['GET'])
def checkout_master(repo_id):
    form = CheckoutMasterForm(request.args)
    if form.validate():
        repo_id = form.repo_id.data
        try:
            repo_info = mysqlOperation.get_data_from_mysql(
                tablename = 'repository',
                params = {'uuid':repo_id},
                fields = ['local_addr']
            )
            local_addr = repo_info[0][0]
            project_path = config.REPO_PATH + '/' +  local_addr
            os.chdir(project_path)

            os.system('git checkout master')
        except:
            raise OperationError('Internal error')
        else:
            message = {
                'status': 'Successful'
            }
            return jsonify(data=message)
    else:
        raise OperationError('Invalid parameters')

@web.route('/commit/commit-time', methods=['GET'])
def commit_time():
    try:
        commit_id = request.args['commit_id']
        commit_info = mysqlOperation.get_data_from_mysql(
            tablename = 'commit',
            params = {'commit_id':commit_id},
            fields = ['commit_time']
        )

    except:
        raise OperationError('Internal error')
    else:
        if len(commit_info) == 0:
            raise OperationError('Invalid parameters')

        else:
            commit_time = commit_info[0][0]
            message = {
                'status': 'Successful',
                'commit_time':str(commit_time)
            }
            return jsonify(data=message)