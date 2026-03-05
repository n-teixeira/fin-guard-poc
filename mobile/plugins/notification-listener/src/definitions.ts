import type { PluginListenerHandle } from '@capacitor/core';

export interface NotificationListenerPlugin {
  requestPermission(): Promise<{ granted: boolean }>;
  hasPermission(): Promise<{ granted: boolean }>;
  addListener(
    eventName: 'notificationReceived',
    listenerFunc: (data: NotificationData) => void
  ): Promise<PluginListenerHandle>;
  removeAllListeners(): Promise<void>;
}

export interface NotificationData {
  title?: string;
  text?: string;
  packageName?: string;
  content: string;
}
