module.exports = {
  root: true,
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@babel/eslint-parser',
    ecmaVersion: 2020,
    sourceType: 'module',
    requireConfigFile: false,
  },
  env: {
    browser: true,
    node: true,
    es6: true,
  },
  extends: ['plugin:vue/vue3-recommended', 'eslint:recommended'],

  // 升级过渡期：先放宽规则，避免阻塞渐进式迁移。后续可逐步收紧。
  globals: {
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    withDefaults: 'readonly',
    _hmt: 'writable',
  },
  // add your custom rules here
  rules: {
    'vue/multi-word-component-names': 'off',
    'vue/max-attributes-per-line': 'off',
    'vue/singleline-html-element-content-newline': 'off',
    'vue/multiline-html-element-content-newline': 'off',
    'vue/no-v-html': 'off',
    'vue/require-default-prop': 'off',
    'vue/attribute-hyphenation': 'off',
    'vue/v-on-event-hyphenation': 'off',
    'no-console': 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0,
    'no-unused-vars': [
      1,
      {
        vars: 'all',
        args: 'none',
      },
    ],
    'no-eval': 'off',
    'no-extend-native': 'off',
    'no-useless-escape': 0,
    'no-control-regex': 0,
    'prefer-const': 1,
    'semi': [2, 'never'],
    'quotes': [2, 'single', {
      avoidEscape: true,
      allowTemplateLiterals: true
    }],
    'comma-dangle': [2, 'never'],
    'indent': [2, 2, {
      SwitchCase: 1
    }],
    'space-before-function-paren': [2, 'never'],
    'eqeqeq': ['error', 'always', {
      null: 'ignore'
    }],
    'no-trailing-spaces': 2,
    'eol-last': 2,
    'object-curly-spacing': [2, 'always', {
      objectsInObjects: false
    }],
    'array-bracket-spacing': [2, 'never'],
    'arrow-spacing': [2, {
      before: true,
      after: true
    }],
    'block-spacing': [2, 'always'],
    'brace-style': [2, '1tbs', {
      allowSingleLine: true
    }],
    'comma-spacing': [2, {
      before: false,
      after: true
    }],
    'comma-style': [2, 'last'],
    'keyword-spacing': [2, {
      before: true,
      after: true
    }],
    'space-infix-ops': 2,
    'space-before-blocks': [2, 'always'],
    'spaced-comment': [2, 'always', {
      markers: ['global', 'globals', 'eslint', 'eslint-disable', '*package', '!', ',']
    }],
  },
};
