# saml-test

## Why did I do this?

At $DAYJOB, I was charged with investigating how to integrate Single Sign-On (SSO) into a potential new Clojure-based application.
And so, I needed to find a library, preferably one in Clojure not Java since I have found Clojure libraries to be far simpler and easier to work with.
I also hoped to have test Identity Provider (IDP) server at my disposal but our company had none already, and we weren't yet ready to partner with an actual provider.
I searched for a free online service, but could not find one that either worked or for which I could successfully set up an account.
(I tried [https://openidp.feide.no/](https://openidp.feide.no/) but just got nowhere.)
And I _really_ didn't want to have to install and configure something as complex as Shibboleth.

## Goals

Ideally, I wanted a completely self-contained project which fulfilled the following:

* Exposes a minimally functional Service Provider (SP) endpoint
* Uses a Clojure library that "spoke" SAML
* Does not require any external installation and configuration of an IDP
* Demonstrates a round trip conversation from SP to IDP and back to SP
* Implements a minimal authorization scheme

## Explanation of SSO using SAML

TODO: Use JavE for diagram.

               IDP

    User

               SP

## Getting things running

#### Clone this repo

Download this project to a local directory:

    git clone https://github.com/quephird/saml-test

#### Download and build IDP

`cd` into the `saml-test` directory.
Then download and build the IDP project; this can all be done through Leiningen:

    lein npm install  
    lein bower install

#### Generate keystore

Create a new keystore, and generate a new certificate for the SP in it:

    keytool -keystore keystore.jks -genkey -alias mylocalsp

Create a new certificate for the IDP:

    openssl req -x509 -new -newkey rsa:2048 -nodes \
        -subj '/C=US/ST=Some state/L=Some city/O=Some org/CN=Local IDP' \
        -keyout ./node_modules/saml-idp/idp-private-key.pem \
        -out ./node_modules/saml-idp/idp-public-cert.pem \
        -days 7300

Import the IDP certificate into the new keystore:

    keytool -import -keystore keystore.jks \
        -file ./node_modules/saml-idp/idp-public-cert.pem \
        -alias mylocalidp

Check to see that both certificates are in fact in the keystore:

    keytool -list -keystore keystore.jks

You should see something like this:

    Keystore type: JKS
    Keystore provider: SUN
    
    Your keystore contains 2 entries
    
    mylocalsp, Mar 10, 2017, PrivateKeyEntry,
    Certificate fingerprint (SHA1): 01:EE:4C:D0:46:F2:1D:31:08:EF:ED:1C:E2:CF:E7:AD:73:4F:6E:EB
    mylocalidp, Mar 14, 2017, trustedCertEntry,
    Certificate fingerprint (SHA1): AE:F1:34:11:2E:C3:6A:FE:B9:52:35:66:68:2F:F8:72:2F:0A:7E:28

#### Start servers

In one session, start the IDP:

    cd ./node_modules/saml-idp/
    node app.js \
        --acs http://localhost:8081/saml \
        --aud http://localhost:8081/saml

In another session, start the SP:

    lein run

#### Test SSO

TODO: Flesh this out

## Important links

`lein-npm`  
[https://github.com/RyanMcG/lein-npm](https://github.com/RyanMcG/lein-npm)


`lein-bower`  
[https://github.com/chlorinejs/lein-bower](https://github.com/chlorinejs/lein-bower)


`saml-idp`  
[https://github.com/mcguinness/saml-idp](https://github.com/mcguinness/saml-idp)
  
## License

Copyright (C) 2017, ⅅ₳ℕⅈⅇℒℒⅇ Ҝⅇℱℱoℜⅆ.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
