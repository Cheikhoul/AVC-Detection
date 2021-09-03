import re
from datetime import datetime

from flask import current_app as app
from flask import request, jsonify, make_response
from flask_login import login_user, login_required, current_user
from sqlalchemy import and_
from Application import db
from Application import login_manager
from Application.models import User, Measurements


################ MOBILE APPLICATION LOGIN  ###########################


@app.route('/loginMobile', methods=['POST'])
def login():
    """ User authentication and login for mobile application
    """

    if not request.json:
        return make_response(400)

    elif current_user.is_authenticated:
        return make_response(jsonify({"msg": "Already logged in"}, 400))  # bad request

    req_data = request.get_json()
    password = req_data["password"]
    email = req_data["email"]

    user = User.query.filter(User.email == email).first()
    if not user:
        return make_response(jsonify("Invalid credentials"), 401)  # resource not found

    elif not user.check_hash(password):
        return make_response(jsonify("Invalid credentials", 401))  # 401 unauthorised access

    login_user(user)
    return make_response(jsonify({"msg": f" Logged in , {user.user_name}"}), 200)  # 200 success


################ DATA UPLOAD  ###########################
@app.route('/set_data', methods=['POST'])
@login_required
def set_data():
    """ Inserting the received data from the mobile application in the database"""
    try:
        file = request.files['file']
        p = str(file.read().decode("utf-8"))  # file sent from phone could differ
        result = p.split(sep='\n')[1:-1]
        successfully_added = 0
        for measure in result:
            res = re.search(r'(\d{2})/(\d{2})/(\d{4})/(\d{2}):(\d{2}),(\d+)', measure)

            if res is not None:
                date_and_time = datetime(int(res.group(3)), int(res.group(2)), int(res.group(1)), int(res.group(4)),
                                         int(res.group(5)))
                m = Measurements(date_and_time, current_user.__dict__['id'], res.group(6))
                db.session.add(m)
                successfully_added += 1

        if successfully_added == len(result):
            db.session.commit()
            return make_response(jsonify({"msg": "Ok"}), 200)
        else:
            return jsonify({"msg": "Invalid data"}, 500)

    except KeyError or ValueError or Exception:
        db.session.rollback()
        return make_response(jsonify({"msg": "Invalid request"}), 400)
