package it.dontesta.labs.liferay.security.tls.auth.gogoshell;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(
	property = {
		"osgi.command.function=getProtectedResource",
		"osgi.command.scope=security"
	},
	service = Object.class
)
public class HTTPSProtectedResourcesCommand {

	public void getProtectedResource(String URI) throws Exception {
		String response = null;

		try {
			response = _httpClient.URLtoString(URI);

			if (_log.isInfoEnabled()) {
				_log.info("Response: " + response);
			}

			System.out.println(response);
		}
		catch (IOException ioe) {
			if (_log.isErrorEnabled()) {
				_log.error(ioe.getMessage(), ioe);
			}

			throw new Exception(
				String.format(
					"They occurred in the service call. {EndPoint: %s}", URI),
				ioe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HTTPSProtectedResourcesCommand.class);

	@Reference
	private Http _httpClient;

}