(ns saml-test.core
  (:require [ring.adapter.jetty :as jetty]
            [saml-test.routes :as routes]))

(defn -main
  "The point of entry for the demo server."
  []
  (jetty/run-jetty routes/app {:port 8081}))
