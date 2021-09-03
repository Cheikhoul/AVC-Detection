# DATABASE MODELS FOR THE APPLICATION
# AVC PROJECT 2019 INSA RENNES
import os
from datetime import datetime

from flask import url_for
from flask_admin.contrib.sqla import ModelView
from flask_admin.menu import MenuLink
from flask_login import UserMixin
from itsdangerous import TimedJSONWebSignatureSerializer as Serialiser
from sqlalchemy import ForeignKey
from sqlalchemy.orm import relationship
from werkzeug.exceptions import abort
from werkzeug.security import generate_password_hash, check_password_hash
from wtforms import PasswordField, StringField, IntegerField
from wtforms.fields.html5 import EmailField
from wtforms.validators import DataRequired, Email, Length, NumberRange
from Application import db, login_manager
from config import env_path, load_dotenv
from flask_security import current_user
load_dotenv(env_path)


@login_manager.user_loader
def user_loader(use_id):
    """ The function registers the user logged in , in the user
        session
    """
    return User.query.get(int(use_id))


class Relation(db.Model):
    """Table for many to many relationship ( doctors and patients)
       linked to the user table
    """

    __tablename__ = 'relation'
    doctor = db.Column("doctor", db.Integer, ForeignKey('user.id'), primary_key=True)
    patient = db.Column("patient", db.Integer, ForeignKey('user.id'), primary_key=True)

    def __init__(self, doc, patient):
        self.doctor = doc.id
        self.patient = patient.id


class User(db.Model, UserMixin):
    """Table for users. Main objective is to verify that the
    connecting user(patient or doctor) is the real one who's results are
    those in the database
    """

    __tablename__ = 'user'
    id = db.Column('id', db.Integer, primary_key=True)  # instanced automatically
    user_name = db.Column('Name', db.String(40), nullable=False, unique=False)
    user_surname = db.Column("Surname", db.String(40), nullable=False, unique=False)
    password = db.Column('Password', db.String(120), nullable=False)
    email = db.Column("Email", db.String(40), nullable=True, unique=True)
    role = db.Column("Role", db.Integer, nullable=False)  # 1 for user , 2 for doctor

    # establishing a bidirectional relationships
    result = relationship("Measurements", lazy='dynamic', backref="Patient ID", cascade="all, delete-orphan")
    patients = relationship("Relation", lazy='select', backref='doctor_info', foreign_keys='Relation.doctor',
                            cascade="all, delete-orphan")
    doctors = relationship("Relation", lazy='select', backref='patient_info', foreign_keys='Relation.patient',
                           cascade="all, delete-orphan")

    def __init__(self, name="", surname="", password="", email="", role=1):

        self.user_name = name
        self.user_surname = surname
        self.email = email
        self.role = role
        self.hash_pass(password)

    def hash_pass(self, password):
        self.password = generate_password_hash(password, method="sha256")

    def check_hash(self, password):
        """ Checking the password hash in the database with this produced from the password
            parameter - login """

        return check_password_hash(self.password, password)

    def get_reset_token(self, expires=1800):
        """ Generating a token with expiration of 180s for password recovery """

        s = Serialiser(os.getenv('SECRET_KEY'), expires)
        return s.dumps({'user_id': self.id}).decode('utf-8')

    @staticmethod
    def verify_reset_token(token):

        s = Serialiser(os.getenv('SECRET_KEY'))
        try:
            id = s.loads(token)['user_id']
        except Exception:
            return None

        return User.query.get(id)

    #############  DOCTOR METHODS ###########
    def get_patients(self):
        """The function return the list of patients related
            with the doctor """

        if self.role != 2:
            return []

        return [patient.patient_info for patient in self.patients]

    def add_patient(self, patient):
        """ Add a patient to the calling object """

        if self.role != 2:
            raise Exception("Operation not allowed")

        db.session.add(Relation(self, patient))
        db.session.commit()

    #############  PATIENT METHODS ###########

    def get_doctors(self):

        return [doc.doctor_info for doc in self.doctors]

    def get_results(self, _from, _to):  # _from <- tuple with date and time
        """Returning results of a patient from date _from
            to date _to"""

        date, time = _from[0], _from[1]  # initial date and time
        date1, time1 = _to[0], _to[1]
        measure = self.result
        return [m.heart_frequencie for m in measure if (date <= m.date <= date1 and time <= m.time <= time1)]

    def __repr__(self):  # used for tests
        return str(self.user_name) + " " + str(self.user_surname) \
               + " " + self.email + " "


class Measurements(db.Model):
    """ Database table for the results of the heart
        frequencies measurement as well as blood pressure measurement.
        Elements referenced by the date and time as well as the user
        id
    """
    __tablename__ = 'measurements'
    date_time = db.Column("Date-time", db.DateTime, primary_key=True)
    # association with the user table
    person_id = db.Column("Patient ID", db.INTEGER, db.ForeignKey('user.id'), primary_key=True)
    heart_frequencies = db.Column("Heart frequency", db.FLOAT, nullable=False)
    blood_pressure = db.Column("Blood pressure", db.FLOAT, nullable=False)

    def __init__(self, date_time, patient_id, heart_f=0, b_p=0):
        self.date_time = date_time
        self.person_id = patient_id
        self.heart_frequencies = heart_f
        self.blood_pressure = b_p

    def __repr__(self):
        return str(self.person_id) + " " + str(self.date_time) + " " \
               + str(self.person_id) + " " + str(self.heart_frequencies) + \
               " " + str(self.blood_pressure)


class MeasurementsModel(ModelView):
    """Admin panel controller view for Measurements and Relation class """

    def is_accessible(self):
        return True if current_user.is_authenticated else abort(404)


class UserController(ModelView):
    """ Admin Model for user creation """

    form_columns = ('user_name', 'user_surname', 'password', 'email', 'role')
    form_extra_fields = {'password': PasswordField("Mot de passe", validators=[DataRequired(), Length(min=4, max=40)]),
                         'user_name': StringField("Nom", validators=[DataRequired(), Length(min=4, max=20)]),
                         'email': EmailField("Email", validators=[DataRequired(), Email(message="Ivalid email")]),
                         'user_surname': StringField("Prénom", validators=[DataRequired(), Length(min=4, max=20)]),
                         'role': IntegerField(validators=[DataRequired(), NumberRange(min=1, max=3,
                                                                                      message="Le rôle doit être "
                                                                                              "entre 1 et 3")])
                         }

    def is_accessible(self):
        if current_user.is_authenticated :
            if 'role' in current_user.__dict__ and current_user.__dict__['role'] != 3:
                return abort(404)
            else:
                return True
        else:
            abort(404)

        return True

    def on_model_change(self, form, model, is_created):
        """ Redefining how password change will be done from admin panel """

        model.password = generate_password_hash(model.password, method="sha256")
        tmp = User.query.filter(User.email == model.email)
        if tmp:
            return False

        if is_created:
            model.registered_on = datetime.now()


def initialise_admin(admin):
    """ Initialising  admin accessible views"""
    admin.add_view(UserController(User, db.session))
    admin.add_view(MeasurementsModel(Relation, db.session))
    admin.add_links(MenuLink(name='Logout', category='', url=url_for('logout')))
