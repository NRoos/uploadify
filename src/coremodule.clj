(ns coremodule
  (:require [amazonica.aws.s3 :as s3]
            [clojure.java.io :as io]
            [clojure.string :as strs])
  (:gen-class))

(defn upload [file-path upload-name]
  (println (str "Uploading: " upload-name))
  (s3/put-object :bucket-name (System/getenv "BUCKET_NAME")
              :key upload-name
              :input-stream (io/input-stream file-path)
              :metadata {:server-side-encryption "AES256"}))

(defn -main
  [& args]
  (let [file-path (first args)]
    (if (= (count args) 2)
      (upload file-path (first (rest args)))
      (upload file-path (last (strs/split file-path #"/"))))))
