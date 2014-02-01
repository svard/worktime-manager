(defproject worktime-manager "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [;clj
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [ring "1.2.1"]
                 [ring/ring-json "0.2.0"]
                 [com.taoensso/timbre "2.7.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [clj-time "0.6.0"]
                 [com.novemberain/monger "1.7.0"]
                 [cheshire "5.1.1"]
                 ;cljs
                 [org.clojure/clojurescript "0.0-2156"]]

  :source-paths ["src/clj" "src/cljs"]

  :test-paths ["test/clj" "test/cljs"]

  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.2"]]

  :ring {:handler worktime-manager.handler/app}

  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
