(ns saml-test.core
  (:require [clojure.string :as s]
            [compojure.core :as core]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [saml-test.routes :as routes]
            [taoensso.timbre :as timbre :refer [log info trace debug warn error]]))

(defn- parse-certificate
  "Strip the ---BEGIN CERTIFICATE--- and ---END CERTIFICATE--- headers and newlines
  from certificate."
  [certstring]
  (->> (s/split certstring #"\n") rest drop-last s/join))

(def config
  {:app-name "Test SAML app"
   :base-uri "http://localhost:8081"
   :idp-uri "http://localhost:7000"
   :idp-cert (parse-certificate (slurp "./node_modules/saml-idp/idp-public-cert.pem"))
   :keystore-file "keystore.jks"
   :keystore-password "changeit"
   :key-alias "mylocalsp"})

(def app
  (handler/api
    (core/routes routes/main-routes
            (routes/saml-routes config))))

(defn -main
  "The point of entry for the demo server."
  []
  (do
    (info "Started Jetty server on port 8081...")
    (jetty/run-jetty app {:port 8081})))
