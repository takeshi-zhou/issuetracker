from python.restfulAPI import create_app
from python.restfulAPI.config.config import FLASK_APP_CONFIG

from gevent import monkey

from gevent.pywsgi import WSGIServer

monkey.patch_all()

from flask_cors import *


app = create_app()
CORS(app, supports_credentials=True)

if __name__ == '__main__':
    # app.run(host=FLASK_APP_CONFIG['host'], debug=FLASK_APP_CONFIG['debug'], port=FLASK_APP_CONFIG['port'], threaded=FLASK_APP_CONFIG['threaded'])
    http_server = WSGIServer((FLASK_APP_CONFIG['host'], FLASK_APP_CONFIG['port']), app)
    http_server.serve_forever()