module.exports = (grunt) ->

  grunt.initConfig
    pkg: grunt.file.readJSON 'package.json'
    coffeescript:
      files: ['', 'src/**/*.js', 'test/**/*.js']
    concat:
      options:
        separator: ';'
      dist:
        src: ['src/**/*.js'],
        dest: 'dist/<%= pkg.name %>.js'
    uglify:
      options:
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
      dist:
        files:
          'dist/<%= pkg.name %>.min.js': ['<%= concat.dist.dest %>']
    watch:
      files: ['<%= src.files %>']
      tasks: ['jshint', 'qunit']

  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-jshint'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-contrib-concat'

  grunt.registerTask 'test', ['jshint', 'qunit']

  grunt.registerTask 'default', ['jshint', 'qunit', 'concat', 'uglify']