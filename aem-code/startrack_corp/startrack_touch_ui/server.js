var Hapi = require('hapi');
var webpack = require('webpack');
var fs = require('fs');

// config ---
var config = {
    isDev: false
}
// ----------

console.log('\x1b[32m', 'Config isDev: ' + config.isDev, '\x1b[0m');

var server = new Hapi.Server();
server.connection({ 
    host: 'localhost', 
    port: 8080,
});

var plugin = {
    register: function (server, options, next) {
        //fs.readFile(file, [encoding], [callback]);

        var compiler = webpack(options);
        compiler.watch({ // watch options:
            aggregateTimeout: 300, // wait so long for more changes
            poll: false // use polling instead of native watchers
        }, function(err, statsStr) {
            if (err) {
                console.log('\x1b[30m', 'Building was failure' ,'\x1b[0m', err);
                return next(err);
            }
            var stats = statsStr.toJson();
            if (stats.errors.length > 0) {
                for (var i in stats.errors) {
                    console.log('\x1b[31m', 'Building was failure' ,'\x1b[0m');
                    console.log(stats.errors[i]);
                }
                return next(err);
            }
            if(stats.warnings.length > 0) {
                for (var i in stats.warnings) {
                    console.log('\x1b[33m', 'warning:' ,'\x1b[0m');
                    console.log(stats.warnings[i]);
                }
            }
            console.log('\x1b[32m', 'Building was successfull' ,'\x1b[0m');
        });
        next();
    }
}

plugin.register.attributes = {
    name: 'plugin',
    version: '1.0.0'
};

server.register([{
        register: require('inert'),
        options: {}
    },
    {
        register: plugin,
        options: {
            entry: "./jsx/app.js",
            output: {
                path: __dirname + '/../ui.apps/src/main/content/jcr_root/etc/designs/startrack_corp/touch_ui',
                filename: "clientlib.js",
                publicPath: "http://localhost:8080/"
            },
            module: {
                loaders: [
                    { test: /\.jsx$/, exclude: "/node_modules/", loader: "babel-loader" },
                    { test: /\.css$/, exclude: "/node_modules/", loaders: ["style", "css?-url"] },
                    { test: /\.scss$/, exclude: "/node_modules/", loaders: ["style", "css?-url", "sass"] },
                    { test: /\.html$/, loader: "html-loader" }
                ]
            },
            plugins: [
                (function () {
                    if (config.isDev) {
                        return function () {};
                    } else {
                        return new webpack.optimize.UglifyJsPlugin({
                            compress: { warnings: false }
                        });
                    }
                })(),
                (function () {
                    if (config.isDev) {
                        return function () {};
                    } else {
                        return new webpack.DefinePlugin({
                            'process.env': {
                                'NODE_ENV': '"production"'
                            }
                        });
                    }
                })()
            ],
            errorDetails: true,
        }
    }
], function() {
    console.log('"HAPI Plugin Webpack" is registered');

    // Start the server
    server.start(function(err) {

        if (err) {
            console.log(err);
            return;
        }

        console.log('Server running at:', server.info.uri);
    });
});

server.route({
    method: 'GET',
    path: '/',
    handler: {
        file: '/web/index.html',
    },
});

server.route({
    method: 'GET',
    path: '/scripts/jquery.min.js',
    handler: {
        file: '/node_modules/jquery/dist/jquery.min.js',
    },
});

server.route({
    method: 'GET',
    path: '/scripts/{param*}',
    handler: {
        directory: {
            path: './../ui.apps/src/main/content/jcr_root/apps/startrack_corp/components/clientlib',
            listing: true
        }
    },
});

server.route({
    method: 'GET',
    path: '/images/{param*}',
    handler: {
        directory: {
            path: './../ui.apps/src/main/content/jcr_root/etc/designs/startrack_corp/clientlib/img',
            listing: true
        }
    },
});


server.route({
    method: 'GET',
    path: '/bin/StartrackSearchTagService',
    handler: function(request, reply) {
        reply({"result":"ok","filter":"level1tag","data":[{"value":"insights", "name":"Insights"}, {"value":"faq", "name":"FAQ"}, {"value":"howtoguide", "name":"How-to Guide"}, {"value":"howtoguidepdf", "name":"How-to Guide PDF"}]});
    }
});
