module.exports = (grunt) ->
  """
  Right Track JavaScript build configuration.

  Test/Work/Release cycle:
  test - Concatonated JavaScript file in the target/js/righttrack directory with test code at the end.
  work - Concatonated and source mapped JavaScript file in the public/js directory.
  release - Optimized and uglified JavaScript file in the public/js directory.

  """

  grunt.initConfig

    pkg: grunt.file.readJSON 'package.json'

    bower:
      public:
        dest: 'public/js/lib'

    clean:
      app: [
        'public/js/app.js'
        'public/js/app.js.map'
        'public/js/app/**/*.js'
        'public/js/app/**/*.js.map'
      ]
      css: [
        'public/css'
      ]
      lib: [
        'public/js/lib'
      ]

    ts:
      options:
        module: 'commonjs'
        sourceMap: true
        sourceRoot: '/typescript/app'
        removeComments: false
      app:
        src: ['public/typescript/app/**/*.ts']
        html: ['public/typescript/app/**/*.html']
        reference: 'public/typescript/app/reference.ts'
        out: 'public/js/app.js'
      test:
        src: ['public/typescript/test/**/*.ts']
        html: ['public/typescript/test/**/*.html']
        reference: 'public/typescript/test/reference.ts'
        out: 'public/js/test.js'

    compass:
      app:
        options:
          sassDir: 'public/sass'
          cssDir: 'public/css'

    jasmine:
      test:
        host: 'http://localhost:8888/'
        src: [
          # angular must be loaded first before any angular plugins
          'public/js/lib/angular.js'
          'public/js/lib/jquery.js'
          # unordered libraries
          'public/js/lib/*'
          # local code
          'public/js/test.js'
        ]
        options:
          outfile: 'public/tests.html'
          keepRunner: true

    watch:
      ts:
        # this config is filled out by the work task
        # using one of the configs in the config settings below
        files: []
        tasks: []
      compass:
        files: ['public/sass/**/*.scss']
        tasks: ['compass']

    work:
      app:
        files: ['public/typescript/app/**/*.ts']
        tasks: ['compile']
      test:
        files: ['public/typescript/**/*.ts']
        tasks: ['test']


  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-compass'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-ts'

  grunt.registerTask 'default', ['work']

  grunt.registerTask 'init', ['clean', 'bower', 'test', 'compass']

  grunt.registerTask 'compile', ['ts:app']
  grunt.registerTask 'test', ['ts:test', 'jasmine:test']

  grunt.registerTask 'work', 'watch all file types for changes', (style, modifier) ->
    """
    Start one of 2 work modes. For the style paramater you can choose:

    1. 'code' or 'app' (default): compile the code, wait for any file changes and recompile
    2. 'tdd' or 'test': run the tests, wait for any file changes and rerun

    All modes will watch stylesheets for changes.

    Adding a ':now' or ':!' will start all tasks immediately (even if nothing has changed).
    """

    # choose one of the config objects registered above, under the 'config' task
    ts_config = switch style
      when '!', 'now'
        # handle modifier as first argument
        modifier = style
        'app'
      when 'app', 'code', undefined then 'app'
      when 'tdd', 'test' then 'test'
      else grunt.fail.warn('Unrecognized work style: ' + style)

    # set any config overrides based on the modifier argument
    watch_task_overrides = {}
    watch_task_overrides.options = {}
    switch modifier
      when 'now', '!'
        # set the grunt:watch config to start all tasks immediately
        watch_task_overrides.options.atBegin = true
      when undefined
        # do nothing
      else grunt.fail.warn('Unrecognized style modifier: ' + modifier)

    # grab the config object
    watch_task_config = grunt.config('work.' + ts_config)
    if not watch_task_config
      grunt.fail.warn('Config object not found: "' + ts_config + '"')
    # override the settings
    grunt.util._.extend(watch_task_config, watch_task_overrides)

    # update the watch.ts config
    grunt.verbose.writeln('watch.ts config set to ' + JSON.stringify(watch_task_config, null, ' '))
    grunt.fail.warn('files required for watch config') if !watch_task_config.files
    grunt.fail.warn('tasks required for watch config') if !watch_task_config.tasks
    grunt.config('watch.ts', watch_task_config)

    # run grunt watch
    grunt.task.run('watch')
