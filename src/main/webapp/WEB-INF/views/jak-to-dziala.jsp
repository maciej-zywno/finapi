<jsp:root version="2.0" xmlns="http://www.w3.org/1999/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:templates="urn:jsptagdir:/WEB-INF/tags/templates" xmlns:form="http://www.springframework.org/tags/form">

	<jsp:directive.page contentType="text/html" />

	<templates:main>
		<jsp:attribute name="content">

			<div class="container">

				<div id="jak-to-dziala">
					<div class="slogan-text-2">
Na podstawie historii transakcji Paypal generator Finapi.pl tworzy cztery rodzaje dokumentów księgowych:<br />
						<br />- <b>ewidencje różnic kursowych dla wszystkich walut</b> (<img src="/resources/img/pdf_icon.gif" width="15" />
						<a href="/resources/pdf/EwidencjaRoznicKursowych.pdf">przykładowa ewidencja w Euro</a> )
						<br />- <b>ewidencje prowizji PayPal</b> (<img src="/resources/img/pdf_icon.gif" width="15" />
						<a href="/resources/pdf/EwidencjaProwizjiPaypal.pdf">przykładowa ewidencja</a> )
						<br />- <b>dowody wewnętrzne</b> dla ewidencji różnic kursowych - osobne dla dodatnich i ujemnych różnic (<img src="/resources/img/pdf_icon.gif" width="15" />
						<a href="/resources/pdf/DowodWewnetrzny-rozniceKursowe-dodatnie.pdf">przykładowy dowód - różnice dodatnie</a> i <img src="/resources/img/pdf_icon.gif" width="15" />
						<a href="/resources/pdf/DowodWewnetrzny-rozniceKursowe-ujemne.pdf">przykładowy dowód - różnice ujemne</a> )
						<br />- <b>faktury wewnętrzne</b> dla ewidencji prowizji(<img src="/resources/img/pdf_icon.gif" width="15" />
						<a href="/resources/pdf/FakturaWewnetrznaPaypal.pdf">przykładowa faktura</a> )
						<br />
						<br />
Generator Finapi.pl analizuje historię transakcji PayPal (pliki zbliżone do plików "Excel") wykorzystując autorskie algorytmy dopasowane 
do wybranej metody obliczania różnic kursowych (tzw. <b>metoda FIFO, LIFO, średnia ważona</b>). 
Generator bierze pod uwagę wszystkie operacje wpływające na salda walutowe (np sprzedaż i zwrot towaru, zwrot prowizji, blokada płatności, płatności oczekujące), a także właściwe przewalutowania (podczas ktorych "realizują się" różnice kursowe).
						<br />
						<br />
Różnice kursowe obliczane są na podstawie różnic między kursem walutowym faktycznym tj. zastosowanym przez PayPal a kursem walutowym ogłaszanym przez NBP na dzień poprzedzający.
						<br />
						<br />
Masz pytania? Chętnie udzielimy odpowiedzi - zadzwoń <b>532 434 927</b> lub napisz na <b>kontakt@finapi.pl</b>
					</div>
				</div>
			</div>
		</jsp:attribute>
	</templates:main>
</jsp:root>