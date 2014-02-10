(ns worktime-manager.components.pager
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [worktime-manager.utils :as utils]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http])
  (:import [goog.date DateTime]))

(defn load-async [app]
  (go
    (let [year (get-in @app [:current-date :year])
          week (get-in @app [:current-date :week])
          url (str "/api/timereport/" year "/" week)
          response (<! (http/get url))]
      (when (= (:status response) 200)
        (om/transact! app [:reports] (fn [_] (:body response)))))))

(defn handle-click [app f]
  (om/transact! app [:current-date :week] f)
  (load-async app))

(defn pager [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:first-week 5
       :current-week (utils/get-week-number (DateTime.))})
    om/IWillMount
    (will-mount [_]
      (load-async app))
    om/IRenderState
    (render-state [_ {:keys [first-week current-week]}]
      (dom/ul #js {:className "pager"}
        (dom/li #js {:className (utils/disabled (<= (get-in app [:current-date :week]) first-week) "previous")}
          (dom/a #js {:onClick #(handle-click app dec)} "Previous"))
        (dom/li #js {:className (utils/disabled (>= (get-in app [:current-date :week]) current-week) "next")}
          (dom/a #js {:onClick #(handle-click app inc)}
            (dom/span nil "Next")))))))
