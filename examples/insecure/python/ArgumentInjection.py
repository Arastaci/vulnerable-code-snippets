def _git(cmd, args, cwd='/'):
    proc = run(['git', cmd, *args],
               stdout=PIPE,
               stderr=DEVNULL,
               cwd=cwd,
               timeout=5)
    return proc.stdout.decode().strip()

@app.route('/blame', methods=['POST'])
def blame():
    url = request.form.get('url',
                           'https://github.com/package-url/purl-spec.git')
    what = request.form.getlist('what[]')
    with TemporaryDirectory() as local:
        if not url.startswith(('https://', 'http://')):
            return make_response('Invalid url!', 403)
        _git('clone', ['--', url, local])
        res = []
        for i in what:
            file, lines = i.split(':')
            res.append(_git('blame', ['-L', lines, file], local))
        return make_response('\n'.join(res), 200)