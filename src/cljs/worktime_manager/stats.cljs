(ns worktime-manager.stats
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [worktime-manager.utils :refer [format-short-date]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [<!]]
            [worktime-manager.utils :as utils]
            [worktime-manager.xhr :refer [xhr]]))

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
        (xhr {:url "/api/stats"
          :method :get
          :content"application/json"
          :on-complete (fn [resp]
                         (om/update! stats resp))})))
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
