(ns worktime-manager.stats
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [worktime-manager.utils :refer [format-short-date]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [worktime-manager.utils :as utils]))

(defn- table-row [stats owner]
  (reify
    om/IRender
    (render [_]
      (let [longest (:longest stats)
            shortest (:shortest stats)]
        (html [:tr nil
               [:td (:_id stats)]
               [:td (str (utils/seconds->hours (:time longest)) "h " (format-short-date (:date longest)))]
               [:td (str (utils/seconds->hours (:time shortest)) "h " (format-short-date (:date shortest)))]
               [:td (str (utils/seconds->hours (:avg stats)) "h")]
               [:td (str (utils/seconds->hours (:sum stats)) "h")]])))))

(defn stats-table [stats owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (when (empty? stats)
        (go
          (let [response (<! (http/get "/api/stats"))]
            (om/update! stats (:body response))))))
    om/IRender
    (render [_]
      (html [:div.table-responsive
             [:table.table.table-striped
              [:thead
               [:tr
                [:th "Year"]
                [:th "Longest day"]
                [:th "Shortest day"]
                [:th "Avg"]
                [:th "Total"]]]
              [:tbody
               (map #(om/build table-row %) stats)]]]))))

;; (defn stats-list [stats owner]
;;   (reify
;;     om/IWillMount
;;     (will-mount [_]
;;       (when (empty? stats)
;;         (go
;;           (let [year (om/get-state owner :year)
;;                 response (<! (http/get "/api/stats"))]
;;             (om/update! stats (:body response))))))
;;     om/IRender
;;     (render [_]
;;       (let [longest (:longest stats)
;;             shortest (:shortest stats)]
;;         (html [:ul
;;                [:li (str "Total working hours " (utils/seconds->hours (:sum stats)) " hours")]
;;                [:li (str "Average working hours " (utils/seconds->hours (:avg stats)) " hours")]
;;                [:li (str "Longest working day " (format-date (:date longest)) " -> " (utils/seconds->hours (:time longest)) " hours")]
;;                [:li (str "Shortest working day " (format-date (:date shortest)) " -> " (utils/seconds->hours (:time shortest)) " hours")]])))))
