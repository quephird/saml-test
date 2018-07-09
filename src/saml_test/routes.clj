(ns saml-test.routes
  (:require [taoensso.timbre :as timbre :refer [log info trace debug warn error]]
            [compojure.core :refer [defroutes routes GET POST]]
            [saml20-clj.routes :as saml-routes]
            [saml20-clj.sp :as saml-sp]
            [saml20-clj.shared :as saml-shared]
            [saml-test.pages :as pages]))

(defroutes main-routes
  (GET "/" []
       {:status  200
        :body    (pages/front-door)})
  (GET "/login" []
       {:status  302
        :headers {"Location" "/saml"}
        :body    ""})
  (GET "/target" []
       {:status  200
        :body    (pages/target)}))

(defn saml-routes
  "The SP routes. They can be combined with application specific routes. Also it is assumed that
  they are wrapped with compojure.handler/site or wrap-params and wrap-session.

  The single argument is a map containing the following fields:

  :app-name - The application's name
  :base-uri - The Base URI for the application i.e. its remotely accessible hostname and
              (if needed) port, e.g. https://example.org:8443 This is used for building the
              'AssertionConsumerService' URI for the HTTP-POST Binding, by prepending the
              base-uri to the '/saml' string.
  :idp-uri  - The URI for the IdP to use. This should be the URI for the HTTP-Redirect SAML Binding
  :idp-cert - The IdP certificate that contains the public key used by IdP for signing responses.
              This is optional: if not used no signature validation will be performed in the responses
  :keystore-file - The filename that is the Java keystore for the private key used by this SP for the
                   decryption of responses coming from IdP
  :keystore-password - The password for opening the keystore file
  :key-alias - The alias for the private key in the keystore

  The created routes are the following:
  - GET /saml/meta : This returns a SAML metadata XML file that has the needed information
                     for registering this SP. For example, it has the public key for this SP.
  - GET /saml : it redirects to the IdP with the SAML request envcoded in the URI per the
                HTTP-Redirect binding. This route accepts a 'continue' parameter that can
                have the relative URI, where the browser should be redirected to after the
                successful login in the IdP.
  - POST /saml : this is the endpoint for accepting the responses from the IdP. It then redirects
                 the browser to the 'continue-url' that is found in the RelayState paramete, or the '/' root
                 of the app.
  "
  [{:keys [app-name base-uri idp-uri idp-cert keystore-file keystore-password key-alias]}]
  (let [decrypter (saml-sp/make-saml-decrypter keystore-file keystore-password key-alias)
        sp-cert (saml-shared/get-certificate-b64 keystore-file keystore-password key-alias)
        ;; It's important to set the digest algorithm to SHA-256 (SHA-2). SHA-1 has been deprecated in browsers and will not work.
        mutables (assoc (saml-sp/generate-mutables)
                   :xml-signer (saml-sp/make-saml-signer keystore-file keystore-password key-alias :algorithm :sha256))
        acs-uri (str base-uri "/saml")
        saml-req-factory! (saml-sp/create-request-factory mutables
                                                          idp-uri
                                                          saml-routes/saml-format
                                                          app-name
                                                          acs-uri)
        prune-fn! (partial saml-sp/prune-timed-out-ids!
                           (:saml-id-timeouts mutables))
        state {:mutables mutables
               :saml-req-factory! saml-req-factory!
               :timeout-pruner-fn! prune-fn!
               :certificate-x509 sp-cert}]
    (routes
      (GET "/saml/meta" []
        {:status 200
         :headers {"Content-type" "text/xml"}
         :body (saml-sp/metadata app-name acs-uri sp-cert)})
      (GET "/saml" [:as req]
        (let [saml-request (saml-req-factory!)
              hmac-relay-state (saml-routes/create-hmac-relay-state (:secret-key-spec mutables) "target")]
          ; (debug (":secret-key-spec: %s" (:secret-key-spec mutables)))
          (info (str "GET /saml hmac-relay-state: " hmac-relay-state))
          (saml-sp/get-idp-redirect idp-uri saml-request hmac-relay-state)))
      (POST "/saml" {params :params session :session}
        (let [xml-response (saml-shared/base64->inflate->str (:SAMLResponse params))
              relay-state (:RelayState params)
              [valid-relay-state? continue-url] (saml-routes/valid-hmac-relay-state? (:secret-key-spec mutables) relay-state)
              saml-resp (saml-sp/xml-string->saml-resp xml-response)
              valid-signature? (if idp-cert
                                 (saml-sp/validate-saml-response-signature saml-resp idp-cert)
                                 false)
              _ (info (if valid-signature? "Signature was valid!" "Signature validation failed."))
              valid? (and valid-relay-state? valid-signature?)
              saml-info (when valid? (saml-sp/saml-resp->assertions saml-resp decrypter))]
          (if valid?
            (do
              (info "Validation was successful, redirecting client...")
              {:status 303
               :headers {"Location" continue-url}
               :session (assoc session :saml saml-info)
               :body "Oh yeah, it worked!"})
            (do
              (error "The SAML response from IdP does not validate!")
              {:status 500
               :body "The SAML response from IdP does not validate!"})))))))
