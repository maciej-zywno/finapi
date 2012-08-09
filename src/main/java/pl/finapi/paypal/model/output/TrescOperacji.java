package pl.finapi.paypal.model.output;

public enum TrescOperacji {

	KUPNO_WALUTY,
	SPRZEDAZ_WALUTY,
	WPLYW_SRODKOW,
	WYPLYW_SRODKOW,
	SALDO_POCZATKOWE,
	SALDO_KONCOWE,
	OBA_SALDA_WALUTOWE;

	public boolean isSaldoPoczatkoweLubKoncowe() {
		return this == TrescOperacji.SALDO_POCZATKOWE || this == TrescOperacji.SALDO_KONCOWE;
	}

}
