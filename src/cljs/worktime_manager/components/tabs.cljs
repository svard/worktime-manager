(ns worktime-manager.components.tabs
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [worktime-manager.utils :as utils]))

(defn active? [is-active]
  (if is-active
    {:class "active"}
    {}))

(defn tabs [route owner]
  (om/component
    (html [:ul.nav.nav-tabs
           [:li (active? (= route :home))
            [:a {:href "#/"} "Home"]]
           [:li (active? (= route :stats))
            [:a {:href "#/statistics"} "Statistics"]]])))
