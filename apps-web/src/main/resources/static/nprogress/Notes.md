Testing
-------

    $ npm install
    $ npm sandbox

or try it out in the browser:

    $ open sandbox/index.html

Testing component build
-----------------------

    $ component install
    $ component build
    $ open sandbox/component.html

Releasing
---------

    $ npm sandbox
    $ bump *.json nprogress.js          # bump version numbers
    $ git release 0.1.1                 # release to bower/github
    $ npm publish                       # release to npm
    $ git push origin master:gh-pages   # update the site
