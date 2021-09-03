import os
from pathlib import Path
from dotenv import load_dotenv
from dotenv import find_dotenv

# defining environment variables file path
env_path = Path('.') / '.env'
load_dotenv(dotenv_path=env_path)
basedir = os.path.abspath(os.path.dirname(__file__))


class Config(object):
    """ This class configures the application object.
        To be used when setting up the app features """

    SECRET_KEY = os.getenv('SECRET_KEY')

    if os.getenv('DB_USER') and os.getenv('DB_PASSWORD') and os.getenv('DB_NAME'):
        # for mysql server database
        SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://' + os.getenv('DB_USER') + ':' + \
                                os.getenv('DB_PASSWORD') + \
                                '@' + os.getenv('HOST') + '/' + os.getenv('DB_NAME')
    else:
        SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'app.db')

    MAIL_SERVER = os.getenv('MAIL_SERVER')
    MAIL_PORT = os.getenv('MAIL_PORT')
    MAIL_USE_TLS = True
    MAIL_USERNAME = os.getenv('MAIL_USER')
    MAIL_PASSWORD = os.getenv('MAIL_PASSWORD')
    FLASK_DEBUG = True
    SQLALCHEMY_TRACK_MODIFICATION = False
    SERVER_NAME = "localhost:62000"


