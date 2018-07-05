(defproject saml-test "0.1.0-SNAPSHOT"
  :description "A demonstration project for integrating a SSO IDP using SAML into a Clojure Web application"
  :url "http://github.com/quephird/saml-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [org.slf4j/slf4j-api "1.7.25"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [ring "1.6.3"]
                 [kirasystems/saml20-clj "0.1.12"]]
  :plugins [[big-solutions/lein-mvn "0.1.0"]
            [lein-bower "0.5.2"]
            [lein-npm "0.6.2"]]
  :npm {:dependencies [[saml-idp "1.1.0"]]}
  :main saml-test.core)
