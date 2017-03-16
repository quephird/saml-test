# saml-test

## Why did I do this?

Needed to investigate SSO integration into a new clojure app
Needed to find a library, prefereabley a clojure one
Hoped to hasve test IDP at ,my disposal bbut none was
Wanted to a ffee pnline omne, couldn't find one either that worked
Didn't want to install somehting as co,mplex as shibboleth

## Goals

Self-contained project not requiring any exterma; dependeeies or comnfiguration
Demonstrate round trip from SP to IDP back to SP
implement authorixatiomn scheme

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
