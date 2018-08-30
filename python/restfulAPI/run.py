from tornado.wsgi import WSGIContainer
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop

import os
import sys
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(BASE_DIR)
# 导入flask项目
from python.restfulAPI.main import app

# http_server = HTTPServer(WSGIContainer(app))
# http_server.listen(8000)  # 对应flask的端口
# IOLoop.instance().start()

# 如果要开启多进程模式用下面的代码，不过仅在linux下
http_server = HTTPServer(WSGIContainer(app))
http_server.bind(8102)
http_server.start()
IOLoop.instance().start()
