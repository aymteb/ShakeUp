/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        ink: {
          DEFAULT: '#0B0F0E',
          soft: '#1A2220',
          mute: '#3D4A46',
        },
        paper: {
          DEFAULT: '#F2F5F3',
          raised: '#FFFFFF',
        },
        volt: {
          DEFAULT: '#B8F000',
          dim: '#8FBC00',
        },
        ash: {
          DEFAULT: '#E4EBE7',
          line: '#C5D0CB',
        },
      },
      fontFamily: {
        display: ['Syne', 'system-ui', 'sans-serif'],
        sans: ['Figtree', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        rail: '4px 0 24px rgba(11, 15, 14, 0.06)',
      },
    },
  },
  plugins: [],
};
