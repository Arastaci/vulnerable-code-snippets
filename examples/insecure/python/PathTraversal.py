app = Flask(__name__)
app.config['TEMPLATES_AUTO_RELOAD'] = True
Session(app)
users = {'guest':'guest'}

@app.route('/login', methods=['GET', 'POST'])
def login():
    # ... do login ...

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form.get('username')
        if username in users:
            return render_template('error.html', msg='Username already taken!', return_to='/register')
        users[username] = request.form.get('password')
        return redirect('/login')
    return render_template('register.html')

@app.route('/notes', methods=['GET', 'POST'])
def notes():
    if not session.get('username'): return redirect('/login')
    notes_file = 'notes/' + session.get('username')
    if commonpath((app.root_path, abspath(notes_file))) != app.root_path:
        return render_template('error.html', msg='Error processing notes file!', return_to='/notes')
    if request.method == 'POST':
        with open(notes_file, 'w') as f: f.write(request.form.get('notes'))
        return redirect('/notes')
    notes = ''
    if exists(notes_file):
        with open(notes_file, 'r') as f: notes = f.read()
    return render_template('notes.html', username=session.get('username'), notes=notes)