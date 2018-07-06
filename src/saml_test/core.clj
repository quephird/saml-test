(ns saml-test.core
  (:require [compojure.core :as core]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [saml-test.routes :as routes]))

(def certti (slurp "./node_modules/saml-idp/idp-public-cert.pem"))

(def config
  {:app-name "Test SAML app"
   :base-uri "http://localhost:8081"
   :idp-uri "http://localhost:7000"
   :idp-cert certti
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
    (println "Jetty server running on port 8081")
    (jetty/run-jetty app {:port 8081})))
