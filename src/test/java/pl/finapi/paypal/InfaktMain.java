package pl.finapi.paypal;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.finapi.paypal.oauth.InfaktApi;

public class InfaktMain {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String consumerKey = "";
		String consumerSecret = "";

		String requestTokenURL = "https://www.infakt.pl/oauth/request_token";
		String accessTokenURL = "https://www.infakt.pl/oauth/access_token";
		String authorizeURL = "https://www.infakt.pl/oauth/authorize";
		String infaktWsBaseUrl = "https://www.infakt.pl/api/v2/";

		OAuthService service = new ServiceBuilder().provider(InfaktApi.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
		Verifier verifier = new Verifier("value");
		Token requestToken = service.getAccessToken(new Token("token", "secret"), verifier);
		Token requestToken2 = service.getRequestToken();
		System.out.println("");
		// service.
	}
}
