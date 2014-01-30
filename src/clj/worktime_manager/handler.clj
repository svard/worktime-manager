(ns worktime-manager.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [file-response]]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(defn timereport
 [body]
 (info body)
 {:status 201})

(defroutes app-routes
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (POST "/timereport" request (timereport (:body request)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})))
