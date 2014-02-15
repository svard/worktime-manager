(ns worktime-manager.components.pager
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils]
            [cljs.core.async :refer [put!]])
  (:import [goog.date DateTime]))

(defn handle-prev-click [app owner]
  (let [first-week (om/get-state owner :first-week)
        selected-week (get-in @app [:current-date :week])
        nav-chan (om/get-shared owner :nav-chan)]
    (when (> selected-week first-week)
      (om/transact! app [:current-date :week] dec)
      (put! nav-chan [(get-in @app [:current-date :year]) (get-in @app [:current-date :week])]))))

(defn handle-next-click [app owner]
  (let [selected-week (get-in @app [:current-date :week])
        current-week (om/get-state owner :current-week)
        nav-chan (om/get-shared owner :nav-chan)]
    (when (< selected-week current-week)
      (om/transact! app [:current-date :week] inc)
      (put! nav-chan [(get-in @app [:current-date :year]) (get-in @app [:current-date :week])]))))

(defn pager [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:first-week 5
       :current-week (utils/get-week-number (DateTime.))})
    om/IRenderState
    (render-state [_ {:keys [first-week current-week]}]
      (dom/ul #js {:className "pager"}
        (dom/li #js {:className (utils/disabled (<= (get-in app [:current-date :week]) first-week) "previous")}
          (dom/a #js {:onClick #(handle-prev-click app owner)} "Previous"))
        (dom/li #js {:className (utils/disabled (>= (get-in app [:current-date :week]) current-week) "next")}
          (dom/a #js {:onClick #(handle-next-click app owner)}
            (dom/span nil "Next")))))))
