var gulp = require('gulp');
var log = require('fancy-log');

var browserSync = require('browser-sync').create();

var del = require('del');

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

var scssWatchPaths = [
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
                    presets: ['env', 'react'],
                    compact: false
                }
            }
        ]
    },
    devtool: 'source-map'
};

var cleanTask = () => del('./build');

function jsTask(done) {
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
}
gulp.task('manual:js', jsTask);

function scssTask() {
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
        .pipe(gulp.dest('./build/css/'))
        .pipe(browserSync.stream({match: '**/*.css'}));
}
gulp.task('manual:scss', scssTask);

function cssTask() {
    return gulp.src([
        './node_modules/material-design-icons/iconfont/material-icons.css'
    ]).pipe(gulp.dest('./build/css'));
}
gulp.task('manual:css', cssTask);

function copyFontTask() {
    var materialDesignIconsFolder = './node_modules/material-design-icons/iconfont/';
    return gulp.src([
        materialDesignIconsFolder + 'MaterialIcons-Regular.eot',
        materialDesignIconsFolder + 'MaterialIcons-Regular.ijmap',
        materialDesignIconsFolder + 'MaterialIcons-Regular.svg',
        materialDesignIconsFolder + 'MaterialIcons-Regular.ttf',
        materialDesignIconsFolder + 'MaterialIcons-Regular.woff',
        materialDesignIconsFolder + 'MaterialIcons-Regular.woff2'
    ]).pipe(gulp.dest('./build/css/'));
}
gulp.task('manual:fonts', copyFontTask);

function copyIndexHtmlTask() {
    return gulp.src('./src/index.html')
        .pipe(gulp.dest('./build/'));
}
gulp.task('manual:index', copyIndexHtmlTask);

function browserSyncInitTask(done) {
    browserSync.init({
        server: {
            baseDir: './build'
        }
    });

    done();
}

function browserSyncReloadTask(done) {
    browserSync.reload();
    done();
}

function watchTask() {
    gulp.watch(jsWatchPaths, { delay: 200 }, gulp.parallel(jsTask, browserSyncReloadTask));
    gulp.watch(scssWatchPaths, { delay: 200 }, gulp.parallel(scssTask, browserSyncReloadTask));
    gulp.watch(htmlWatchPaths, gulp.parallel(copyIndexHtmlTask, browserSyncReloadTask));
}

var buildTask = gulp.parallel(jsTask, scssTask, cssTask, copyFontTask, copyIndexHtmlTask);

gulp.task('default', gulp.series(cleanTask, buildTask));

var quickBuildTask = gulp.parallel(jsTask, scssTask, copyIndexHtmlTask);
gulp.task('dirty', gulp.series(quickBuildTask));

gulp.task('dev', gulp.series(cleanTask, buildTask, browserSyncInitTask, watchTask));
