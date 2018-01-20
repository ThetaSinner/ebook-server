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

var jsWatchPaths = [
    './root.js',
    './src/js/**/*.js'
];

var cssWatchPaths = [
    './root.scss',
    './src/scss/**/*.scss'
];

var htmlWatchPaths = [
    './src/index.html'
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
                './node_modules/bootstrap/scss/',
                './node_modules/material-design-icons/iconfont/'
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

var copyFontTask = function () {
    var materialDesignIconsFolder = './node_modules/material-design-icons/iconfont/';
    return gulp.src([
        materialDesignIconsFolder + 'MaterialIcons-Regular.eot',
        materialDesignIconsFolder + 'MaterialIcons-Regular.ijmap',
        materialDesignIconsFolder + 'MaterialIcons-Regular.svg',
        materialDesignIconsFolder + 'MaterialIcons-Regular.ttf',
        materialDesignIconsFolder + 'MaterialIcons-Regular.woff',
        materialDesignIconsFolder + 'MaterialIcons-Regular.woff2'
    ]).pipe(gulp.dest('./build/css'));
};

var copyIndexHtmlTask = function() {
    return gulp.src('./src/index.html')
        .pipe(gulp.dest('./build/index.html'));
};

var buildTask = gulp.parallel(jsTask, cssTask, copyFontTask, copyIndexHtmlTask);

gulp.task('default', gulp.series(buildTask));

gulp.task('watch', function (done) {
    gulp.watch(jsWatchPaths, { delay: 200 }, gulp.parallel(jsTask));
    gulp.watch(cssWatchPaths, { delay: 200}, gulp.parallel(cssTask));
    gulp.watch(htmlWatchPaths, gulp.parallel(copyIndexHtmlTask));

    done();
});
