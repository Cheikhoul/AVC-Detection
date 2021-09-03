from Application.models import *
db.create_all()
u = User.query.filter(User.email == "admin@admin.admin")

if not u:
    print("User can't be created, please check your database")

else:
    u = User("", "", "admin", "admin@admin.admin", 3)
    db.session.add(u)
    db.session.commit()
    print("User created!")