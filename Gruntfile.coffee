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
#      js:
#        files: [
#          expand: true
#          cwd: 'target/js'
#          dest: 'public/js'
#          src: '**'
#        ]
#      ts:
#        files: [
#          expand: true
#          cwd: 'src/main/typescript'
#          dest: 'public/typescript'
#          src: '**'
#        ]

    ts:
      app:
        src: ['righttrack/app/**/*.ts']
        html: ['righttrack/app/**/*.html']
        reference: 'righttrack/app/reference.ts'
        out: 'public/js/app.js'
        watch: 'righttrack/app'
        options:
          sourcemap: true
          sourceRoot: 'public'
      test:
        src: ['righttrack/test/**/*.ts']
        html: ['righttrack/test/**/*.html']
        outDir: 'target/js'
        options:
          module: 'commonjs'
          sourcemap: false

#      compile:
#        src: ['src/main/typescript/app/**/*.ts']
#        html: ['src/main/typescript/app/**/*.html']
#        reference: 'src/main/typescript/app/reference.ts'
#        out: 'target/js/app.js'
#        options:
#          sourcemap: false
#      test:
#        src: ['src/test/typescript/app/**/*.ts']
#        html: ['src/test/typescript/app/**/*.html']
#        reference: 'src/test/typescript/app/reference.ts'
#        out: 'target/js/tests.js'
#        options:
#          module: "commonjs"
#          sourcemap: false
#      public:
#        src: ['public/typescript/app/**/*.ts']
#        html: ['public/typescript/app/**/*.html']
#        reference: 'public/typescript/app/reference.ts'
#        out: 'public/js/app.js'

    jasmine:
      test:
        host: 'http://127.0.0.1:8000/'
        src: 'target/js/**/*.js'
        options:
          specs: 'test*.js'
#          helpers: '*Helper.js'

    sass:
      public:
        files: [
          'public/css/main.css': 'src/main/sass/main.scss'
        ]

#    uglify:
#      options:
#        banner: '/*! <%= pkg.name %> <%= grunt.template.today("mm/dd/yyyy") %> */\n'
#        mangle: false
#      release:
#        options:
#          sourceMap: (fileName) ->
#            fileName + '.map'
#          sourceMappingURL: (path) ->
#            path.replace(/.*\/(.*)$/, '$1') + '.map'
#          sourceMapPrefix: 2  # public/js
#          sourceMapIn: 'public/js/src/app.js.map'
#        files: [
#          dest: 'public/js/app.js'
#          src: 'public/js/src/app.js'
#        ]

    watch:
      source:
        files: ['righttrack/app/**/*.ts']
        tasks: ['compile-source', 'deploy-source']
#      test:
#        files: ['src/test/typescript/**/*.ts']
#        tasks: ['typescript:build', 'typescript:test', 'jasmine']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-ts'

#  grunt.registerTask 'do-test', ['stage-test', 'jasmine']
#  grunt.registerTask 'test', ['do-test', 'watch:test']

#  grunt.registerTask 'compile', ['ts:compile-source', 'ts:compile-test']
#  grunt.registerTask 'compile-source', ['ts:compile']
#  grunt.registerTask 'compile-test', ['ts:test']
#
#  grunt.registerTask 'build', ['compile-test', 'bower:build']
#  grunt.registerTask 'deploy', ['copy:js', 'bower:public']
#  grunt.registerTask 'deploy-source', ['clean:typescript', 'copy:ts', 'ts:public']
#
#  grunt.registerTask 'test', ['compile-source', 'compile-test', 'jasmine']
#  grunt.registerTask 'work', ['compile-source', 'deploy-source', 'watch']
#  grunt.registerTask 'release', ['build', 'deploy']  # Tests here

  grunt.registerTask 'default', ['work']

  grunt.registerTask 'compile-work', ['copy:work', 'ts:app']
  grunt.registerTask 'compile-test', ['copy:app', 'copy:test', 'ts:test']

  grunt.registerTask 'run-test', ['jasmine:test']

  grunt.registerTask 'work', ['clean', 'compile-work']
  grunt.registerTask 'test', ['clean', 'compile-test', 'run-test']
