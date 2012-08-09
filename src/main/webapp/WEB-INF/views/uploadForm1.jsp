<%@page contentType="text/html;charset=UTF-8" %>
	<%@page pageEncoding="UTF-8" %>
		<%@ page session="false" %>
			<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
				<html>
					<head>
						<META http-equiv="Content-Type" content="text/html;charset=UTF-8" />
						<title>Upload Example</title>
					</head>
					<body>
						<form:form modelAttribute="uploadItem" method="post" enctype="multipart/form-data">
							<fieldset>
								<legend>Generacja dokumentów księgowych</legend>
								<p>
Sprzedajesz na ebay? Przyjmujesz wplaty przez Paypal w EUR, USD, GBP? Jesli tak to juz wiesz (lub powinienes wiedziec), ile czasu zajmuje rozliczanie roznic kursowych i ksiegowanie/wyliczanie prowizji pobieranych przez Paypal.
<br />
									<br /> 
Mimo, ze wszystkie dane potrzebne do <b>prawidlowego </b> rozliczenia sa dostepne online to reczne wklikanie ich do excela, odpowiednie przemnozenie przez odpowiedni kurs walutowy i zsumowanie wszystkich danych zajmuje co miesiac wiele godzin. To nie wszystko (link do miejsca, gdzie opisane beda wszsytkie myki mogace zaskoczyc np "platnosc nieautoryzowana karta", "blokada konta" itp) a i tak na koniec zastanawiasz sie czy nie popelniles gdzies bledu.
<br />
									<br /> 
Teraz wszystko to dostepne w sekunde dzieki generatowi, ktory automatycznie sumuje Twoje transakcje na Paypal i oblicza roznice kursowe na podstawie danych udostepnianych przez NBP.
</p>
								<p>
									<form:label for="fileData" path="fileData">Raport paypal</form:label>
									<br />
									<form:input path="fileData" type="file" value="Wybierz plik" />
								</p>

								<p>
									<input type="submit" value="Pobierz ewidencję prowizji Paypal" />
								</p>

							</fieldset>
						</form:form>
					</body>
				</html>