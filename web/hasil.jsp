<%-- 
    Document   : hasil.jsp
    Author     : Nisa Dian Rachmadi, Try Ajitiono
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- Style -->
        <link rel="stylesheet" href="assets/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="assets/css/style.css">
        
        <!-- Title -->
        <title>FRANC News Analyst - Hasil Klasifikasi</title>
    </head>
    <body>
        <img src="">
        <!-- logo-->
        <div id="head" align="center">
            <a href="index.html">
                <span class="judul">FRANC</span>
                <span class="detail">News Analyst</span>
            </a>
        </div>
        <div id="berita" align="center">
            <div id="status" class="centered-pills">
                <ul class="nav nav-pills" >
                  <li role="presentation" class="disabled"><a href="#">Pilih cara input</a></li>
                  <li role="presentation" class="disabled"><a href="#">Masukan berita</a></li>
                  <li role="presentation" class="disabled active"><a href="#">Lihat hasilnya</a></li>
                </ul>
            </div>
            <section>
                <!-- Isi ini dengan bagian akurasi nantinya -->
            </section>
            <table>
                <tr>
                    <th>Judul artikel masuk kategori:</th>
                </tr>
                <tr>
                    <td>cat1</td>
                </tr>
                <tr>
                    <td>cat1</td>
                </tr>
                <tr>
                    <td>cat1</td>
                </tr>
            </table>
            <div id="feedback">
                <span class="change">
                    Change category!
                </span>
            <nav class="drop">
                <ul>
                    <li>
                        <form>
                            <nav>
                                <ul>
                                    <li><input type="checkbox" value="cat1" name="cat1">cat 1</li>
                                    <li><input type="checkbox" value="cat2" name="cat2">cat 2 </li>
                                    <li><input type="checkbox" value="cat3" name="cat3">cat 3</li>
                                    <li><input type="checkbox" value="cat4" name="cat4">cat 4 </li>
                                    <li><input type="checkbox" value="cat5" name="cat5">cat 5</li>
                                    <li><input type="checkbox" value="cat6" name="cat6">cat 6</li> 
                                    <li><input type="checkbox" value="cat7" name="cat7">cat 7</li>
                                    <li><input type="checkbox" value="cat8" name="cat8">cat 8</li> 
                                    <li><input type="checkbox" value="cat9" name="cat9">cat 9</li>
                                    <li><input type="checkbox" value="cat10" name="cat10">cat 10</li> 
                                </ul>
                                <input class="btn btn-primary" type="submit" value="submit">
                            </nav>
                        </form>
                    </li>
                </ul>
            </nav>
        </div>
        </div>
        <footer>
        </footer>
    </body>
</html>
