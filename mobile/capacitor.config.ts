import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'br.com.finguard',
  appName: 'FinGuard',
  webDir: 'www',
  server: {
    androidScheme: 'https'
  }
};

export default config;
