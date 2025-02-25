Main.java:

HttpServer srv = HttpServer.create(new InetSocketAddress(9000), 0);
srv.createContext("/", new HttpHandler() {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String comments = "<!DOCTYPE html>\n<html><head><title>Comments</title></head><body><table>";
        try (Statement stmt = Main.conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM comments");
            while (rs.next()) {
                comments += "<tr><td>" + rs.getString("comment") + "</td></tr>";
            }
        } catch (SQLException e) {
            Main.response(he, 500, "Internal Server Error");
        }
        Main.response(he, 200, comments + "</table></body></html>");
    }
});

srv.createContext("/comment", new HttpHandler() {
    @Override
    public void handle(HttpExchange he) throws IOException {
        try (var stmt = Main.conn.prepareStatement("INSERT INTO comments (comment) VALUES (?)")) {
            Source comment =  new StreamSource(he.getRequestBody());
            Source xslt = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("comment.xslt"));
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = tf.newTransformer(xslt);
            StringWriter writer = new StringWriter();
            transformer.transform(comment, new StreamResult(writer));
            stmt.setString(1, writer.getBuffer().toString());
            stmt.executeUpdate();
            Main.response(he, 200, "Ok");
        } catch (Exception e) {
            Main.response(he, 500, "Internal Server Error");
        }
    }
});
srv.start();

-----------------------------------------------------------------------------

comment.xslt:

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" />
    <!-- Allow the following tags: <b>, <i> and <u> -->
    <xsl:template match="//b | //i | //u">
        <xsl:element name="{local-name()}">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    <!-- Allow links to https://example.com -->
    <xsl:template match="//*[@href]">
        <xsl:element name="{local-name()}">
            <xsl:attribute name="href">
                <xsl:choose>
                    <xsl:when test="starts-with(@href, 'https://example.com/')">
                        <xsl:value-of select="@href"/>
                    </xsl:when>
                    <xsl:otherwise>/</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>