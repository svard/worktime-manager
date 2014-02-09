(ns worktime-manager.components.pager
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

(defn pager [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:first-week 6})
    om/IRender
    (render [_]
      (dom/ul #js {:className "pager"}
        (dom/li #js {:className (utils/disabled (<= (:current-week app) (om/get-state owner :first-week)) "previous")}
          (dom/a nil "Previous"))
        (dom/li #js {:className (utils/disabled (>= (:current-week app) (utils/get-week-number (DateTime.))) "next")}
          (dom/a nil
            (dom/span nil "Next")))))))
