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
        lift: '0 12px 40px rgba(11, 15, 14, 0.14)',
        rail: '4px 0 24px rgba(11, 15, 14, 0.06)',
      },
      keyframes: {
        rise: {
          '0%': { opacity: '0', transform: 'translateY(16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        drift: {
          '0%, 100%': { transform: 'translate3d(0,0,0) scale(1)' },
          '50%': { transform: 'translate3d(2%, -1%, 0) scale(1.03)' },
        },
        pulseLine: {
          '0%, 100%': { opacity: '0.35' },
          '50%': { opacity: '0.85' },
        },
      },
      animation: {
        rise: 'rise 0.7s cubic-bezier(0.22, 1, 0.36, 1) both',
        'rise-delay': 'rise 0.7s cubic-bezier(0.22, 1, 0.36, 1) 0.12s both',
        'rise-late': 'rise 0.7s cubic-bezier(0.22, 1, 0.36, 1) 0.24s both',
        drift: 'drift 14s ease-in-out infinite',
        'pulse-line': 'pulseLine 3.2s ease-in-out infinite',
      },
    },
  },
  plugins: [],
};
