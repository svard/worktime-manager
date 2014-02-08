(ns worktime-manager.main
  (:require [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secretary.core :as secretary :include-macros true :refer [defroute]]
            [worktime-manager.components.list :as tl]
            [worktime-manager.utils :as utils]))

(enable-console-print!)

(def app-state (atom {:reports []}))

(defn list-view [app owner]
  (reify
    om/IRender
    (render [_]
      (om/build tl/time-list (:reports app)))))

(om/root app-state list-view (. js/document (getElementById "container")))
