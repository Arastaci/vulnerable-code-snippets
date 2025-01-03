HttpServer srv = HttpServer.create(new InetSocketAddress(9000), 0);
srv.createContext("/", new HttpHandler() {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String ret = "<!DOCTYPE html><html><head><title>Comments</title></head><body><table>";
        try {
            ResultSet rs = statement.executeQuery("select * from comments");
            while (rs.next()) {
                String comment = rs.getString("comment").replace("<", "&lt;").replace(">", "&gt;");
                ret += "<tr><td>" + Normalizer.normalize(comment, Normalizer.Form.NFKC) + "</td></tr>\n";
            }
            Main.response(he, 200, ret + "</table></body></html>");
        } catch (Exception exp) {
            System.out.println(exp);
            Main.response(he, 500, "Internal Server Error");
        }
    }
});

srv.createContext("/comment", new HttpHandler() {
    @Override
    public void handle(HttpExchange he) throws IOException {
        try {
            JSONObject jsonObject = (JSONObject)(new JSONParser()).parse(new InputStreamReader(he.getRequestBody(), "UTF-8"));
            PreparedStatement stmt = finalConnection.prepareStatement("insert into comments values(?)");
            stmt.setString(1, (String)jsonObject.get("comment"));
            stmt.executeUpdate();
            Main.response(he, 200, "Ok");
        } catch (Exception exp) {
            Main.response(he, 500, "Internal Server Error");
        }
    }
});
srv.start();