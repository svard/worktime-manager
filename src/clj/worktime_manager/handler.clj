(ns worktime-manager.handler
  (:use compojure.core)
  (:require [worktime-manager.db :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [monger.joda-time]
            [monger.json]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [file-response response]]
            [taoensso.timbre :as timbre]
            [cheshire.core :refer [generate-string]]))

(timbre/refer-timbre)

(defn timereport
 [body]
 (info body)
 (let [result (insert-report body)]
   (if (nil? result)
     {:status 500}
     {:status 201
      :headers {"Location" (str (:_id result))}})))

(defroutes app-routes
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (POST "/timereport" request (timereport (:body request)))
  (GET "/timereport" request (response (generate-string(get-reports-by-week 2014 4))))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})))
