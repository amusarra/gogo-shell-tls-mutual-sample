/**
 * MIT License
 *
 * Gogo Shell - Command for getting HTTPS protected resources
 *
 * Copyright (C) 2020 Antonio Musarra's Blog
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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

	public void getProtectedResource(String uri) throws Exception {
		String response = null;

		try {
			response = _httpClient.URLtoString(uri);

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
					"They occurred in the service call. {EndPoint: %s} - {Error Message: %s}",
					uri, ioe.getMessage()),
				ioe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HTTPSProtectedResourcesCommand.class);

	@Reference
	private Http _httpClient;

}