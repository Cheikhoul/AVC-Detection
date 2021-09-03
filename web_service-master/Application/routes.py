from flask import request, jsonify, render_template, redirect, make_response, url_for, flash, abort
from flask import current_app as app
from flask_login import login_required, current_user, login_user, logout_user
from sqlalchemy.exc import IntegrityError
from Application.models import Measurements, db, User, Relation
from Application.forms import LoginForm, RegisterPatient, ResetRequest, ResetPassword
from Application import mail, REMEMBER_COOKIE_DURATION
from flask_mail import Message
import datetime


################### ROUTES FOR WEB APPLICATION ################################
@app.route('/')
def hello():
    return redirect(url_for('login_web'))


@app.route('/doctors_view', methods=['GET', 'POST'])
@login_required
def doctors_view():
    """Doctors view logic endpoint handler. Here AJAX requests
        from the javascript code are handled and processed.
    """
    if current_user.__dict__['role'] == 1:
        return abort(404)

    if request.method == 'POST':
        try:
            date_begin = list(map(lambda x: int(x), str(request.form['dateDeb']).split(sep='/')))
            hour_begin = list(map(lambda x: int(x), str(request.form['heureDebut']).split(sep=':')))
            date_end = list(map(lambda x: int(x), str(request.form['dateFin']).split(sep='/')))
            hour_end = list(map(lambda x: int(x), request.form['heureFin'].split(sep=':')))
            id_patient = int(request.form['id'])

            date_begin = datetime.datetime(year=date_begin[0], month=date_begin[1], day=date_begin[2],
                                           hour=hour_begin[0], minute=hour_begin[1])
            date_end = datetime.datetime(year=date_end[0], month=date_end[1], day=date_end[2],
                                         hour=hour_end[0], minute=hour_end[1])

            user = User.query.filter(User.id == id_patient).first()
            results = user.result
            heart_f, dates_hf = list(), list()
            blood_p, dates_bd = list(), list()
            results = results.filter(Measurements.date_time >= date_begin) \
                .filter(Measurements.date_time <= date_end)\
                .order_by(Measurements.date_time)\
                .all()

            if bool(request.form["freq"]):
                for res in results:
                    heart_f.append(res.heart_frequencies)
                    dates_hf.append(res.date_time)

            if bool(request.form["tens"]):
                for res in results:
                    blood_p.append(res.blood_pressure)
                    dates_bd.append(res.date_time)


            return jsonify({"msg": "Success", "heart_f": heart_f, "dates_hf": dates_hf,
                            "blood_p": blood_p, "dates_bd": dates_bd})
        except Exception:
            return jsonify({"msg": "Error"})
    else:
        user = User.query.filter(User.id == current_user.__dict__['id']).first()
        patients_info = [{"name": u.user_name, "surname": u.user_surname, "id": u.id} for u in user.get_patients()]
        return render_template('doctors_view.html', name=user.user_name, patients={"patients": patients_info})


@app.route('/delete_pat', methods=['POST'])
@login_required
def delete_pat():
    if current_user.__dict__['role'] != 2:
        return abort(404)

    id_patient = request.form['id']
    doc_id = current_user.__dict__['id']
    rel = Relation.query.filter(Relation.doctor == doc_id, Relation.patient == id_patient).first()

    if not rel:
        return jsonify({"msg": "Unauthorised action"})

    db.session.delete(rel)
    db.session.commit()
    return jsonify({"msg": "Success"})


@app.route('/register_patient', methods=['GET', 'POST'])
@login_required
def register_patient():
    """  Adding a patient to the database from an authenticated doctor account"""

    if current_user.__dict__['role'] != 2:
        return abort(404)

    form = RegisterPatient()
    if form.validate_on_submit():
        name, email, surname, password = form.patient_name.data, form.email.data, form.patient_surname.data, \
                                         form.password.data
        try:
            new_patient = User(name, surname, password, email, 1)
            db.session.add(new_patient)
            doc = User.query.filter(User.id == current_user.__dict__['id']).first()
            doc.add_patient(new_patient)
            return redirect(url_for("doctors_view"))
        except IntegrityError:
            return redirect(url_for("register_patient"))

    return render_template('addpatient.html', form=form, name=current_user.__dict__['user_name'])


def send_email(user):
    """ Sending email to the user by generating the token and ..."""

    token = user.get_reset_token()

    msg = Message('Password Reset Request', sender='noreply@avc.com', recipients=[user.email])

    msg.body = f'Pour réinitialiser votre mot de passe cliquez sur le lien suivant ' \
               f'{url_for("password_reset_token", token=token, _external=True)}'

    mail.send(msg)


@app.route('/request_reset', methods=['GET', 'POST'])
def request_reset():
    """Request password route """

    if current_user.is_authenticated:
        return redirect(url_for('doctors_view'))

    form = ResetRequest()
    if form.validate_on_submit():
        user = User.query.filter(User.email == form.email.data).first()
        if user is not None:
            send_email(user)
        flash("Email avec un lien pour réinitialiser votre mot de passe sera envoyé à l'adresse", "info")
        return redirect(url_for('login_web'))

    return render_template('reset_request.html', title="Reset Password Request", form=form)


@app.route("/password_reset/<token>", methods=['GET', 'POST'])
def password_reset_token(token):
    """Route for password change. URL is accessed
        from reset token send to the users email.
    """

    user = User.verify_reset_token(token)
    if user is None:
        return abort(401)

    if current_user.is_authenticated:
        return abort(400, msg="<h1> Invalid operation </h1>")

    form = ResetPassword()

    if form.validate_on_submit():
            user.hash_pass(form.password.data)
            db.session.commit()
            flash("Mot de passe changé!", 'success')
            return redirect(url_for('login_web'))

    return render_template('reset_password.html', title="Reset Password", form=form, token=token)


@app.route('/login_web', methods=['GET', 'POST'])
def login_web():
    """ Web form of login for clinicians only """

    if current_user.is_authenticated:
        if current_user.__dict__['role'] == 2:
            return redirect(url_for("doctors_view"))
        elif current_user.__dict__['role'] == 3:
            return redirect('/admin')
        else:
            return abort(404)

    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter(User.email == form.email.data).first()

        if user is None:
            flash("Invalid credentials", category="danger")
            return redirect(url_for("login_web"))

        elif user.role == 1:
            return abort(404)

        elif user and user.check_hash(form.password.data):
            login_user(user)
            if user.role == 3:
                return redirect('/admin')

            return redirect(url_for("doctors_view"))

        else:
            flash("Invalid credentials", category="danger")
            return redirect(url_for("login_web"))

    return render_template('login_page.html', form=form)


@app.route('/logout', methods=['GET', 'POST'])
@login_required
def logout():
    """Logout route for web application as well as
        for mobile application
    """

    logout_user()
    if request.method == 'GET':
        flash("Logged out!", category="success")
        return redirect(url_for('login_web'))
    else:
        return make_response(jsonify({"msg": "Logged out"}), 200)
