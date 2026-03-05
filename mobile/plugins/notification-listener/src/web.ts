import type { NotificationData } from './definitions';

export class NotificationListenerWeb {
  async requestPermission(): Promise<{ granted: boolean }> {
    console.warn('Notification Listener is not available on web');
    return { granted: false };
  }

  async hasPermission(): Promise<{ granted: boolean }> {
    return { granted: false };
  }

  async addListener(
    _eventName: string,
    _listenerFunc: (data: NotificationData) => void
  ): Promise<{ remove: () => Promise<void> }> {
    return { remove: async () => {} };
  }

  async removeAllListeners(): Promise<void> {}
}
