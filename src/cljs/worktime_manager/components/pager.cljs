(ns worktime-manager.components.pager
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [worktime-manager.utils :as utils]
            [cljs.core.async :refer [put!]])
  (:import [goog.date DateTime]))

(defn handle-prev-click [date owner]
  (let [first-week (om/get-state owner :first-week)
        selected-week (:week @date)
        nav-chan (om/get-state owner :nav-chan)]
    (when (> selected-week first-week)
      (om/transact! date [:week] dec)
      (put! nav-chan [(:year @date) (:week @date)]))))

(defn handle-next-click [date owner]
  (let [selected-week (:week @date)
        current-week (om/get-state owner :current-week)
        nav-chan (om/get-state owner :nav-chan)]
    (when (< selected-week current-week)
      (om/transact! date [:week] inc)
      (put! nav-chan [(:year @date) (:week @date)]))))

(defn pager [date owner]
  (reify
    om/IInitState
    (init-state [_]
      {:first-week 5
       :current-week (utils/get-week-number (DateTime.))})
    om/IRenderState
    (render-state [_ {:keys [first-week current-week]}]
      (html [:ul.pager
             [:li {:class (utils/disabled (<= (:week date) first-week) "previous")}
              [:a {:on-click #(handle-prev-click date owner)} "Previous"]]
             [:li {:class (utils/disabled (>= (:week date) current-week) "next")}
              [:a {:on-click #(handle-next-click date owner)}
               [:span "Next"]]]]))))
