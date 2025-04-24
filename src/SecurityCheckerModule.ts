import { NativeModule, requireNativeModule } from 'expo';

import { SecurityCheckerModuleEvents } from './SecurityChecker.types';

declare class SecurityCheckerModule extends NativeModule<SecurityCheckerModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<SecurityCheckerModule>('SecurityChecker');
