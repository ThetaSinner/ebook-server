var gulp = require('gulp');
var log = require('fancy-log');

// js
var path = require('path');
var webpack = require('webpack');

// css
var sourcemaps = require('gulp-sourcemaps');
var cssnano = require('gulp-cssnano');
var concat = require('gulp-concat');
var autoprefixer = require('gulp-autoprefixer');
var sass = require('gulp-sass');

var js = [
    './src/js/**/*.js'
];

var webpackConfig = {
    entry: './root.js',
    output: {
        path: path.resolve(__dirname, 'build/js'),
        filename: 'bundle.js'
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                loader: 'babel-loader',
                query: {
                    presets: ['env', 'react']
                }
            }
        ]
    },
    devtool: 'source-map'
};

var jsTask =  function (done) {
    webpack(webpackConfig, (err, stats) => {
        if (err) {
            log.error('Webpack error', err);
        }
        else {
            log('Webpack stats', stats.toString({
                assets: true,
                chunks: false,
                chunkModules: false,
                colors: true,
                hash: false,
                timings: true,
                version: false
            }));
        }

        done();
    });
};

var cssTask = function() {
    return gulp.src('./root.scss')
        .pipe(sourcemaps.init())
        .pipe(sass({
            includePaths: [
                './node_modules/bootstrap/scss/'
            ],
            outputStyle: 'compressed'
        }).on('error', sass.logError))
        .pipe(autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false,
        }))
        .pipe(concat('bundle.css'))
        .pipe(cssnano())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('./build/css/'));
};

var build = gulp.parallel(jsTask, cssTask);

gulp.task('default', gulp.series(build));

gulp.task('watch', function () {
    gulp.watch(js, { delay: 200 }, gulp.parallel(jsTask));
});
