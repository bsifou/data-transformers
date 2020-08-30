# Clojure installer and CLI tools for windows 

## to start from emacs

clojure -Sdeps "{:deps {nrepl {:mvn/version \"0.6.0\"} cider/piggieback {:mvn/version \"0.4.0\"} cider/cider-nrepl {:mvn/version \"0.22.0-SNAPSHOT\"}}}" -m nrepl.cmdline --middleware "[\"cider.nrepl/cider-middleware\", \"cider.piggieback/wrap-cljs-repl\"]"

then

M-x cider-connect-clj&cljs 

or

clojure -m figwheel.main -b dev -r from the command line 


# docs:
https://figwheel.org/docs/emacs.html
https://cider.readthedocs.io/en/latest/clojurescript/#using-figwheel-main
https://www.youtube.com/watch?v=hcFx-QL5ySM
```

clj -Sdeps '{:deps {cider/cider-nrepl {:mvn/version "0.20.0"} }}' -e '(require (quote cider-nrepl.main)) (cider-nrepl.main/init ["cider.nrepl/cider-middleware"])'
```
it will not run on windows.
You have to use double quotes instead of single quotes  an additionally you have to escape double quotes in strings with a backslash. so the modified command 

```
clj -Sdeps "{:deps {cider/cider-nrepl {:mvn/version \"0.20.0\"} }}" -e "(require (quote cider-nrepl.main)) (cider-nrepl.main/init [\"cider.nrepl/cider-middleware\"])"
```
runs on windows and *surprise*  it also runs on unix
# data-transformers
