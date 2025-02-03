@WebServlet(name = "MercurialImporterServlet", urlPatterns = {"/check"})
public class MercurialImporterServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        var out = res.getOutputStream();
        if (req.getParameter("repository") == null 
            || req.getParameter("repository").indexOf("$(") != -1 
            || req.getParameter("repository").indexOf("`") != -1) {
            res.setStatus(405);
            return;
        }
        var cmd = new String[] {
            "hg",
            "identify",
            req.getParameter("repository")
        };
        var p = Runtime.getRuntime().exec(cmd);
        var br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String l;
        while ((l = br.readLine()) != null) {
            out.write(l.getBytes("ascii"));
        }
        br.close();
    }
}