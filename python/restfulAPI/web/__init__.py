from flask import Blueprint

web = Blueprint('web', __name__)

from python.restfulAPI.web import code_views
from python.restfulAPI.web import commit_views
from python.restfulAPI.web import error_views