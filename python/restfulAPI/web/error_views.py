from flask import jsonify, request

from python.restfulAPI.libs.error import OperationError
from . import web

@web.errorhandler(404)
def not_found(error=None):
    message = {
        'status': 'Failed',
        'message': 'Not Found: ' + request.url
    }
    resp = jsonify(message)
    resp.status_code = 404
    return resp

@web.errorhandler(OperationError)
def operation_failed(OperationError):
    message = {
        'status': 'Failed',
        'message': OperationError.__str__
    }
    resp = jsonify(message)
    resp.status_code = 500

    return resp