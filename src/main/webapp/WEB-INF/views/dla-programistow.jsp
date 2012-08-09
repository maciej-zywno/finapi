<jsp:root version="2.0" xmlns="http://www.w3.org/1999/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:templates="urn:jsptagdir:/WEB-INF/tags/templates" xmlns:form="http://www.springframework.org/tags/form">

	<jsp:directive.page contentType="text/html" />

	<templates:main>
		<jsp:attribute name="content">

			<div class="container">

				<div id="dla-programistow">
					<div class="slogan-text-2">
						Serwis Finapi.pl poprzez API udostępnia zewnętrznym systemom następujące funkcje:
						<br />
						<br />
						<b>1. Kurs walut NBP dla dnia poprzedzającego</b>
						<br />
						<br />

Przykładowe wywołania:<br />
						<br />
						<jsp:text>
							<![CDATA[<a href="http://finapi.pl/api/?waluta=USD&data=2012-03-08">http://finapi.pl/api/?waluta=USD&data=2012-03-08</a>]]>
						</jsp:text>
						<br />
						<jsp:text>
							<![CDATA[<a href="http://finapi.pl/api/?waluta=EUR&data=2012-02-13">http://finapi.pl/api/?waluta=EUR&data=2012-02-13</a>]]>
						</jsp:text>
						<br />
						<br />


						<b>2. Kurs walut NBP</b>
						<br />
						<br />

Przykładowe wywołania:<br />
						<br />
						<jsp:text>
							<![CDATA[<a href="http://finapi.pl/api/?waluta=USD&data=2012-03-08">http://finapi.pl/api/?waluta=USD&data=2012-03-08</a>]]>
						</jsp:text>
						<br />
						<jsp:text>
							<![CDATA[<a href="http://finapi.pl/api/?waluta=EUR&data=2012-02-13">http://finapi.pl/api/?waluta=EUR&data=2012-02-13</a>]]>
						</jsp:text>
						<br />
						<br />


						<b>3. Generacja dokumentów księgowych w formacie PDF</b>
						<br />
						<br />
						<![CDATA[http://finapi.pl/api/]]>
						<br />
HTTP POST multipart/form-data, form id="uploadItem" 
<br />
						<br />
						<b>4. Integracja z infakt.pl i iFirma.pl</b>
						<br />
						<br />
Serwis Finapi.pl umożliwia również automatyczne dodawanie wygenerowanych 
dokumentów do Twojej dokumentacji księgowej prowadzonej w serwisach iFirma.pl i infakt.pl 
(zgodnie z dokumentacją <a href="http://www.ifirma.pl/dla-uzytkownikow/api.html">ifirma.pl/dla-uzytkownikow/api.html</a> i 
<a href="http://www.infakt.pl/api_infakt">infakt.pl/api_infakt</a>)
<br />
						<br />
Taka integracja wymaga udostępnienia nam danych potrzebnych do autoryzacji w podanych serwisach - prosimy o kontakt na api@finapi.pl lub pod nr 532 434 927    

										</div>
				</div>
			</div>
		</jsp:attribute>
	</templates:main>
</jsp:root>