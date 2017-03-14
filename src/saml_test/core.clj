(ns saml-test.core
  (:require [compojure.core :as core]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [saml-test.routes :as routes]))

(def config
  {:app-name "Test SAML app"
   :base-uri "http://localhost:8081"
   :idp-uri "http://localhost:7000"
   :idp-cert "MIIEMDCCAxigAwIBAgIJAPZcPVjOJlnMMA0GCSqGSIb3DQEBBQUAMG0xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMRAwDgYDVQQKEwdKYW5reUNvMR8wHQYDVQQDExZUZXN0IElkZW50aXR5IFByb3ZpZGVyMB4XDTE3MDMxMjE5MjkzNFoXDTM3MDMwNzE5MjkzNFowbTELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xEDAOBgNVBAoTB0phbmt5Q28xHzAdBgNVBAMTFlRlc3QgSWRlbnRpdHkgUHJvdmlkZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC02ay1lhBNntX3p+8Wlsq0wlq31rJkBkmlz/CAJz1kQmhwBxpc0icLHdcWwF4BJ3zcV/CtpNpWv8uD+7EPVJKpavbdi318Ug2K2vqZI7Dyvqi3X50+s3uyAG/gIy63h1qwfFOdIVj7YbJiKvUF4/6w4BWbnNgQL3dBNOPEGdzNgzRlffIbRJwDaIpgiY8nE8sAkTmPzaAuKAGc5eedYDZW+xkDHHHFHtpCxnAaDVfwCPxv+uGRP7emSp7TvFHFKmKQUTZh+FYyJfk8yUs6AJTaJOR2mRFrFxht7pRlavWeAbRDaa4wUUgwHNqvjBI47RZnOeplljN4TyLOL9J9tvXJAgMBAAGjgdIwgc8wHQYDVR0OBBYEFD9Q9rcVIp+s4unYzstD/T5hZjNHMIGfBgNVHSMEgZcwgZSAFD9Q9rcVIp+s4unYzstD/T5hZjNHoXGkbzBtMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEQMA4GA1UEChMHSmFua3lDbzEfMB0GA1UEAxMWVGVzdCBJZGVudGl0eSBQcm92aWRlcoIJAPZcPVjOJlnMMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADggEBAJDIaTj9PimXTExXGLAU7p8HzACvXXmO7fMZWxahvWDwrgmlhJ+7S5oYYqkYkCleBtYbHduakGPeWbEwcZjNzLsMFYxQntiM3gz1dUS3DpR5lP+/UhO7HNN81XSgj63K56YO1iS8hX6NzLylOcfxw3wgMtguzIyxLLGfE1rbZkC67D/kfz+jLp6U9ChjqHE3FbSFrl2dhHltdHJ4ErM1vlbTIoa+52ka4WKD2uArgilgTo7wvG1fE9n7SXcK5QdUlb4Voi71UchkkauuNXhq090i6Y6GP1EicHuK6ymT4P9sGwnGoXHPik9v6QuTwxcAZIoR9J6P8CyrL2OHIseh6xQ="
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
  (jetty/run-jetty app {:port 8081}))
