(ns worktime-manager.handler
  (:use compojure.core)
  (:require [worktime-manager.db :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [monger.joda-time]
            [monger.json]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response header]]
            [cheshire.core :refer [generate-string]]
            [clojure.java.io :refer [resource]]))

(defn timereport [body]
 (let [result (insert-report body)]
   (if (nil? result)
     {:status 500}
     {:status 201
      :headers {"Location" (str (:_id result))}})))

(defn get-timereports [year-str week-str]
  (let [year (read-string year-str)
        week (dec (read-string week-str))]
    (-> (get-reports-by-week year week)
        (generate-string)
        (response)
        (header "Content-Type" "application/json"))))

(defroutes api-routes
  (POST "/timereport" request (timereport (:body request)))
  (GET "/timereport/:year/:week" [year week] (get-timereports year week)))

(defroutes app-routes
  (context "/api" [] api-routes)
  (GET "/" [] (resource "public/index.html"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})))
