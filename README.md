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

Below is a diagram illustrating the conversation between all three parties in this demo project.

```
                                                    +-------+
             --------------------4------------------|  IDP  |
            |  ------------------3----------------->+-------+
            | |
            | |
            | |
            | |
            v |
         +-------+
        -|  User |
       | +-------+
       |    ^ |
       |    | |
       |    | |
       |    | |
       |    | |
       |    | -------------------1----------------->+-------+
       |     --------------------2------------------|  SP   |
       |                                            +-------+
        ----------------------5-------------------------^

```

`1` represents the first request made by the user to enter into the SP.  
The SP responds by sending a 302 back with the IDP URL to the user in `2`.  
The user's browser then makes its first request to the IDP with the return URL for the SP in `3`.  
Upon successful authentication into the IDP, it sends another 302 to the user in `4`.  
Finally, the user returns to the SP with a SAML payload from the IDP in `5`.  

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

Now in your browser go to [http://localhost:8081](http://localhost:8081); you should see something like this:

![SP front door](images/SP_front_door.png)

Click the button and you should be taken to the IDP with a screen resembling this:

![IDP_login](images/IDP_login.png)

This is a mock IDP so there isn't actually any authentication implementation but that's ok; 
all we really want to demonstrate is that we can have the IDP send a valid SAML response back to the SP.

## Important links

Wikipedia article on SAML  
[https://en.wikipedia.org/wiki/Security_Assertion_Markup_Language](https://en.wikipedia.org/wiki/Security_Assertion_Markup_Language)

`saml20-clj`  
[https://github.com/vlacs/saml20-clj](https://github.com/vlacs/saml20-clj)

`lein-npm`  
[https://github.com/RyanMcG/lein-npm](https://github.com/RyanMcG/lein-npm)


`lein-bower`  
[https://github.com/chlorinejs/lein-bower](https://github.com/chlorinejs/lein-bower)


`saml-idp`  
[https://github.com/mcguinness/saml-idp](https://github.com/mcguinness/saml-idp)
  
## License

Copyright (C) 2017, ⅅ₳ℕⅈⅇℒℒⅇ Ҝⅇℱℱoℜⅆ.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
