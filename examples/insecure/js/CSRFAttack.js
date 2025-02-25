const csrfProtect = require('csurf')({ cookie: true });
app.use(session({
    secret: process.env.SECRET,
    cookie: {
        secure: true,
        sameSite: 'none',
    },
}));

app.post('/upload', parseForm, csrfProtect, async (req, res) => {
    const f = req.files.template;
    if (path.extname(f.name) !== '.txt') {
        return res.status(400).send();
    }
    const id = uuid.v4();
    await f.mv(`public/uploads/${id}`);
    return res.json({id});
});

app.get('/exportPDF', csrfProtect, async (req, res) => {
    if (!req.query.id) {
        return res.status(400).send();
    }
    const id = path.basename(req.query.id);
    const dst = `public/export/${id}.pdf`;
    const f = buildForm(`public/uploads/${id}`).replaceAll('{csrf_token}', req.csrfToken());
    await fs.writeFile(dst, f);
    return res.send(`<a href="${escape(dst)}">Your PDF!<a>`);
});