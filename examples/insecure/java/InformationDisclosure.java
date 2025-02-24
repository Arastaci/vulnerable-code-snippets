HttpServer srv = HttpServer.create(new InetSocketAddress(1337), 0);

srv.createContext("/register", he - > {
    try {
        JSONObject params = Server.getParams(he);
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        if (username == null || password == null) {
            Server.response(he, 500, "Internal Server Error");  
            return;
        }

        if (Server.user_exists(conn, username)) {
            Server.response(he, 403, "user exists");
            return;
        }

        ResultSet rs = smt.executeQuery("SELECT password FROM users");
        while (rs.next()) {
            if (rs.getString("password").startsWith(password)) {
                Server.response(he, 403, "password policy not followed");
                return;
            }
        }
    } catch (ParseException | SQLException e) {
        Server.response(he, 500, "Internal Server Error");
        return;
    }
});


srv.start();