<%-- 
    Document   : manual-wekaproses
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
           //melakukan proses weka dari teks
            String hasil = NewsClassifier.NaiveBayesClassification(request.getParameter("judulmanual"), request.getParameter("kontenmanual"), "naiveBayesModel.ARFF");
			out.println(hasil);
		//nanti ditambahin lagi sama method dari newsclassifier buat ngeproses file hasil upload
           //forward ke halamna hasil
           //response.sendRedirect("hasil.html");
        %>
    </body>
</html>
