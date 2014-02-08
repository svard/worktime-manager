(ns worktime-manager.components.list
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

(defn display-report [report]
  (let [date (utils/str->date (:arrival report))]
    (str (utils/display-date date) " worked " (utils/seconds->hours (:total report)) " hours")))

(defn time-list-element [report owner]
  (om/component
   (dom/li nil (display-report report))))

(defn total-worktime [reports]
  (-> (reduce #(+ % (:total %2)) 0 reports)
      (utils/seconds->hours)))

(defn time-list [reports owner]
  (reify
    om/IInitState
    (init-state [_]
      {:total-time 0})
    om/IWillMount
    (will-mount [_]
      (go
        (let [year (.getYear (DateTime.))
              week (.getWeekNumber (DateTime.))
              url (str "/api/timereport/" year "/" week)
              response (<! (http/get url))]
          (om/transact! reports (fn [_] (:body response)))
          (om/set-state! owner :total-time (total-worktime (:body response))))))
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h1 nil "Worktime")
        (apply dom/ul nil
          (map #(om/build time-list-element %) reports))
        (dom/p nil (str "Total worktime " (om/get-state owner :total-time)))))))
