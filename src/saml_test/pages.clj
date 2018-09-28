(ns saml-test.pages
  (:require [hiccup.page :refer [html5]]))

(defn front-door []
  (html5
    [:html
     [:head
      [:link {:rel "stylesheet" :href "//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"}]
      [:title "Welcome to the demo SP"]]
     [:body.container
      [:h1 "Hey, you've found your local Service Provider!!!"]
      [:p.lead "You can get the SAML metadata for this SP " [:a {:href "/saml/meta"} "here"] "!!!"]
      [:a.btn.btn-primary {:href "login"} "Take me to the IdP!!!"]]]))

(defn- show-saml-assertions [assertions]
  (for [assertion assertions]
    [:table {:class "table-bordered table-hover"}
     ;; SAML attributes
     (for [[k v] (:attrs assertion)]
       [:tr
        [:td k] [:td v]])
     ;; Actual name ID
     [:tr
      [:td :name-id] [:td (:value (:name-id assertion))]]]))

(defn show-saml-info [saml-info]
  (html5
    [:html
     [:head
      [:link {:rel "stylesheet" :href "//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"}]
      [:style "tr { height: 30px; } td { padding: 10px; }"]
      [:title "SAML response from IdP"]]
     [:body.container
      [:h1 "This is the SAML response received from the IdP"]
      [:table {:class "table-bordered table-hover"}
       (for [[k v] saml-info]
         (if (not= k :assertions)
           [:tr
            [:td k] [:td v]]
           [:tr
            [:td :assertions] [:td (show-saml-assertions v)]]))]]]))

(defn access-denied []
  "TODO")

(defn target []
  (html5
    [:html
     [:head
      [:link {:rel "stylesheet" :href "//maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"}]
      [:title "Congrats, your round-trip to the IdP was successful!"]]
     [:body.container
      [:h1 "Everything seems to have worked!"]
      [:p.lead "The SAML response from the IdP was valid. Your SP->IdP->SP -roundtrip was successful!"]
      [:a.btn.btn-primary {:href "/"} "Return to main page"]]]))
