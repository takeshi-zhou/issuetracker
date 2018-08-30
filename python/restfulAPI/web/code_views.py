from flask import jsonify, request
from . import web
from python.restfulAPI.libs.error import OperationError

@web.route('/code-service', methods=['GET'])
def get_file_content():
    file_path = request.args['file_path']
    try:
        with open(file_path, 'r', encoding='UTF-8') as f:
            data = f.read()
    except Exception as e:
        raise OperationError(e.__str__())
    else:
        message = {
            'status':'Successful',
            'content':data
        }
        return jsonify(data=message)