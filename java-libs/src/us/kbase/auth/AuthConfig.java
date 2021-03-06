package us.kbase.auth;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/** The configuration class for the KBase auth client. In most use cases the
 * default configuration will work.
 * 
 * @author gaprice@lbl.gov
 *
 */
public class AuthConfig {
	
	//TODO write mock auth server to test alternate settings
	private static final String DEFAULT_KBASE_AUTH_SERVER_URL =
			"https://www.kbase.us/services/authorization/";
	private static final String DEFAULT_GLOBUS_AUTH_URL =
			"https://nexus.api.globusonline.org/";
	private static final String DEFAULT_KBASE_USER_GROUP_ID =
			"99d2a548-7218-11e2-adc0-12313d2d6e7f";
	private static final RefreshingToken DEFAULT_TOKEN = null;
	
	private static final String LOGIN_LOC = "Sessions/Login";
	private static final String GLOBUS_GROUPS = "groups/";
	private static final String GLOBUS_USERS = "users/";
	private static final String GLOBUS_GROUP_MEMBERS = "/members/";
			
	private URI authServerURL;
	private URI globusURL;
	private UUID kbaseUsersGroupID;
	private RefreshingToken token;
	
	/**
	 * Create a configuration object with default settings. In this case the
	 * token is null.
	 */
	public AuthConfig() {
		try {
			authServerURL = new URI(DEFAULT_KBASE_AUTH_SERVER_URL);
			globusURL = new URI(DEFAULT_GLOBUS_AUTH_URL);
		} catch (URISyntaxException use) {
			throw new RuntimeException(
					"This cannot occur. Please check with your local deity for an explanation.");
		}
		kbaseUsersGroupID = UUID.fromString(DEFAULT_KBASE_USER_GROUP_ID);
		token = DEFAULT_TOKEN;
	}
	
	/** Set the URL of the KBase authorization server
	 * @param authServer the URL of the KBase authorization server.
	 * @return this
	 * @throws URISyntaxException if the URL is not a valid URI. In general
	 * this should never happen.
	 */
	public AuthConfig withKBaseAuthServerURL(URL authServer)
			throws URISyntaxException {
		if (authServer == null) {
			throw new NullPointerException("authServer cannot be null");
		}
		if (!authServer.toString().endsWith("/")) {
			try {
				authServer = new URL(authServer.toString() + "/");
			} catch (MalformedURLException e) {
				throw new RuntimeException("This can't happen");
			}
		}
		authServerURL = authServer.toURI();
		return this;
	}
	
	/** Set the URL of the Globus Online service.
	 * @param globusAuth the Globus URL.
	 * @return this
	 * @throws URISyntaxException if the URL is not a valid URI. In general
	 * this should never happen.
	 */
	public AuthConfig withGlobusAuthURL(URL globusAuth)
			throws URISyntaxException {
		if (globusAuth == null) {
			throw new NullPointerException("globusAuth cannot be null");
		}
		if (!globusAuth.toString().endsWith("/")) {
			try {
				globusAuth = new URL(globusAuth.toString() + "/");
			} catch (MalformedURLException e) {
				throw new RuntimeException("This can't happen");
			}
		}
		globusURL = globusAuth.toURI();
		return this;
	}
	
	/** Set the ID of the group in Globus Online to use when querying users.
	 * @param groupID the ID of the  group in Globus.
	 * @return this
	 */
	public AuthConfig withKBaseUsersGroupID(final UUID groupID) {
		if (groupID == null) {
			throw new NullPointerException("groupID cannot be null");
		}
		kbaseUsersGroupID = groupID;
		return this;
	}
	
	/** Set the token to use when querying users in Globus Online. This token
	 * is used when validating user names and fetching user details. Note that
	 * in order to see all users in the specified group, the user this token
	 * represents must be an administrator of the group. Otherwise users with
	 * private profiles will not be visible.
	 * @param token the token to use for Globus queries.
	 * @return this
	 */
	public AuthConfig withRefreshingToken(final RefreshingToken token) {
		if (token == null) {
			throw new NullPointerException("token cannot be null");
		}
		this.token = token;
		return this;
	}

	/** Returns the configured KBase authorization service URL.
	 * @return the authorization service URL.
	 */
	public URL getAuthServerURL() {
		try {
			return authServerURL.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never happen");
		}
	}

	/** Returns the configured Globus Online URL.
	 * @return the Globus URL.
	 */
	public URL getGlobusURL() {
		try {
			return globusURL.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never happen");
		}
	}

	/** Returns the configured Globus Online group ID used when querying users.
	 * @return the Globus group ID.
	 */
	public UUID getKbaseUsersGroupID() {
		return kbaseUsersGroupID;
	}

	/** Returns the configured token used when querying users.
	 * @return the token.
	 */
	public RefreshingToken getToken() {
		return token;
	}
	
	/** Returns the full URL used for logging in a user with the KBase
	 * authorization service.
	 * @return the auth service login URL.
	 */
	public URL getAuthLoginURL() {
		try {
			return authServerURL.resolve(LOGIN_LOC).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never happen");
		}
	}
	
	/** Returns the full URL used for querying users with the Globus Online
	 * service within a specified group.
	 * 
	 * NOTE: not all valid KBase users are part of the KBase Globus Group! So
	 * this URL should be used with extreme caution for looking up user information.
	 *
	 * @return the Globus user query URL.
	 */
	public URL getGlobusGroupMembersURL() {
		try {
			return globusURL.resolve(GLOBUS_GROUPS +
					kbaseUsersGroupID.toString() + GLOBUS_GROUP_MEMBERS).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never happen");
		}
	}

	/** Returns the full URL used for querying users with the Globus Online
	 * service for any registered user regardles of group.

	 * @return the Globus user query URL.
	 */
	public URL getGlobusUsersURL() {
		try {
			return globusURL.resolve(GLOBUS_USERS).toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never happen");
		}
	}

}
