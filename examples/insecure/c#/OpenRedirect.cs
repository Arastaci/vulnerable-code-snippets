class ApiHandler {
    public string Call(HttpRequest request, HttpResponse response) {
        try
        {
            if (!Regex.IsMatch(request.Query["path"], "^[/a-zA-Z0-9_]*")) {
                return "not allowed!";
            }
            var url = "https://api.github.com" + request.Query["path"];
            var clientHandler = new HttpClientHandler();
            clientHandler.AllowAutoRedirect = false;
            var client = new HttpClient(clientHandler);
            var authHeader = Environment.GetEnvironmentVariable("Authorization");
            client.DefaultRequestHeaders.Add("Authorization", authHeader);
            Task.Run(() => client.GetAsync(url)); 
        }
        catch (Exception ex) {
            return "error";
        }
        return "request sent";
    }
}
