import { NativeModule, requireNativeModule } from 'expo';
import {SecurityCheckReport} from "./SecurityCheckerModule.types";


declare class SecurityCheckerModule extends NativeModule<{
  onChange(report: SecurityCheckReport): void
}> {
  check(): SecurityCheckReport;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<SecurityCheckerModule>('SecurityChecker');
