<jsp:root version="2.0" xmlns="http://www.w3.org/1999/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:templates="urn:jsptagdir:/WEB-INF/tags/templates" xmlns:form="http://www.springframework.org/tags/form">

	<jsp:directive.page contentType="text/html" />

	<templates:main>
		<jsp:attribute name="content">

			<div class="container">

				<div id="left">
					<div id="slogan-1">
						<div class="slogan-text-1">Sprzedajesz na Ebay lub Amazon?</div>
						<div class="slogan-text-1">Przyjmujesz wpłaty w Euro, USD, GBP?</div>
						<div class="slogan-text-1">Masz konto PayPal, DeutscheBank, Sparkasse?</div>
					</div>
					<div id="slogan-2">
						<div class="slogan-text-2">Tutaj samodzielnie, jednym kliknięciem i za darmo utworzysz wymagane dokumenty księgowe:</div>
						<ul>
							<li>ewidencje różnic kursowych</li>
							<li>ewidencje prowizji Paypal</li>
							<li>inne powiązane dokumenty (dowody wewnętrzne i faktury wewnętrzne)</li>
						</ul>
					</div>
					<div id="slogan-2">
						<div class="slogan-text-2">
Prowadzenie księgowości przy sprzedaży zagranicznej wymaga czasochłonnego i ręcznego analizowania historii transakcji, 
tak aby poprawnie obliczyć <b>różnice kursowe</b>.<br />
							<br /> Teraz możesz skorzystać z darmowego generatora Finapi, który poprawną dokumentację 
utworzy automatycznie na podstawie historii Paypal i kursów wymiany walut dostępnych na stronie NBP.
										</div>
					</div>
				</div>
				<div id="right">
					<div id="uploadFileForm">
						<form:form modelAttribute="uploadItem" method="post" enctype="multipart/form-data">
							<p>
								<form:label for="fileData" path="fileData">Utwórz ewidencje na podstawie historii transakcji PayPal:</form:label>
								<br />
								<br />
								<form:input path="fileData" type="file" value="Wybierz plik" />
							</p>
							<p>
								<form:input path="fileData" type="submit" value="Wygeneruj dokumenty księgowe" />
							</p>
							<p>
								<form:checkbox value="false" id="addCompanyName" path="addCompanyName" label="Umieść moje dane firmowe w utworzonych dokumentach" 
									onclick="if(document.getElementById('addCompanyName').checked) {
												document.getElementById('companyData').style.display='block';
												document.getElementById('companyData').style.visibility='visible';
											}else {
												document.getElementById('companyData').style.display='none';
												document.getElementById('companyData').style.visibility='hidden';
											}"									
								/>
							</p>
							<div id="companyData" style="visibility: hidden">
								<p>
									<form:label for="companyName" path="companyName">Nazwa firmy </form:label>
									<form:input path="companyName" type="text" value="P.H.U Biedronka" />
								</p>
								<p>
									<form:label for="city" path="city">Miasto </form:label>
									<form:input path="city" type="text" value="Zgorzelec" size="10" />
									<form:label for="zipcode" path="zipcode"> Kod </form:label>
									<form:input path="zipcode" type="text" value="98-765" size="3" maxLength="6" />
								</p>
								<p>
									<form:label for="address" path="address">Adres </form:label>
									<form:input path="address" type="text" value="ul. Piękna 7 m.2" />
								</p>
								<p>
									<form:label for="nip" path="nip">NIP</form:label>
									<form:input path="nip" type="text" value="1234567890" />
								</p>
								<p>
									<form:label for="wystawil" path="wystawil">Sporządził (Twoje imię i nazwisko)</form:label>
									<br />
									<form:input path="wystawil" type="text" value="Jan Kowalski" />
								</p>
								<p>
									<form:label for="email" path="email">Paypal email</form:label>
									<br />
									<form:input path="email" type="text" value="jan.kowalski@gmail.com" />
								</p>
							</div>
						</form:form>
					</div>
					<div id="youtubeMovie">
						<!--iframe width="470" height="315" src="http://www.youtube.com/embed/us8Vq7cEOtA" frameborder="0" allowfullscreen="false">
						</iframe-->
					</div>
				</div>
			</div>
		</jsp:attribute>
	</templates:main>
</jsp:root>