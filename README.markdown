# Gogo Shell Command for getting HTTPS protected resources via SSL/TLS Two-Way Authentication 
[![Antonio Musarra's Blog](https://img.shields.io/badge/maintainer-Antonio_Musarra's_Blog-purple.svg?colorB=6e60cc)](https://www.dontesta.it) [![Twitter Follow](https://img.shields.io/twitter/follow/antonio_musarra.svg?style=social&label=%40antonio_musarra%20on%20Twitter&style=plastic)](https://twitter.com/antonio_musarra) ![Java CI](https://github.com/amusarra/gogo-shell-tls-mutual-sample/workflows/Java%20CI/badge.svg?branch=develop)


How many of you have had to face the problems related to the realization of an 
HTTPS client towards a Mutual Authentication service? And how many of you in a 
Liferay context? I can imagine that the number is high, not to mention 
the nights spent debugging.

Suppose that from our Liferay portal we need to access one or more services 
exposed in HTTPS and protected by a Two-Way SSL/TLS Mutual Authentication. 
In this type of scenario, how can we implement the client to access the 
service? What are also the prerequisites?

![](docs/images/Liferay_HTTPS_Two-Way_Mutual_Authentication_Client_Architecure_1.png)

Liferay greatly facilitates the implementation of the HTTPS client, 
by virtue of the fact that the platform makes available the HttpUtil 
class (existing since Liferay versions 6.x) which fully matches our needs 
and which implements the interface com.liferay.portal.kernel.util.Http.

## 1. Quick Start
The requirements to be able to perform the connection test to the HTTPS service 
in Mutual Authentication are the following:

1. Liferay Portal 7.2 GA2;
2. Docker 17.x or 18.x or 19.x;
3. Configuring Liferay with the keyStore and trustStore settings. 
For more information see article [Liferay 7.2: Esempio di Two-Way SSL/TLS Mutual Authentication Client](http://bit.ly/37S42l7)
4. Clone this project repository
5. Build and Deploy bundle
6. Run Docker Container [amusarra/apache-ssl-tls-mutual-authentication](https://hub.docker.com/r/amusarra/apache-ssl-tls-mutual-authentication)
7. Run Gogo Shell Command getProtectedResource(uri)

We set the system properties for the keyStore to debug ssl connections. 
In this case, it is not necessary to set up the trustStore because the 
certificates used are issued by trusted CAs.

The following is the setenv.sh file of the Liferay 7.2 GA2 tomcat bundle 
modified to set the keyStore system properties. Once set up, start the Liferay portal.

Replace the `$LIFERAY_HOME` with your Liferay home directory and create the 
`security/keystore` directory inside it. 
You can find the keyStore `tls-client.dontesta.it.jks` in `docs/security/certs` directory.

```bash
SSL_OPTS="$SSL_OPTS -Djavax.net.ssl.keyStore=$LIFERAY_HOME/security/keystore/tls-client.dontesta.it.jks"
SSL_OPTS="$SSL_OPTS -Djavax.net.ssl.keyStorePassword=secret"
SSL_OPTS="$SSL_OPTS -Djavax.net.debug=ssl"

CATALINA_OPTS="$SSL_OPTS $CATALINA_OPTS -Dfile.encoding=UTF-8 -Djava.locale.providers=JRE,COMPAT,CLDR -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT -Xms2560m -Xmx2560m -XX:MaxNewSize=1536m -XX:MaxMetaspaceSize=768m -XX:MetaspaceSize=768m -XX:NewSize=1536m -XX:SurvivorRatio=7"
```

If you want can use the Liferay Docker image. For more info goto [Docker Hub](https://hub.docker.com/r/liferay/portal).

Add the following entry to the hosts file (/etc/hosts)

```bash
##
# Two-Way Mutual Authentication via Apache 2.4 HTTP
##
127.0.0.1       tls-auth.dontesta.it
```

At this point you have to proceed with the clone, build and deploy of the project.

```bash
$ git clone https://github.com/amusarra/gogo-shell-tls-mutual-sample.git
```

```bash
$ cd gogo-shell-tls-mutual-sample
$ ./gradlew build
$ cp build/libs/it.dontesta.labs.liferay.security.tls.auth.gogoshell-1.0.0.jar $LIFERAY_HOME/deploy
```

Starting the Docker container of HTTPS services in Mutual Authentication.

```bash
$ docker run -i -t -d -p 443:10443 --name=tls-auth amusarra/apache-ssl-tls-mutual-authentication
$ docker ps --format '{{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}\t{{.Names}}'
```
Below is an example of the output of the `docker ps` command.

![](docs/images/DockerPS_TLS-AUTH_Container.png)

Connection to Liferay's Gogo Shell (via telnet) and execution of the 
security:getProtectedResource command.

```bash
$ telnet localhost 11311

g! security:getProtectedResource https://tls-auth.dontesta.it/secure/api/headers
```

Example of expected result from the command execution.

```bash
...
verify_data:  { 209, 145, 235, 190, 40, 23, 132, 133, 8, 105, 123, 236 }
***
pipe-getProtectedResource https://tls-auth.dontesta.it/secure/api/headers, WRITE: TLSv1.2 Handshake, length = 40
pipe-getProtectedResource https://tls-auth.dontesta.it/secure/api/headers, READ: TLSv1.2 Change Cipher Spec, length = 25
pipe-getProtectedResource https://tls-auth.dontesta.it/secure/api/headers, READ: TLSv1.2 Handshake, length = 40
*** Finished
verify_data:  { 202, 185, 192, 44, 167, 181, 144, 117, 34, 115, 255, 238 }
***
%% Cached client session: [Session-10, TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384]
pipe-getProtectedResource https://tls-auth.dontesta.it/secure/api/headers, READ: TLSv1.2 Application Data, length = 864
pipe-getProtectedResource https://tls-auth.dontesta.it/secure/api/headers, setSoTimeout(0) called
{
  "headers": {
    "Accept-Encoding": "gzip,deflate",
    "Connection": "Keep-Alive",
    "Host": "tls-auth.dontesta.it",
    "User-Agent": "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv 11.0) like Gecko",
    "X-Client-Dn": "emailAddress=antonio.musarra@gmail.com,CN=antonio.musarra@gmail.com,OU=IT Labs,O=Antonio Musarra Digital Personal Certificate,ST=Italy,L=Bronte,C=IT",
    "X-Client-Verify": "SUCCESS",
    "X-Forwarded-Host": "tls-auth.dontesta.it",
    "X-Forwarded-Server": "tls-auth.dontesta.it"
  }
}
```

## 2. Resources
Following are some resources that could be useful as a summary

1. [Importazione Certificati SSL sul Java Keystore (JKS)](http://bit.ly/2to0ed9)
2. [eBook Liferay SSL/TLS Security. Come configurare il bundle Liferay per abilitare il protocollo SSL/TLS](http://bit.ly/2Xan9q9)
3. [Docker Image Apache SSL/TLS Mutual Authentication on Azure Cloud](http://bit.ly/2Sm5dU7)
4. [GitHub – amusarra/docker-apache-ssl-tls-mutual-authentication](http://bit.ly/2VjbJ1x)
5. [Apache HTTP 2.4 – Docker image for SSL/TLS Mutual Authentication](https://www.youtube.com/watch?v=VIRWJjqb0y0)

[![Liferay 7 Wildfly: How to add support for Oracle DB ](https://img.youtube.com/vi/VIRWJjqb0y0/0.jpg)](https://www.youtube.com/watch?v=VIRWJjqb0y0)

## Project License
MIT License

Gogo Shell - Command for getting HTTPS protected resources

Copyright (c) 2020 Antonio Musarra's Blog - https://www.dontesta.it

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in the
Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
