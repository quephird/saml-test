(defproject saml-test "0.1.0-SNAPSHOT"
  :description "A demonstration project for integrating a SSO IDP using SAML into a Clojure Web application"
  :url "http://github.com/quephird/saml-test"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.2"]
                 [hiccup "1.0.5"]
                 [org.slf4j/slf4j-api "1.7.22"]
                 [org.slf4j/slf4j-log4j12 "1.7.22"]
                 [ring "1.5.1"]
                 [saml20-clj "0.1.6"]]
  :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]
            [big-solutions/lein-mvn "0.1.0"]]
  :git-dependencies [["https://github.com/OpenConext/Mujina.git"]]
  :main saml-test.core)
