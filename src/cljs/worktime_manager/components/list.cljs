(ns worktime-manager.components.list
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [worktime-manager.utils :refer [format-date]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [worktime-manager.utils :as utils]))

(defn stats-list [stats owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (when (empty? stats)
        (go
          (let [response (<! (http/get "/api/stats"))]
            (om/transact! stats (fn [_] (:body response)))))))
    om/IRender
    (render [_]
      (let [stat (first stats)
            longest (:longest stat)
            shortest (:shortest stat)]
        (dom/ul nil
          (dom/li nil (str "Total working hours " (utils/seconds->hours (:sum stat)) " hours"))
          (dom/li nil (str "Average working hours " (utils/seconds->hours (:avg stat)) " hours"))
          (dom/li nil (str "Longest working day " (format-date (:date longest)) " -> " (utils/seconds->hours (:time longest)) " hours"))
          (dom/li nil (str "Shortest working day " (format-date (:date shortest)) " -> " (utils/seconds->hours (:time shortest)) " hours")))))))
