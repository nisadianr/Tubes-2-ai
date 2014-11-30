<%-- 
    Document   : upload-wekaproses
    Author     : Try Ajitiono
--%>

<%@page import="newsclassifier.FileUploader"%>
<%@page import="newsclassifier.NewsClassifier"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Redirecting... FRANC News Analyst</title>
    </head>
    <body>
        <%
           FileUploader fu = new FileUploader();
           fu.uploadFile(request);
           //nanti ditambahin lagi sama method dari newsclassifier buat ngeproses file hasil upload
           //forward ke halamna hasil
           response.sendRedirect("hasil.html");
        %>
    </body>
</html>
