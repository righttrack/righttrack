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
        dest: 'target/js/lib'
      public:
        dest: 'public/js/lib'

    clean:
      app: [
        'public/js/app/**/*.js'
        'public/js/app/**/*.js.map'
      ]
      lib: [
        'public/js/lib/**/*.js'
        'public/js/lib/**/*.js.map'
      ]
      target: [
        'target/js'
      ]
      typescript: [
        'public/typescript'
      ]
      misc: [
        'src/main/typescript/**/*.js'
        'src/main/typescript/**/*.js.map'
      ]

    copy:
      source:
        files: [
          expand: true
          cwd: 'src/main/typescript'
          dest: 'public/typescript'
          src: '**'
        ]
      app:
        files: [
          expand: true
          cwd: 'righttrack/app'
          dest: 'target/typescript'
          src: '**'
        ]
      test:
        files: [
          expand: true
          cwd: 'righttrack/test'
          dest: 'target/typescript'
          src: '**'
        ]

    ts:
      app:
        src: ['righttrack/app/**/*.ts']
        html: ['righttrack/app/**/*.html']
        reference: 'righttrack/app/reference.ts'
        out: 'public/js/app.js'
        options:
          module: 'commonjs'
          sourcemap: true
          sourceRoot: '/source'
      watch:
        src: ['righttrack/app/**/*.ts']
        html: ['righttrack/app/**/*.html']
        reference: 'righttrack/app/reference.ts'
        out: 'public/js/app.js'
        watch: 'righttrack/app'
        options:
          module: 'commonjs'
          sourcemap: true
          sourceRoot: '/source'
#      source:
#        src: ['righttrack/app/**/*.ts']
#        html: ['righttrack/app/**/*.html']
#        reference: 'righttrack/app/reference.ts'
#        amdloader: 'target/js/app/loader.js'
#        outDir: 'target/js/app'
#        options:
#          module: 'amd'
#          sourcemap: false
      test:
        src: ['righttrack/test/**/*.ts']
        html: ['righttrack/test/**/*.html']
        reference: 'righttrack/test/reference.ts'
        outDir: 'target/js/righttrack'
        amdloader: 'target/js/righttrack/test/loader.js'
        options:
          module: 'amd'
          sourcemap: false

    jasmine:
      test:
        host: 'http://127.0.0.1:8000/'
        src: 'target/js/**/*.js'
        options:
          specs: 'test*.js'
          template: require('grunt-template-jasmine-requirejs')
          templateOptions:
            requireConfig:
              baseUrl: 'target/js/'
              deps: ['app/loader', 'test/loader']

    sass:
      public:
        files: [
          'public/css/main.css': 'src/main/sass/main.scss'
        ]

    watch:
      test:
        files: ['righttrack/**/*.ts']
        tasks: ['compile-test', 'run-test']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-ts'

  grunt.registerTask 'default', ['work']

  grunt.registerTask 'init', ['bower']

  grunt.registerTask 'compile-work', ['copy:source', 'ts:app']
  grunt.registerTask 'compile-test', ['ts:test']

  grunt.registerTask 'work-and-watch', ['copy:source', 'ts:watch']
  grunt.registerTask 'test-and-watch', ['compile-test', 'run-test', 'watch:test']

  grunt.registerTask 'run-test', ['jasmine:test']

  grunt.registerTask 'work', ['clean', 'work-and-watch']
  grunt.registerTask 'test', ['clean', 'test-and-watch']
