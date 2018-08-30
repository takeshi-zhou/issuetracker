from wtforms import Form, IntegerField, StringField, BooleanField
from wtforms.validators import NumberRange, UUID, DataRequired, InputRequired, Length


class CommitForm(Form):
    repo_id = StringField(validators=[UUID(), DataRequired(), InputRequired()])
    page = IntegerField(validators=[NumberRange(min=1)], default=1)
    per_page = IntegerField(validators=[NumberRange(min=1, max=100)], default=50)
    is_whole = BooleanField(default=False)

class CommitTimeForm(Form):
    pass

class CheckoutForm(Form):
    commit_id = StringField(validators=[DataRequired(), Length(min=40, max=40)])
    repo_id = StringField(validators=[UUID(), DataRequired(), InputRequired()])

class CheckoutMasterForm(Form):
    repo_id = StringField(validators=[UUID(), DataRequired(), InputRequired()])