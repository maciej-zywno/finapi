package pl.finapi.paypal.oauth;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class InfaktApi extends DefaultApi10a {
	
	private static final String AUTHORIZE_URL = "https://www.infakt.pl/oauth/authorize";
	private static final String REQUEST_TOKEN_RESOURCE = "www.infakt.pl/oauth/request_token";
	private static final String ACCESS_TOKEN_RESOURCE = "www.infakt.pl/oauth/access_token";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://" + ACCESS_TOKEN_RESOURCE;
	}

	@Override
	public String getRequestTokenEndpoint() {
		return "https://" + REQUEST_TOKEN_RESOURCE;
	}

	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}

	public static class SSL extends InfaktApi {
		@Override
		public String getAccessTokenEndpoint() {
			return "https://" + ACCESS_TOKEN_RESOURCE;
		}

		@Override
		public String getRequestTokenEndpoint() {
			return "https://" + REQUEST_TOKEN_RESOURCE;
		}
	}

	/**
	 * Twitter 'friendlier' authorization endpoint for OAuth.
	 * 
	 * Uses SSL.
	 */
	public static class Authenticate extends SSL {
		private static final String AUTHENTICATE_URL = "https://api.twitter.com/oauth/authenticate?oauth_token=%s";

		@Override
		public String getAuthorizationUrl(Token requestToken) {
			return String.format(AUTHENTICATE_URL, requestToken.getToken());
		}
	}

	/**
	 * Just an alias to the default (SSL) authorization endpoint.
	 * 
	 * Need to include this for symmetry with 'Authenticate' only.
	 */
	public static class Authorize extends SSL {
	}
}