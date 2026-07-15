import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        surface: {
          950: "#06080d",
          900: "#0d121a",
          850: "#131b26",
          800: "#192333",
          700: "#263247"
        }
      },
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui", "sans-serif"]
      },
      boxShadow: {
        panel: "0 18px 50px rgba(0,0,0,0.28)",
        subtle: "0 1px 0 rgba(255,255,255,0.03) inset"
      },
      spacing: {
        68: "17rem"
      }
    }
  },
  plugins: []
} satisfies Config;
