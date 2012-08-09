<jsp:root version="2.0" xmlns="http://www.w3.org/1999/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:templates="urn:jsptagdir:/WEB-INF/tags/templates" xmlns:form="http://www.springframework.org/tags/form">

	<jsp:directive.page contentType="text/html" />

	<templates:main>
		<jsp:attribute name="content">

			<div class="container">

					<div class="slogan-text-2">
Wystąpił błąd. Upewnij się, że wysyłany raport Paypal zawiera wymagane kolumny:
<br/>
<br/>"Data"
<br/>"Godzina"
<br/> "Strefa czasowa"
<br/>"Typ"
<br/>"Status"
<br/>"Waluta"
<br/>"Brutto"
<br/>"Opłata"
<br/>"Netto"
<br/>"Saldo"
<br/><br/>
Jeśli błąd powtórzy się, wyślij raport Paypal bezpośrednio na kontakt@finapi.pl lub skontaktuj się z nami telefonicznie (532 434 927)
					</div>
			</div>
		</jsp:attribute>
	</templates:main>
</jsp:root>