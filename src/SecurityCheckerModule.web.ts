import { registerWebModule, NativeModule } from 'expo';

import { SecurityCheckerModuleEvents } from './SecurityChecker.types';

class SecurityCheckerModule extends NativeModule<SecurityCheckerModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(SecurityCheckerModule);
