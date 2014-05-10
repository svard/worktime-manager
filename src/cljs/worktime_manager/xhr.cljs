(ns worktime-manager.xhr
  (:require [goog.events :as events]
            [cljs.reader :as reader])
  (:import [goog.net EventType XhrIo]))

(def ^:private http-methods
  {:get "GET"
   :post "POST"
   :put "PUT"
   :delete "DELETE"})

(defn json-decode
  [string]
  (if-let [json (js/JSON.parse string)]
    (js->clj json :keywordize-keys true)
    string))

(defn xhr [{:keys [method url data on-complete content]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr EventType.COMPLETE (fn [e]
                                            (when (= method :get)
                                              (on-complete (json-decode (.getResponseText xhr))))))
    (. xhr
       (send url (http-methods method) (when data (pr-str data)) #js {"Content-Type" content}))))
