/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        ink: {
          DEFAULT: '#111827',
          soft: '#1F2937',
          mute: '#6B7280',
        },
        paper: {
          DEFAULT: '#F4F5F7',
          raised: '#FFFFFF',
        },
        volt: {
          DEFAULT: '#B8F000',
          dim: '#9ACC00',
        },
        ash: {
          DEFAULT: '#EEF0F3',
          line: '#E8EAED',
        },
      },
      fontFamily: {
        display: ['Syne', 'system-ui', 'sans-serif'],
        sans: ['Figtree', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        lift: '0 8px 30px rgba(17, 24, 39, 0.06)',
        rail: '1px 0 0 rgba(229, 231, 235, 1)',
      },
      borderRadius: {
        none: '0',
        DEFAULT: '0',
      },
    },
  },
  plugins: [],
};
