# YAKVS

Yet Another Key Value Store

Key-Value store database server

* Java 21
* SSLSocket
* Virtual Threads
* Protocol buffers

## server.yml

* name = the name for the proto file saved on disk
* port = specify bound port
* quickstart = enable quickstart mode. Server starts with .wantClientAuth() instead of .needClientAuth()

## Keywords

* SET = Set a new key
* GET = Get a key
* DELETE = Delete a key
* COUNT = Size of entries
* KEYS = Iterate all the keys
* FLUSH = Flush the database
* SAVE = Save to disk

## Quickstart

If started with `quickstart` clients can send request to the server without need to authenticate themselves.

#### OpenSSL
* Create RSA 2048 key and Certificate


`
openssl req -newkey rsa:2048 -nodes -keyout quickstart-server-key.pem -x509 -days 365 -out quickstart-server-certificate.pem
`
* Create PKCS12 keystore with certificate


`
openssl pkcs12 -inkey quickstart-server-key.pem -in quickstart-server-certificate.pem -export -out quickstart-server-certificate.p12
`


Example client in quickstart mode. Not recommended for prod.

``` java
public static void main(String[] args) {
       try {
            var s = SSLContext.getInstance("TLS");
            s.init(null, getTrustAllCerts(), null);
            var c = new YakvsClient("localhost", SERVER_CONFIG.getPort(), s);
            c.connect();
            c.count();
            c.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
public static TrustManager[] getTrustAllCerts() {
        return new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
    }
```

## SSL

Only supports TLS version 1.3 - Clients needs to authenticate themselves.

Properties used for SSL - Set as VM Option.

* `-Djavax.net.ssl.trustStore=<YOUR TRUSTSTORE LOCATION>`
* `-Djavax.net.ssl.trustStorePassword=<YOUR TRUSTSTORE PASSWORD>`
* `-Djavax.net.ssl.trustStoreType=<YOUR TRUSTSTORE TYPE>`
* `-Djavax.net.ssl.keyStore=<YOUR KEYSTORE LOCATION>`
* `-Djavax.net.ssl.keyStorePassword=<YOUR KEYSTORE PASSWORD>`
* `-Djavax.net.ssl.keyStoreType=<YOUR KEYSTORE TYPE>`
