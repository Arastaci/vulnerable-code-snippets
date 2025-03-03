MemcachedConnector mcc = new MemcachedConnector("memcached", 11211);
mcc.set("welcome_en", "Hi there!");
mcc.set("welcome_de", "Hallo!");
mcc.set("welcome_fr", "Bonjour!");
mcc.set("auth_backend", "http://192.168.64.2:8000/");

HttpServer srv = HttpServer.create(new InetSocketAddress(9000), 0);

srv.createContext("/", (HttpExchange he) -> {
    String lang = "en";
    if (he.getRequestURI().getQuery() != null) {
        for (String param : he.getRequestURI().getQuery().split("&")) {
            String[] entry = param.split("=");
            if (entry[0].equals("lang")) lang = entry[1];
        }
    }
    String welcomeMessage = mcc.get("welcome_" + lang);
    Main.response(he, 200, welcomeMessage);
});

srv.createContext("/login", (HttpExchange he) -> {
    try {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(he.getRequestBody(), "UTF-8"));
        String authBackend = mcc.get("auth_backend");
        String body = "username=" + jsonObject.get("username") + "&password=" + jsonObject.get("password");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(authBackend))
                                     .POST(HttpRequest.BodyPublishers.ofString(body))
                                     .headers("Content-Type", "application/x-www-form-urlencoded").build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            Main.response(he, 200, "Welcome!\n");
            return;
        }
    } catch (Exception exp) { }
    Main.response(he, 403, "Login failed!\n");
});

srv.start();