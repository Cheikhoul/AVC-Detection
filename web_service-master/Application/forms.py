from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, BooleanField, SubmitField, DateField, TimeField
from wtforms.fields.html5 import EmailField
from wtforms.validators import InputRequired, Length, Email, EqualTo


class LoginForm(FlaskForm):
    """ Login form for the web page. Login will be made using the email and password registered in advance
        in the database """
    email = EmailField("Email", validators=[InputRequired(), Email(message="Email invalide"), Length(min=4, max=40)])
    password = PasswordField('Mot de passe', validators=[InputRequired(), Length(min=2, max=28)])
    submit = SubmitField('Se connecter')


class RegisterPatient(FlaskForm):
    """ Register a patient in the database """

    patient_name = StringField("Nom", validators=[InputRequired(), Length(min=4, max=20)])
    patient_surname = StringField("Prénom", validators=[InputRequired(), Length(min=4, max=20)])
    password = PasswordField('Mot de passe', validators=[InputRequired(), Length(min=4, max=40)])
    email = EmailField("Email", validators=[InputRequired(), Email(message='Invalid email'), Length(min=4, max=200)])
    register = SubmitField("Enregistrer")


class ResetRequest(FlaskForm):
    """Form for password reset request using the email credentials of the giving user"""
    email = EmailField("Email", validators=[InputRequired(), Email(message='Invalid email'), Length(min=4, max=200)])
    submit = SubmitField('Reset password')


class ResetPassword(FlaskForm):
    password = PasswordField('Mot de passe', validators=[InputRequired(), Length(min=4, max=200)])
    confirm_password = PasswordField('Confirmez  le mot de passe', validators=[InputRequired(), Length(min=4, max=200),
                                                                               EqualTo('password', message="Les deux "
                                                                                                           "mot de "
                                                                                                           "passe ne "
                                                                                                           "sont pas "
                                                                                                           "identiques")
                                                                               ])
    submit = SubmitField('Change mot de passe')


class DoctorsView(FlaskForm):
    """ Form for the fields of the main view - 'doctors_view' """

    from_date = DateField("Du", validators=[InputRequired()])
    from_hour = TimeField("From", validators=[InputRequired()])
    to_date = DateField("Au", validators=[InputRequired()])
    to_hour = TimeField("Up to", validators=[InputRequired()])
