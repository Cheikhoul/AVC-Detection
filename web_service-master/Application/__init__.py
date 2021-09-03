import datetime
from flask import Flask
from config import Config
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_login import LoginManager
from flask_mail import Mail
from flask_bootstrap import Bootstrap
from flask_assets import Bundle, Environment
from flask_admin import Admin
from flask_security import Security, SQLAlchemyUserDatastore

# GLOBAL PLUGINS
app = Flask(__name__)
app.config.from_object(Config)

# Database
db = SQLAlchemy(app)
migrate = Migrate(app, db)  # migration engine
# Login manager configuration
login_manager = LoginManager(app)
login_manager.needs_refresh_message = "Session timeout, re-login"
login_manager.needs_refresh_message_category = "info"
REMEMBER_COOKIE_DURATION = datetime.timedelta(minutes=10)
# Mail engine
mail = Mail(app)
# Admin config
admin = Admin(app)
#  CSS and JavaScript assets
Bootstrap(app)
js = Bundle('js/avc.js', 'js/lib/Chart.bundle.min.js',
            'js/lib/Chart.min.js', output='js/gen/doctors_view.js')
assets = Environment(app)
assets.register('doctors_view_js', js)
css = Bundle("css/lib/bootstrap.min.css", "css/lib/Chart.min.css",
             "css/icons/font-awesome.min1.css", "css/lib/bootstrap.css.map",
             output="css/gen/doctors_view.css")
assets.register("doctors_view_css", css)
css_login = Bundle("css/lib/bootstrap.min.css", "css/lib/bootstrap.css.map",
                   "css/signin.css", output="css/gen/login_view.css")
assets.register("login_view_css", css_login)


def create_app():
    """Initialising the core application """

    with app.app_context():
        from . import mobile_app_routes, models, routes
        db.create_all()
        models.initialise_admin(admin)

    return app
