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
