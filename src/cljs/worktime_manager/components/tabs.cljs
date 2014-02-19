(ns worktime-manager.components.tabs
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils]))

(defn active? [is-active]
  (if is-active
    #js {:className "active"}
    #js {}))

(defn tabs [route owner]
  (om/component
    (dom/ul #js {:className "nav nav-tabs"}
      (dom/li (active? (= route :home))
        (dom/a #js {:href "#/"} "Home"))
      (dom/li (active? (= route :stats))
        (dom/a #js {:href "#/statistics"} "Statistics")))))
