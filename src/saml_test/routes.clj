(ns saml-test.routes
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [hiccup.page :refer [html5]]))

(defn front-door []
  (html5
   [:html
    [:head
     [:link {:rel "stylesheet" :href "//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"}]
     [:title "Welcome to the demo SP"]]
    [:body.container
     [:h1 "Demo Service Provider"]
     [:p.lead "You can get the SAML metadata " [:a {:href "/saml/meta"} "here"]]
     [:a.btn.btn-primary {:href "login"} "Login to IdP"]]]))

(defroutes main-routes
  (GET "/" []
       {:status  200
        :body    (front-door)})
  (GET "/login" []
       {:status  302
        :headers {"Location" "/saml?continue-url=idp_url"}
        :body    ""}))

(def app
  (handler/api main-routes))
