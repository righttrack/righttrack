module.exports = (grunt) ->
  """
  Right Track config work, build, and release cycle:

  Build/Release cycle:
  build - JavaScript files in the target/js/src directory.
  stage - Concatonated JavaScript files in the target/js/dist directory source-mapped to *.js.map
  release (build) - Optimized and uglified JavaScript files in the public directory.

  Test/Work cycle:
  test - Raw JavaScript specs and config files in the src/test directory.
  source - Raw JavaScript source files in the public directory.
  work (source) - Source-mapped Raw JavaScript with CoffeeScript in the public directory.

  """

  grunt.initConfig

    pkg: grunt.file.readJSON 'package.json'

    bower:
      build:
        dest: 'target/js/src/lib'
      source:
        dest: 'web/main/typescript/lib/bower_source'
      release:
        dest: 'public/js/lib'

    clean:
      build: ['target/js']
      work: ['public/js', 'public/coffee']
      bower_source: ['src/main/typescript/bower_source']

    copy:
      work:
        files: [
          expand: true
          cwd: 'web/main/coffee'
          dest: 'public/coffee'
          src: '**/*.coffee'
        ]
      build:
        files: [
          dest: 'target/js/dist/src/lib/requirejs.js'
          src: 'target/js/src/lib/requirejs.js'
        ]
      release:
        files: [
          expand: true
          cwd: 'target/js/dist/src'
          dest: 'public/js/src'
          src: '**/*'
        ]

    typescript:
      build:
        src: ['web/main/typescript/**/*.ts']
        dest: 'target/js/src'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: 'web/main/typescript'
          sourceRoot: 'target/js/src'
          sourcemap: false
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true
      work:
        src: ['web/main/typescript/**/*.ts']
        dest: 'public/js'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: 'web/main/typescript'
          sourceRoot: 'public/js'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true
      test:
        src: ['web/test/typescript/**/*.ts']
        dest: 'target/js/src'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: 'web/type/typescript'
          sourceRoot: 'target/js/src'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true

    coffee:
      build:
        options:
          sourceMap: false
        files: [
          expand: true
          cwd: 'web/main/coffee'
          dest: 'target/js/src'
          src: '**/*.coffee'
          ext: '.js'
        ]
      work:
        options:
          bare: true
          sourceMap: true
        files: [
          expand: true
          cwd: 'public/coffee'
          dest: 'public/js'
          src: '**/*.coffee'
          ext: '.js'
        ]
      test:
        options:
          sourceMap: false
        files: [
          expand: true
          cwd: 'web/test/coffee'
          dest: 'target/js/src'
          src: '**/*.coffee'
          ext: '.js'
        ]

    requirejs:
      build:
        options:
          findNestedDependencies: true
          mainConfigFile: 'target/js/src/config/defaults.js'
          baseUrl: 'target/js/src'
          name: 'app'
          out: 'target/js/dist/src/app.js'
          optimize: 'none'
      work:
        options:
          findNestedDependencies: true
          mainConfigFile: 'target/js/config/defaults.js'

    jasmine:
      test:
#        host: 'http://127.0.0.1:8000/'
        src: 'target/js/src/spec/**/*.js'
        options:
          specs: '**/*Spec.js'
#          helpers: '*Helper.js'
          template: require 'grunt-template-jasmine-requirejs'
          templateOptions:
            requireConfigFile: ['target/js/src/config/defaults.js', 'target/js/src/config/test.js']

    sass:
      work:
        files: [
          'public/css/main.css': 'web/main/sass/main.scss'
        ]

    uglify:
      options:
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
        mangle: false
      release:
        options:
          sourceMap: (fileName) ->
            fileName + '.map'
          sourceMappingURL: (path) ->
            path.replace(/.*\/(.*)$/, '$1') + '.map'
        files: [
          dest: 'public/js/app.js'
          src: 'public/js/src/app.js'
        ,
          dest: 'public/js/lib/requirejs.js'
          src: 'public/js/src/lib/requirejs.js'
        ]

    watch:
      allTypeScript:
        files: ['web/**/*.ts']
        tasks: ['copy:source', 'typescript']
      workTypeScript:
        files: ['web/main/coffee/**/*.ts']
        tasks: ['stage-work']
      testTypeScript:
        files: ['web/**/*.ts']
        tasks: ['typescript:build', 'typescript:test', 'jasmine']

      allCoffee:
        files: ['web/**/*.coffee']
        tasks: ['copy:source', 'coffee']
      workCoffee:
        files: ['web/main/coffee/**/*.coffee']
        tasks: ['stage-work']
      testCoffee:
        files: ['web/**/*.coffee']
        tasks: ['coffee:build', 'coffee:test', 'jasmine']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-typescript'

  grunt.registerTask 'compile-build', ['typescript:build']
#  grunt.registerTask 'compile-build', ['coffee:build']
  grunt.registerTask 'compile-test', ['typescript:build', 'typescript:test']
#  grunt.registerTask 'compile-test', ['coffee:build', 'coffee:test']
  grunt.registerTask 'compile-work', ['copy:work', 'typescript:work', 'sass:work']
#  grunt.registerTask 'compile-work', ['copy:work', 'coffee:work', 'sass:work']

  grunt.registerTask 'stage-build', ['clean', 'bower:build', 'compile-build', 'copy:build']
#  grunt.registerTask 'stage-build', ['clean', 'bower:build', 'compile-build', 'requirejs:build', 'copy:build']
  grunt.registerTask 'stage-test', ['clean', 'bower:build', 'compile-test', 'typescript:test']
#  grunt.registerTask 'stage-test', ['clean', 'bower:build', 'compile-test', 'coffee:test']
  grunt.registerTask 'stage-work', ['clean:work', 'bower:release', 'compile-work']
#  grunt.registerTask 'stage-work', ['clean:work', 'bower:release', 'compile-work']

  grunt.registerTask 'do-test', ['stage-test', 'jasmine']
  grunt.registerTask 'test', ['do-test', 'watch:test']
  grunt.registerTask 'do-work', ['stage-work']
  grunt.registerTask 'work', ['do-work', 'watch:work']

  grunt.registerTask 'release', ['stage-build', 'copy:release', 'uglify:release']

  grunt.registerTask 'default', ['stage-work', 'dotest']
