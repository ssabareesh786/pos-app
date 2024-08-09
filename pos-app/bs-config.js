module.exports = {
  port: 9001,
  files: ["src/main/webapp/html/**/*", "src/main/webapp/static/**/*"],
  browser: "google chrome",
  reloadDelay: 1,
  open: false,
  proxy: {
    target: 'http://localhost:9000/posapp',
    middleware: function (req, res, next) {
      var url = req.url;
      if (url === '/ui/brand') {
        req.url = '/ui/brand';
      } else if (url === '/ui/category') {
        req.url = '/ui/category';
      } else if (url === '/ui/product') {
        req.url = '/ui/product';
      }
      return next();
    }
  }
};
