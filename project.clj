(defproject worktime-manager "0.1.31"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [;clj
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [ring "1.2.1"]
                 [ring-middleware-format "0.3.2"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [clj-time "0.6.0"]
                 [com.novemberain/monger "1.7.0"]
                 ;cljs
                 [org.clojure/clojurescript "0.0-2173"]
                 [om "0.5.3"]
                 [secretary "0.7.1"]
                 [sablono "0.2.14"]]

  :source-paths ["src/clj" "src/cljs"]

  :test-paths ["test/clj" "test/cljs"]

  :plugins [[lein-ring "0.8.10"]
            [com.cemerick/clojurescript.test "0.2.2"]
            [lein-cljsbuild "1.0.2"]]

  :ring {:handler worktime-manager.handler/app}

  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]
         :global-vars {*warn-on-reflection* true}}}

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/main_dev.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/main.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :preamble ["react/react.min.js"]
                                   :externs ["react/externs/react.js"]}}
                       {:id "test"
                        :source-paths ["test/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]
              :test-commands {"phantom" ["phantomjs" :runner
                                         "this.literal_js_was_evaluated=true"
                                         "resources/public/js/test.js"]}})
