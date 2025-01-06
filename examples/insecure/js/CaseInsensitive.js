const express = require('express');
const auth = require('./auth');
const app = express();

app.use((req, res, next) => {
  if (req.url.startsWith('/api')) {
    const allowed = auth.checkToken(req);
    if (!allowed) {
      return res.status(401).send('missing auth token!');
    }
  }

  next();
});

app.use('/static', express.static('./static'));
app.use('/api', require('./api'));

app.listen(1337);
